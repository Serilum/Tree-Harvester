package com.natamus.treeharvester.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.functions.ItemFunctions;
import com.natamus.collective.services.Services;
import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.data.Variables;
import com.natamus.treeharvester.processing.LeafProcessing;
import com.natamus.treeharvester.processing.TreeProcessing;
import com.natamus.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Date;
import java.util.List;

public class TreeCutEvents {
	public static boolean onTreeHarvest(Level level, Player player, BlockPos bpos, BlockState state, BlockEntity blockEntity) {
		if (level.isClientSide) {
			return true;
		}

		Pair<Level, Player> cachepair = new Pair<Level, Player>(level, player);
		if (!Variables.harvestSpeedCache.containsKey(cachepair)) {
			if (ConfigHandler.treeHarvestWithoutSneak) {
				if (player.isCrouching()) {
					return true;
				}
			} else {
				if (!player.isCrouching()) {
					return true;
				}
			}
		}
		else {
			Variables.harvestSpeedCache.remove(cachepair);
		}

		Block block = level.getBlockState(bpos).getBlock();
		if (!Util.isTreeLog(block)) {
			return true;
		}
		
		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		Item handitem = hand.getItem();
		if (ConfigHandler.mustHoldAxeForTreeHarvest) {
			if (!Services.TOOLFUNCTIONS.isAxe(hand)) {
				return true;
			}

			if (!Variables.allowedAxes.contains(handitem)) {
				return true;
			}
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
		
		int durabilitylosecount = (int)Math.ceil(1.0 / ConfigHandler.loseDurabilityModifier);
		int durabilitystartcount = -1;

		ServerPlayer serverPlayer = (ServerPlayer)player;

		BlockPos highestLogPos = bpos.immutable();
		List<BlockPos> logsToBreak = TreeProcessing.getAllLogsToBreak(level, bpos, logcount, block);
		for (BlockPos logpos : logsToBreak) {
			if (logpos.getY() > highestLogPos.getY()) {
				highestLogPos = logpos.immutable();
			}

			BlockState logstate = level.getBlockState(logpos);
			Block log = logstate.getBlock();

			if(ConfigHandler.automaticallyPickupItems) {
				List<ItemStack> drops = Block.getDrops(logstate, (ServerLevel)level, logpos, blockEntity);
				for (ItemStack drop : drops) {
					if(!serverPlayer.addItem(drop)) {
						Block.popResource(level, logpos, drop);
					}
				}
				level.setBlock(logpos, Blocks.AIR.defaultBlockState(), 3);
			} else {
				BlockFunctions.dropBlock(level, logpos);
			}
			//ForgeEventFactory.onEntityDestroyBlock(player, logpos, logstate);

			if (!player.isCreative()) {
				if (ConfigHandler.loseDurabilityPerHarvestedLog) {
					if (durabilitystartcount == -1) {
						durabilitystartcount = durabilitylosecount;
						ItemFunctions.itemHurtBreakAndEvent(hand, serverPlayer, InteractionHand.MAIN_HAND, 1);
					}
					else {
						durabilitylosecount -= 1;

						if (durabilitylosecount == 0) {
							ItemFunctions.itemHurtBreakAndEvent(hand, serverPlayer, InteractionHand.MAIN_HAND, 1);
							durabilitylosecount = durabilitystartcount;
						}
					}
				}
				if (ConfigHandler.increaseExhaustionPerHarvestedLog) {
					player.causeFoodExhaustion(0.025F * (float)ConfigHandler.increaseExhaustionModifier);
				}
			}
		}

		LeafProcessing.breakTreeLeaves(level, player, logsToBreak, bpos, highestLogPos);

		return logsToBreak.size() == 0;
	}

	public static float onHarvestBreakSpeed(Level level, Player player, float digSpeed, BlockState state) {
		if (!ConfigHandler.increaseHarvestingTimePerLog) {
			return digSpeed;
		}

		Block block = state.getBlock();
		if (!Util.isTreeLog(block)) {
			return digSpeed;
		}

		if (ConfigHandler.treeHarvestWithoutSneak) {
			if (player.isCrouching()) {
				return digSpeed;
			}
		}
		else {
			if (!player.isCrouching()) {
				return digSpeed;
			}
		}

		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		Item handitem = hand.getItem();
		if (ConfigHandler.mustHoldAxeForTreeHarvest) {
			if (!Services.TOOLFUNCTIONS.isAxe(hand)) {
				return digSpeed;
			}

			if (!Variables.allowedAxes.contains(handitem)) {
				return digSpeed;
			}
		}

		int logcount = -1;

		Date now = new Date();
		Pair<Level, Player> keypair = new Pair<Level, Player>(level, player);
		if (Variables.harvestSpeedCache.containsKey(keypair)) {
			Pair<Date, Integer> valuepair = Variables.harvestSpeedCache.get(keypair);
			long ms = (now.getTime()-valuepair.getFirst().getTime());

			if (ms < 1000) {
				logcount = valuepair.getSecond();
			}
			else {
				Variables.harvestSpeedCache.remove(keypair);
			}
		}

		BlockPos bpos = null;

		HitResult hitResult = player.pick(20.0D, 0.0F, false);
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			bpos = ((BlockHitResult)hitResult).getBlockPos();
		}

		if (bpos == null) {
			return digSpeed;
		}

		boolean recheck = false;
		if (logcount < 0) {
			if (TreeProcessing.isTreeAndReturnLogAmount(level, bpos) < 0) {
				return digSpeed;
			}

			logcount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos);
			if (logcount == 0) {
				return digSpeed;
			}

			Variables.harvestSpeedCache.put(keypair, new Pair<Date, Integer>(now, logcount));
			recheck = true;
		}

		return digSpeed/(1+(logcount * (float)ConfigHandler.increasedHarvestingTimePerLogModifier));
	}
}