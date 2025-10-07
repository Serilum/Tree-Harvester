package com.natamus.treeharvester.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.treeharvester.config.ConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.List;

public class Util {
	public static boolean isTreeLog(Block block) {
		try {
			return (CompareBlockFunctions.isTreeLog(block) || isGiantMushroomStemBlock(block) || isTreeRoot(block)) && !block.getName().getString().toLowerCase().contains("stripped");
		}
		catch (IllegalArgumentException ignored) { // Fixes mod incompatibility.
			return false;
		}
	}
	public static boolean isTreeLeaf(Block block) {
		return CompareBlockFunctions.isTreeLeaf(block, ConfigHandler.enableNetherTrees) || isGiantMushroomLeafBlock(block);
	}
	public static boolean isSapling(Block block) {
		return CompareBlockFunctions.isSapling(block) || (block instanceof MushroomBlock && ConfigHandler.enableHugeMushrooms);
	}

	public static boolean isNetherTreeLeaf(Block block) {
		return block.equals(Blocks.NETHER_WART_BLOCK) || block.equals(Blocks.WARPED_WART_BLOCK) || block.equals(Blocks.SHROOMLIGHT);
	}
	public static boolean isTreeRoot(Block block) {
		return block instanceof MangroveRootsBlock;
	}

	public static boolean isGiantMushroomStemBlock(Block block) {
		if (!ConfigHandler.enableHugeMushrooms) {
			return false;
		}
		MapColor materialcolour = block.defaultMapColor();
		return block instanceof HugeMushroomBlock && materialcolour.equals(MapColor.WOOL);
	}

	public static boolean isGiantMushroomLeafBlock(Block block) {
		if (!ConfigHandler.enableHugeMushrooms) {
			return false;
		}
		MapColor materialcolour = block.defaultMapColor();
		return block instanceof HugeMushroomBlock && (materialcolour.equals(MapColor.COLOR_RED) || materialcolour.equals(MapColor.DIRT));
	}

	public static boolean isMangroveRootOrLog(Block block) {
		return block instanceof MangroveRootsBlock || block.equals(Blocks.MANGROVE_LOG);
	}

	public static boolean isAzaleaLeaf(Block block) {
		return block.equals(Blocks.AZALEA_LEAVES) || block.equals(Blocks.FLOWERING_AZALEA_LEAVES);
	}

	public static boolean areEqualLogTypes(Block one, Block two) {
		if (!isTreeLog(one) || !isTreeLog(two)) {
			return false;
		}

		if (isMangroveRootOrLog(one) && isMangroveRootOrLog(two)) {
			return true;
		}

		String oneIdentifier = one.getName().getString().split(" ")[0];
		String twoIdentifier = two.getName().getString().split(" ")[0];

		return oneIdentifier.equals(twoIdentifier);
	}

	public static Pair<Boolean, List<BlockPos>> isConnectedToLogs(Level level, BlockPos startpos) {
		List<BlockPos> recursiveList = BlockPosFunctions.getBlocksNextToEachOtherMaterial(level, startpos, Arrays.asList(MapColor.WOOD), 6);
		for (BlockPos connectedpos : recursiveList) {
			Block connectedblock = level.getBlockState(connectedpos).getBlock();
			if (isTreeLog(connectedblock)) {
				return new Pair<Boolean, List<BlockPos>>(true, recursiveList);
			}
		}
		return new Pair<Boolean, List<BlockPos>>(false, recursiveList);
	}
}