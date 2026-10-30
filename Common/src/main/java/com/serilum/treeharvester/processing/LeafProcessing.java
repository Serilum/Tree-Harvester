package com.serilum.treeharvester.processing;

import com.natamus.collective.functions.BlockPosFunctions;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LeafProcessing {
	public static Block getTreeLeafBlock(Level level, BlockPos highestLogPos, Block logType) {
		BlockPos highestLeafPos = highestLogPos.above().immutable();
		Block highestLeafBlock = level.getBlockState(highestLeafPos).getBlock();
		if (!Util.isTreeLeafOf(highestLeafBlock, logType)) {
			for (BlockPos aroundPos : BlockPosFunctions.getBlocksAround(highestLeafPos, false)) {
				Block aroundBlock = level.getBlockState(aroundPos).getBlock();
				if (Util.isTreeLeafOf(aroundBlock, logType)) {
					highestLeafBlock = aroundBlock;
					break;
				}
			}
		}
		return highestLeafBlock;
	}

	public static void breakTreeLeaves(Level level, List<BlockPos> logsToBreak, BlockPos lowestCenterLogPos, BlockPos highestLogPos, Block highestLeafBlock, ItemStack harvestTool) {
		if (level.isClientSide) {
			return;
		}

		int highestX = Integer.MIN_VALUE; int highestZ = Integer.MIN_VALUE;
		int lowestX = Integer.MAX_VALUE; int lowestZ = Integer.MAX_VALUE;

		for (BlockPos brokenLogPos : logsToBreak) {
			int x = brokenLogPos.getX();
			int z = brokenLogPos.getZ();

			if (x > highestX) {
				highestX = x;
			}
			if (x < lowestX) {
				lowestX = x;
			}
			if (z > highestZ) {
				highestZ = z;
			}
			if (z < lowestZ) {
				lowestZ = z;
			}
		}

		if (!Variables.processBreakLeaves.containsKey(level)) {
			Variables.processBreakLeaves.put(level, new CopyOnWriteArrayList<BlockPos>());
		}

		boolean highestLeafIsMushroom = Util.isGiantMushroomLeafBlock(highestLeafBlock);

		int distance = 3;
		if (Util.isNetherTreeLeaf(highestLeafBlock)) {
			distance = 5;
		}

		List<BlockPos> treeBlocks = new ArrayList<BlockPos>();
		for (BlockPos iPos : BlockPos.betweenClosed(lowestX-distance, lowestCenterLogPos.getY()-1, lowestZ-distance, highestX+distance, highestLogPos.getY()+5, highestZ+distance)) {
			treeBlocks.add(iPos.immutable());
		}
		Collections.shuffle(treeBlocks);

		List<BlockPos> queuedMushroomCaps = new ArrayList<>();
		for (BlockPos treeBlockPos : treeBlocks) {
			if (!level.isLoaded(treeBlockPos)) {
				continue;
			}

			BlockState treeBlockState = level.getBlockState(treeBlockPos);
			Block treeBlock = treeBlockState.getBlock();

			if (!Util.isTreeLeaf(treeBlock) || TreeProcessing.isPersistentLeaf(treeBlockState)) {
				continue;
			}

			if (!highestLeafBlock.equals(treeBlock) && !(Util.isNetherTreeLeaf(highestLeafBlock) && Util.isNetherTreeLeaf(treeBlock))) {
				continue;
			}

			if ((!Util.isGiantMushroomLeafBlock(treeBlock) && !highestLeafIsMushroom)) {
				if (hasLogsWithinDistance(level, treeBlockPos, 2)) {
					continue;
				}
			}

			if (highestLeafIsMushroom) {
				queuedMushroomCaps.add(treeBlockPos);
			}
			queueLeafBreak(level, treeBlockPos, harvestTool);
		}

		if (highestLeafIsMushroom) {
			int capReach = 8;
			BoundingBox capArea = new BoundingBox(lowestX - capReach, lowestCenterLogPos.getY() - 1, lowestZ - capReach, highestX + capReach, highestLogPos.getY() + 5, highestZ + capReach);
			for (BlockPos capPos : getConnectedMushroomCaps(level, queuedMushroomCaps, highestLeafBlock, capArea)) {
				queueLeafBreak(level, capPos, harvestTool);
			}
		}
	}

	private static void queueLeafBreak(Level level, BlockPos leafPos, ItemStack harvestTool) {
		if (!Variables.processBreakLeaves.get(level).contains(leafPos)) {
			Variables.processBreakLeaves.get(level).add(leafPos);
			Variables.leafHarvestTools.put(leafPos, harvestTool);
		}
	}

	private static List<BlockPos> getConnectedMushroomCaps(Level level, List<BlockPos> startCaps, Block capBlock, BoundingBox capArea) {
		List<BlockPos> connectedCaps = new ArrayList<>();
		HashSet<BlockPos> checkedPositions = new HashSet<>(startCaps);
		ArrayDeque<BlockPos> capsToCheckAround = new ArrayDeque<>(startCaps);

		while (!capsToCheckAround.isEmpty()) {
			BlockPos checkPos = capsToCheckAround.poll();
			for (BlockPos aroundPos : BlockPos.betweenClosed(checkPos.offset(-1, -1, -1), checkPos.offset(1, 1, 1))) {
				if (!capArea.isInside(aroundPos) || checkedPositions.contains(aroundPos)) {
					continue;
				}

				BlockPos capPos = aroundPos.immutable();
				checkedPositions.add(capPos);

				if (!level.getBlockState(capPos).getBlock().equals(capBlock) || hasLogsWithinDistance(level, capPos, 2)) {
					continue;
				}

				connectedCaps.add(capPos);
				capsToCheckAround.add(capPos);
			}
		}

		return connectedCaps;
	}

	private static boolean hasLogsWithinDistance(Level level, BlockPos checkPos, int distance) {
		for (BlockPos treePos : BlockPos.betweenClosed(checkPos.getX()-distance, checkPos.getY()-distance, checkPos.getZ()-distance, checkPos.getX()+distance, checkPos.getY()+distance, checkPos.getZ()+distance)) {
			if (Util.isTreeLog(level.getBlockState(treePos).getBlock())) {
				return true;
			}
		}
		return false;
	}
}
