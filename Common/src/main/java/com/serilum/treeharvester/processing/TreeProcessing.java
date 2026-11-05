package com.serilum.treeharvester.processing;

import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.services.Services;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Quartet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TreeProcessing {
	public static int isTreeAndReturnLogAmount(Level level, BlockPos pos, Block logType) {
		int leafcount = 8;
		int logCount = 0;
		int prevleafcount = -1;
		int prevlogCount = -1;

		for (int y = 1; y<=30; y+=1) {
			if (prevleafcount == leafcount && prevlogCount == logCount) {
				break;
			}
			prevleafcount = leafcount;
			prevlogCount = logCount;

			for (BlockPos npos : BlockPos.betweenClosed(pos.getX()-2, pos.getY()+(y-1), pos.getZ()-2, pos.getX()+2, pos.getY()+(y-1), pos.getZ()+2)) {
				BlockState nblockState = level.getBlockState(npos);
				Block nblock = nblockState.getBlock();
				if (Util.isTreeLeafOf(nblock, logType)) {
					if (ConfigHandler.ignorePlayerMadeTrees && isPersistentLeaf(nblockState)) {
						return -1;
					}

					leafcount-=1;
				}
				else if (Util.isTreeLog(nblock) && !Util.isPlayerPlacedLog(level, npos)) {
					logCount+=1;
				}
			}
		}

		if (leafcount < 0) {
			return logCount;
		}
		return -1;
	}

	public static boolean isPersistentLeaf(BlockState blockState) {
		return blockState.hasProperty(LeavesBlock.PERSISTENT) && blockState.getValue(LeavesBlock.PERSISTENT);
	}

	public static void prepareSaplingReplant(Level level, BlockPos pos, Block logType, Block leafType) {
		if (!ConfigHandler.replaceSaplingOnTreeHarvest) {
			return;
		}

		CopyOnWriteArrayList<BlockPos> bottomlogs = new CopyOnWriteArrayList<BlockPos>();
		if (isReplantSoil(level.getBlockState(pos.below()))) {
			Iterator<BlockPos> it = BlockPos.betweenClosedStream(pos.getX()-1, pos.getY(), pos.getZ()-1, pos.getX()+1, pos.getY(), pos.getZ()+1).iterator();
			while (it.hasNext()) {
				BlockPos npos = it.next();
				Block block = level.getBlockState(npos).getBlock();
				if (block.equals(logType) || Util.areEqualLogTypes(logType, block)) {
					bottomlogs.add(npos.immutable());
				}
			}
		}

		Variables.saplingPositions.add(new Quartet<>(new Date(), pos.immutable(), bottomlogs, leafType));
	}

	private static boolean isReplantSoil(BlockState soilState) {
		Block soilBlock = soilState.getBlock();
		return soilState.is(BlockTags.SUPPORTS_VEGETATION) || CompareBlockFunctions.isDirtBlock(soilBlock) || soilBlock instanceof MyceliumBlock;
	}

	public static List<BlockPos> getLogsToBreak(Level level, BlockPos startPos, Block logType) {
		boolean isMangrove = Util.isMangroveRootOrLog(logType);
		int maxLogs = ConfigHandler.maxAmountOfLogsBrokenPerHarvest;
		int lowestY = startPos.getY();

		List<BlockPos> logsToBreak = new ArrayList<>();
		HashSet<BlockPos> foundLogs = new HashSet<>();
		ArrayDeque<BlockPos> logsToCheckAround = new ArrayDeque<>();
		logsToCheckAround.add(startPos.immutable());

		while (!logsToCheckAround.isEmpty()) {
			BlockPos checkPos = logsToCheckAround.poll();
			int downY = checkPos.getY()-1;

			// The second scan two blocks up bridges small gaps in a trunk.
			for (BlockPos scanCenter : List.of(checkPos, checkPos.above(2))) {
				for (BlockPos aroundPos : BlockPos.betweenClosed(scanCenter.getX() - 1, scanCenter.getY() - 1, scanCenter.getZ() - 1, scanCenter.getX() + 1, scanCenter.getY() + 1, scanCenter.getZ() + 1)) {
					if (maxLogs > 0 && logsToBreak.size() >= maxLogs) {
						return logsToBreak;
					}

					if (!ConfigHandler.automaticallyFindBottomBlock && aroundPos.getY() < lowestY) {
						continue;
					}

					if (foundLogs.contains(aroundPos)) {
						continue;
					}

					Block aroundBlock = level.getBlockState(aroundPos).getBlock();
					if (!aroundBlock.equals(logType) && !Util.areEqualLogTypes(logType, aroundBlock)) {
						continue;
					}

					if (Util.isPlayerPlacedLog(level, aroundPos)) {
						continue;
					}

					BlockPos logPos = aroundPos.immutable();
					foundLogs.add(logPos);
					logsToBreak.add(logPos);

					if (!isMangrove || logPos.getY() != downY) {
						logsToCheckAround.add(logPos);
					}
				}
			}
		}

		return logsToBreak;
	}

	public static void harvestTree(Level level, Player player, BlockPos bpos, BlockPos brokenPos, Block block, List<BlockPos> logsToBreak) {
		BlockPos highestLogPos = bpos.immutable();
		for (BlockPos logpos : logsToBreak) {
			if (logpos.getY() > highestLogPos.getY()) {
				highestLogPos = logpos.immutable();
			}
		}

		Block leafBlock = LeafProcessing.getTreeLeafBlock(level, highestLogPos, block);
		prepareSaplingReplant(level, bpos, block, leafBlock);

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

	public static boolean isBreakAllowed(Level level, Player player, BlockPos pos, BlockState state) {
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
}
