package com.serilum.treeharvester.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.services.Services;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.HarvestAttempt;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.processing.LeafProcessing;
import com.serilum.treeharvester.processing.TreeProcessing;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
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
		if (level.isClientSide || Variables.isHarvestingTree) {
			return true;
		}

		BlockPos brokenPos = bpos.immutable();
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

		int logcount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos, block);
		if (logcount < 0) {
			return true;
		}

		List<BlockPos> logsToBreak = TreeProcessing.getLogsToBreak(level, bpos, block);
		if (ConfigHandler.ignorePlayerMadeTrees && Util.isConnectedToBuilding(level, logsToBreak)) {
			return true;
		}

		Variables.isHarvestingTree = true;
		try {
			if (!logsToBreak.isEmpty() && !isBreakAllowed(level, player, brokenPos, state)) {
				return false;
			}

			harvestTree(level, player, bpos, brokenPos, block, logsToBreak);
		}
		finally {
			Variables.isHarvestingTree = false;
		}

		return logsToBreak.size() == 0;
	}

	private static void harvestTree(Level level, Player player, BlockPos bpos, BlockPos brokenPos, Block block, List<BlockPos> logsToBreak) {
		BlockPos highestLogPos = bpos.immutable();
		for (BlockPos logpos : logsToBreak) {
			if (logpos.getY() > highestLogPos.getY()) {
				highestLogPos = logpos.immutable();
			}
		}

		Block leafBlock = LeafProcessing.getTreeLeafBlock(level, highestLogPos, block);
		TreeProcessing.prepareSaplingReplant(level, bpos, block, leafBlock);

		int durabilitylosecount = (int)Math.ceil(1.0 / ConfigHandler.loseDurabilityModifier);
		int durabilitystartcount = -1;

		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack harvestTool = hand.copy();
		for (BlockPos logpos : logsToBreak) {
			if (!level.mayInteract(player, logpos)) {
				continue;
			}

			BlockState logState = level.getBlockState(logpos);
			if (!logpos.equals(brokenPos) && !isBreakAllowed(level, player, logpos, logState)) {
				continue;
			}

			if (!level.getBlockState(logpos).equals(logState)) {
				continue;
			}

			if (player.isCreative()) {
				level.removeBlock(logpos, false);
			}
			else {
				BlockFunctions.dropBlock(level, logpos, player, hand);
			}

			if (ConfigHandler.triggerBlockBreakEventPerHarvestedLog) {
				Services.EVENTTRIGGER.triggerAfterBlockBreakEvent(level, player, logpos, logState, null);
			}

			if (!player.isCreative()) {
				player.awardStat(Stats.BLOCK_MINED.get(logState.getBlock()));

				if (ConfigHandler.loseDurabilityPerHarvestedLog) {
					if (durabilitystartcount == -1) {
						durabilitystartcount = durabilitylosecount;
						damageAxe(level, player, hand, logpos, logState);
					}
					else {
						durabilitylosecount -= 1;

						if (durabilitylosecount == 0) {
							damageAxe(level, player, hand, logpos, logState);
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
	}

	private static boolean isBreakAllowed(Level level, Player player, BlockPos pos, BlockState state) {
		if (!ConfigHandler.triggerBlockBreakEventPerHarvestedLog) {
			return true;
		}
		return Services.EVENTTRIGGER.triggerBlockBreakEvent(level, player, pos, state);
	}

	private static void damageAxe(Level level, Player player, ItemStack hand, BlockPos logPos, BlockState logState) {
		if (ConfigHandler.preventAxeBreakingOnTreeHarvest && Util.isAtLastDurability(hand)) {
			return;
		}
		hand.mineBlock(level, logState, logPos, player);
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

		HarvestAttempt attempt = getOrStartHarvestAttempt(level, player, bpos, block);
		if (!attempt.isTreeHarvest || attempt.logCount <= 0) {
			return digSpeed;
		}

		return digSpeed/(1+(attempt.logCount * (float)Util.increasedHarvestingTimePerLogModifier(level)));
	}

	// The harvest mode is locked when a log starts being broken, so toggling sneak halfway cannot skip the extra harvesting time.
	private static HarvestAttempt getOrStartHarvestAttempt(Level level, Player player, BlockPos bpos, Block block) {
		Pair<Level, Player> attemptKey = new Pair<Level, Player>(level, player);

		HarvestAttempt attempt = Variables.harvestAttempts.get(attemptKey);
		if (attempt != null && attempt.pos.equals(bpos) && isRecent(attempt, player)) {
			attempt.lastSeenTick = player.tickCount;
			return attempt;
		}

		boolean isTreeHarvest = Util.isTreeHarvestMode(player) && Util.isHoldingHarvestAxe(player) && Util.hasDurabilityForHarvest(player);

		int logCount = -1;
		if (isTreeHarvest) {
			logCount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos, block);
		}

		attempt = new HarvestAttempt(bpos.immutable(), isTreeHarvest, logCount, player.tickCount);
		Variables.harvestAttempts.keySet().removeIf(key -> key.getSecond().isRemoved());
		Variables.harvestAttempts.put(attemptKey, attempt);
		return attempt;
	}

	// The break speed is checked every tick while a log is being broken, so a longer gap means the player stopped and this is a new attempt.
	private static boolean isRecent(HarvestAttempt attempt, Player player) {
		return player.tickCount - attempt.lastSeenTick <= 2;
	}
}