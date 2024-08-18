package com.natamus.treeharvester.events;

import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.functions.HashMapFunctions;
import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.data.Variables;
import com.natamus.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class LeafEvents {
	public static void onWorldTickLeaves(ServerLevel level) {
		int sizeLeft = HashMapFunctions.computeIfAbsent(Variables.processBreakLeaves, level, k -> new CopyOnWriteArrayList<>()).size() + HashMapFunctions.computeIfAbsent(Variables.processTickLeaves, level, k -> new CopyOnWriteArrayList<>()).size();

		int leavesLeft = ConfigHandler.amountOfLeavesBrokenPerTick;
		if (sizeLeft > 2000) {
			leavesLeft *= 4;
		}
		else if (sizeLeft > 1000) {
			leavesLeft *=3;
		}

		for (BlockPos leafPos : Variables.processBreakLeaves.get(level)) {
			Variables.processBreakLeaves.get(level).remove(leafPos);

			if (Util.isTreeLeaf(level.getBlockState(leafPos).getBlock())) {
				BlockFunctions.dropBlock(level, leafPos);

				leavesLeft--;
				if (leavesLeft < 0) {
					break;
				}
			}
		}

		if (leavesLeft > 0) {
			for (BlockPos leafPos : Variables.processTickLeaves.get(level)) {
				Variables.processTickLeaves.get(level).remove(leafPos);

				BlockState state = level.getBlockState(leafPos);
				if (Util.isTreeLeaf(state.getBlock())) {
					try {
						state.tick(level, leafPos, level.random);
						state.randomTick(level, leafPos, level.random);
					}
					catch (IllegalArgumentException ignored) { }

					leavesLeft--;
					if (leavesLeft < 0) {
						break;
					}
				}
			}
		}
	}

	public static void onNeighbourNotify(Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
		if (level.isClientSide) {
			return;
		}

		if (!state.getBlock().equals(Blocks.AIR)) {
			return;
		}

		ServerLevel serverLevel = (ServerLevel)level;
		for (Direction direction : notifiedSides) {
			BlockPos oPos = pos.relative(direction);
			if (!level.isLoaded(oPos)) {
                continue;
            }

			BlockState oState = level.getBlockState(oPos);
			Block oBlock = oState.getBlock();
			if (CompareBlockFunctions.isTreeLeaf(oBlock, ConfigHandler.enableNetherTrees) || Util.isGiantMushroomLeafBlock(oBlock)) {
				BlockPos addPos = oPos.immutable();
				if (!HashMapFunctions.computeIfAbsent(Variables.processTickLeaves, level, k -> new CopyOnWriteArrayList<>()).contains(addPos)) {
					Variables.processTickLeaves.get(level).add(addPos);
				}
			}
		}
	}
}