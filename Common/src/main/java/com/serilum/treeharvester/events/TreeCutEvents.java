package com.serilum.treeharvester.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockFunctions;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.HarvestAttempt;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.processing.LeafProcessing;
import com.serilum.treeharvester.processing.TreeProcessing;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class TreeCutEvents {
	public static boolean onTreeHarvest(Level level, Player player, BlockPos bpos, BlockState state, BlockEntity blockEntity) {
		if (level.isClientSide()) {
			return true;
		}

		Pair<Level, Player> attemptKey = new Pair<Level, Player>(level, player);
		HarvestAttempt attempt = Variables.harvestAttempts.remove(attemptKey);

		boolean isTreeHarvest;
		if (attempt != null && attempt.pos.equals(bpos) && isRecent(attempt, player)) {
			isTreeHarvest = attempt.isTreeHarvest;
		}
		else {
			isTreeHarvest = Util.isTreeHarvestMode(player);
		}

		if (!isTreeHarvest) {
			return true;
		}

		Block block = level.getBlockState(bpos).getBlock();
		if (!Util.isTreeLog(block)) {
			return true;
		}

		if (!Util.isHoldingHarvestAxe(player) || !Util.hasDurabilityForHarvest(player)) {
			return true;
		}

		if (ConfigHandler.automaticallyFindBottomBlock) {
			BlockPos temppos = bpos.immutable();
			while (level.getBlockState(temppos.below()).getBlock().equals(block)) {
				temppos = temppos.below().immutable();
			}

			for (BlockPos belowpos : BlockPos.betweenClosed(temppos.getX()-1, temppos.getY()-1, temppos.getZ()-1, temppos.getX()+1, temppos.getY()-1, temppos.getZ()+1)) {
				if (level.getBlockState(belowpos).getBlock().equals(block)) {
					temppos = belowpos.immutable();
					while (level.getBlockState(temppos.below()).getBlock().equals(block)) {
						temppos = temppos.below().immutable();
					}
					break;
				}
			}

			bpos = temppos.immutable();
		}

		int logcount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos);
		if (logcount < 0) {
			return true;
		}

		List<BlockPos> logsToBreak = TreeProcessing.getLogsToBreak(level, bpos, block);

		BlockPos highestLogPos = bpos.immutable();
		for (BlockPos logpos : logsToBreak) {
			if (logpos.getY() > highestLogPos.getY()) {
				highestLogPos = logpos.immutable();
			}
		}

		Block leafBlock = LeafProcessing.getTreeLeafBlock(level, highestLogPos);
		TreeProcessing.prepareSaplingReplant(level, bpos, block, leafBlock);

		int durabilitylosecount = (int)Math.ceil(1.0 / ConfigHandler.loseDurabilityModifier);
		int durabilitystartcount = -1;

		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack harvestTool = hand.copy();
		for (BlockPos logpos : logsToBreak) {
			BlockFunctions.dropBlock(level, logpos, player, hand);

			if (!player.isCreative()) {
				if (ConfigHandler.loseDurabilityPerHarvestedLog) {
					if (durabilitystartcount == -1) {
						durabilitystartcount = durabilitylosecount;
						damageAxe(hand, player);
					}
					else {
						durabilitylosecount -= 1;

						if (durabilitylosecount == 0) {
							damageAxe(hand, player);
							durabilitylosecount = durabilitystartcount;
						}
					}
				}
				if (ConfigHandler.increaseExhaustionPerHarvestedLog) {
					player.causeFoodExhaustion(0.025F * (float)ConfigHandler.increaseExhaustionModifier);
				}
			}
		}

		if (ConfigHandler.enableFastLeafDecay) {
			LeafProcessing.breakTreeLeaves(level, logsToBreak, bpos, highestLogPos, leafBlock, harvestTool);
		}

		return logsToBreak.size() == 0;
	}

	private static void damageAxe(ItemStack hand, Player player) {
		if (ConfigHandler.preventAxeBreakingOnTreeHarvest && Util.isAtLastDurability(hand)) {
			return;
		}
		hand.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
	}

	public static float onHarvestBreakSpeed(Level level, Player player, float digSpeed, BlockState state) {
		if (!Util.increaseHarvestingTimePerLog(level)) {
			return digSpeed;
		}

		Block block = state.getBlock();
		if (!Util.isTreeLog(block)) {
			return digSpeed;
		}

		BlockPos bpos = null;

		HitResult hitResult = player.pick(20.0D, 0.0F, false);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			bpos = ((BlockHitResult)hitResult).getBlockPos();
		}

		if (bpos == null) {
			return digSpeed;
		}

		HarvestAttempt attempt = getOrStartHarvestAttempt(level, player, bpos);
		if (!attempt.isTreeHarvest || attempt.logCount <= 0) {
			return digSpeed;
		}

		return digSpeed/(1+(attempt.logCount * (float)Util.increasedHarvestingTimePerLogModifier(level)));
	}

	// The harvest mode is locked when a log starts being broken, so toggling sneak halfway cannot skip the extra harvesting time.
	private static HarvestAttempt getOrStartHarvestAttempt(Level level, Player player, BlockPos bpos) {
		Pair<Level, Player> attemptKey = new Pair<Level, Player>(level, player);

		HarvestAttempt attempt = Variables.harvestAttempts.get(attemptKey);
		if (attempt != null && attempt.pos.equals(bpos) && isRecent(attempt, player)) {
			attempt.lastSeenTick = player.tickCount;
			return attempt;
		}

		boolean isTreeHarvest = Util.isTreeHarvestMode(player) && Util.isHoldingHarvestAxe(player) && Util.hasDurabilityForHarvest(player);

		int logCount = -1;
		if (isTreeHarvest) {
			logCount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos);
		}

		attempt = new HarvestAttempt(bpos.immutable(), isTreeHarvest, logCount, player.tickCount);
		Variables.harvestAttempts.put(attemptKey, attempt);
		return attempt;
	}

	// The break speed is checked every tick while a log is being broken, so a longer gap means the player stopped and this is a new attempt.
	private static boolean isRecent(HarvestAttempt attempt, Player player) {
		return player.tickCount - attempt.lastSeenTick <= 2;
	}
}