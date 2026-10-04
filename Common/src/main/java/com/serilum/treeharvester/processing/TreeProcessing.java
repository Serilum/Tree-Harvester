package com.serilum.treeharvester.processing;

import com.natamus.collective.functions.CompareBlockFunctions;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
	public static int isTreeAndReturnLogAmount(Level level, BlockPos pos) {
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
				if (Util.isTreeLeaf(nblock)) {
					if (ConfigHandler.ignorePlayerMadeTrees && isPersistentLeaf(nblockState)) {
						return -1;
					}

					leafcount-=1;
				}
				else if (Util.isTreeLog(nblock)) {
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
		return soilState.is(BlockTags.DIRT) || CompareBlockFunctions.isDirtBlock(soilBlock) || soilBlock instanceof MyceliumBlock;
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
}
