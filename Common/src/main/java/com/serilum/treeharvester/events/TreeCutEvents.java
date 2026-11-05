package com.serilum.treeharvester.events;

import com.mojang.datafixers.util.Pair;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.HarvestAttempt;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.processing.TreeProcessing;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class TreeCutEvents {
	public static boolean onTreeHarvest(Level level, Player player, BlockPos bpos, BlockState state, BlockEntity blockEntity) {
		if (level.isClientSide() || Variables.isHarvestingTree) {
			return true;
		}

		BlockPos brokenPos = bpos.immutable();
		Pair<Level, Player> attemptKey = new Pair<Level, Player>(level, player);
		HarvestAttempt attempt = Variables.harvestAttempts.remove(attemptKey);

		boolean isTreeHarvest;
		if (attempt != null && attempt.pos.equals(bpos) && Util.isRecentHarvestAttempt(attempt, player)) {
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

		if (Util.isPlayerPlacedLog(level, bpos)) {
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
			if (!logsToBreak.isEmpty() && !TreeProcessing.isBreakAllowed(level, player, brokenPos, state)) {
				return false;
			}

			TreeProcessing.harvestTree(level, player, bpos, brokenPos, block, logsToBreak);
		}
		finally {
			Variables.isHarvestingTree = false;
		}

		return logsToBreak.size() == 0;
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

		HarvestAttempt attempt = Util.getOrStartHarvestAttempt(level, player, bpos, block);
		if (!attempt.isTreeHarvest || attempt.logCount <= 0) {
			return digSpeed;
		}

		return digSpeed/(1+(attempt.logCount * (float)Util.increasedHarvestingTimePerLogModifier(level)));
	}
}
