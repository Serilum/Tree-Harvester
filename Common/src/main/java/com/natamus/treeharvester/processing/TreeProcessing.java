package com.natamus.treeharvester.processing;

import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.data.Variables;
import com.natamus.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TreeProcessing {
	public static int isTreeAndReturnLogAmount(Level level, BlockPos pos) {
		Variables.highestleaf.put(pos, 0);

		int leafcount = 8;
		int logCount = 0;
		int prevleafcount = -1;
		int prevlogCount = -1;

		int highesty = 0;
		for (int y = 1; y<=30; y+=1) {
			if (prevleafcount == leafcount && prevlogCount == logCount) {
				break;
			}
			prevleafcount = leafcount;
			prevlogCount = logCount;

			for (BlockPos npos : BlockPos.betweenClosed(pos.getX()-2, pos.getY()+(y-1), pos.getZ()-2, pos.getX()+2, pos.getY()+(y-1), pos.getZ()+2)) {
				BlockState nblockState = level.getBlockState(npos);
				Block nblock = nblockState.getBlock();
				if (CompareBlockFunctions.isTreeLeaf(nblock, ConfigHandler.enableNetherTrees) || Util.isGiantMushroomLeafBlock(nblock)) {
					if (ConfigHandler.ignorePlayerMadeTrees && nblockState.getOptionalValue(LeavesBlock.PERSISTENT).orElse(false)) {
						return -1;
					}

					leafcount-=1;

					if (npos.getY() > highesty) {
						highesty = npos.getY();
					}
				}
				else if (Util.isTreeLog(nblock)) {
					logCount+=1;
				}
			}
		}

		Variables.highestleaf.put(pos.immutable(), highesty);

		if (leafcount < 0) {
			return logCount;
		}
		return -1;
	}

	public static List<BlockPos> getAllLogsToBreak(Level level, BlockPos pos, int logCount, Block logType) {
		CopyOnWriteArrayList<BlockPos> bottomlogs = new CopyOnWriteArrayList<BlockPos>();
		if (ConfigHandler.replaceSaplingOnTreeHarvest) {
			Block blockbelow = level.getBlockState(pos.below()).getBlock();
			if (CompareBlockFunctions.isDirtBlock(blockbelow) || blockbelow instanceof MyceliumBlock) {
				Iterator<BlockPos> it = BlockPos.betweenClosedStream(pos.getX()-1, pos.getY(), pos.getZ()-1, pos.getX()+1, pos.getY(), pos.getZ()+1).iterator();
				while (it.hasNext()) {
					BlockPos npos = it.next();
					Block block = level.getBlockState(npos).getBlock();
					if (block.equals(logType) || Util.areEqualLogTypes(logType, block)) {
						bottomlogs.add(npos.immutable());
					}
				}
			}

			Variables.saplingPositions.add(new Triplet<>(new Date(), pos.immutable(), bottomlogs));
		}

		return getLogsToBreak(level, pos, new ArrayList<BlockPos>(), logCount, logType);
	}

	private static List<BlockPos> getLogsToBreak(Level level, BlockPos pos, List<BlockPos> logsToBreak, int logCount, Block logType) {
		List<BlockPos> checkAround = new ArrayList<BlockPos>();

		boolean isMangrove = Util.isMangroveRootOrLog(logType);
		int downY = pos.getY()-1;

		List<BlockPos> aroundLogs = new ArrayList<BlockPos>();
		for (BlockPos aL : BlockPos.betweenClosed(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)) {
			aroundLogs.add(aL.immutable());
		}

		for (BlockPos aroundLogPos : aroundLogs) {
			if (logsToBreak.contains(aroundLogPos)) {
				continue;
			}

			BlockState logstate = level.getBlockState(aroundLogPos);
			Block logblock = logstate.getBlock();
			if (logblock.equals(logType) || Util.areEqualLogTypes(logType, logblock)) {
				if (!isMangrove || aroundLogPos.getY() != downY) {
					checkAround.add(aroundLogPos);
				}
				logsToBreak.add(aroundLogPos);
			}
		}

		if (checkAround.size() == 0) {
			return logsToBreak;
		}

		for (BlockPos capos : checkAround) {
			for (BlockPos logpos : getLogsToBreak(level, capos, logsToBreak, logCount, logType)) {
				if (!logsToBreak.contains(logpos)) {
					logsToBreak.add(logpos.immutable());
				}
			}
		}

		BlockPos up = pos.above(2);
		return getLogsToBreak(level, up.immutable(), logsToBreak, logCount, logType);
	}
}
