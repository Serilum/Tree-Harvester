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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TreeCutEvents {
	public static boolean onTreeHarvest(Level level, Player player, BlockPos bpos, BlockState state, BlockEntity blockEntity) {
		if (level.isClientSide) {
			return true;
		}

		Block block = level.getBlockState(bpos).getBlock();
		if (!Util.isTreeLog(block)) {
			return true;
		}

		if (ConfigHandler.increaseHarvestingTimePerLog) {
			UUID playerUUID = player.getUUID();

			Pair<ResourceKey<Level>, UUID> cachepair = new Pair<ResourceKey<Level>, UUID>(level.dimension(), playerUUID);
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
			} else {
				Variables.harvestSpeedCache.remove(cachepair);
			}

			if (!Variables.harvestAllowed.containsKey(player)) {
				return true;
			}

			if (!Variables.harvestAllowed.get(player)) {
				return true;
			}
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

			BlockFunctions.dropBlock(level, logpos);
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

		LeafProcessing.breakTreeLeaves(level, logsToBreak, bpos, highestLogPos);

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

		UUID playerUUID = player.getUUID();
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
		Pair<ResourceKey<Level>, UUID> keypair = new Pair<ResourceKey<Level>, UUID>(level.dimension(), playerUUID);
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

		if (logcount < 0) {
			logcount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos);
			if (logcount <= 0) {
				return digSpeed;
			}

			Variables.harvestSpeedCache.put(keypair, new Pair<Date, Integer>(now, logcount));
		}

		return digSpeed/(1+(logcount * (float)ConfigHandler.increasedHarvestingTimePerLogModifier));
	}

	public static void startBlockHarvest(Player player, Level level, InteractionHand hand, BlockPos blockPos, Direction direction) {
		if (level.isClientSide) {
			return;
		}

		if (!ConfigHandler.increaseHarvestingTimePerLog) {
			return;
		}

		Block block = level.getBlockState(blockPos).getBlock();
		if (!Util.isTreeLog(block)) {
			return;
		}

		ItemStack handStack = player.getItemInHand(hand);
		Item handitem = handStack.getItem();
		if (ConfigHandler.mustHoldAxeForTreeHarvest) {
			if (!Services.TOOLFUNCTIONS.isAxe(handStack)) {
				return;
			}

			if (!Variables.allowedAxes.contains(handitem)) {
				return;
			}
		}

		if (!Variables.harvestAllowed.containsKey(player)) {
			boolean shouldAllowHarvest = ConfigHandler.treeHarvestWithoutSneak != player.isCrouching();
			Variables.harvestAllowed.put(player, shouldAllowHarvest);
		}
	}

	public static void onWorldTickHarvest(ServerLevel level) {
		for (Player player : Variables.harvestAllowed.keySet()) {
			if (player == null) {
				Variables.harvestAllowed.remove(player);
				continue;
			}

			HitResult hitResult = player.pick(20.0D, 0.0F, false);
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos hitPos = ((BlockHitResult)hitResult).getBlockPos();
				if (!Util.isTreeLog(player.level().getBlockState(hitPos).getBlock())) {
					Variables.harvestAllowed.remove(player);
					return;
				}
			}

			if (ConfigHandler.treeHarvestWithoutSneak) {
				if (player.isCrouching()) {
					Variables.harvestAllowed.put(player, false);
				}
			}
			else {
				if (!player.isCrouching()) {
					Variables.harvestAllowed.put(player, false);
				}
			}
		}
	}
}