package com.serilum.treeharvester.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.services.Services;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.processing.AxeBlacklist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.List;

public class Util {
	public static boolean isTreeLog(Block block) {
		try {
			return (isWoodLog(block) || isGiantMushroomStemBlock(block) || isTreeRoot(block)) && !block.getName().getString().toLowerCase().contains("stripped");
		}
		catch (IllegalArgumentException ignored) { // Fixes mod incompatibility.
			return false;
		}
	}
	public static boolean isTreeLeaf(Block block) {
		if (block.defaultBlockState().is(BlockTags.LEAVES) || block instanceof LeavesBlock) {
			return true;
		}
		if (ConfigHandler.enableNetherTrees && isNetherTreeLeaf(block)) {
			return true;
		}
		return isGiantMushroomLeafBlock(block);
	}

	// Stone pillars like deepslate and basalt are RotatedPillarBlocks too, so the fallback only accepts ones mined with an axe.
	private static boolean isWoodLog(Block block) {
		BlockState defaultState = block.defaultBlockState();
		if (defaultState.is(BlockTags.LOGS)) {
			return true;
		}
		return block instanceof RotatedPillarBlock && defaultState.is(BlockTags.MINEABLE_WITH_AXE);
	}
	public static boolean isSapling(Block block) {
		return CompareBlockFunctions.isSapling(block) || (block instanceof MushroomBlock && ConfigHandler.enableHugeMushrooms);
	}

	public static boolean isTreeHarvestMode(Player player) {
		if (treeHarvestWithoutSneak(player.level())) {
			return !player.isCrouching();
		}
		return player.isCrouching();
	}

	public static boolean isHoldingHarvestAxe(Player player) {
		if (!mustHoldAxeForTreeHarvest(player.level())) {
			return true;
		}

		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!Services.TOOLFUNCTIONS.isAxe(hand)) {
			return false;
		}
		return !AxeBlacklist.isBlacklisted(hand.getItem());
	}

	// A client predicts the log break speed, so it has to use the server's settings instead of its own config file.
	public static boolean treeHarvestWithoutSneak(Level level) {
		if (level.isClientSide && Variables.receivedServerHarvestConfig) {
			return Variables.serverTreeHarvestWithoutSneak;
		}
		return ConfigHandler.treeHarvestWithoutSneak;
	}

	public static boolean mustHoldAxeForTreeHarvest(Level level) {
		if (level.isClientSide && Variables.receivedServerHarvestConfig) {
			return Variables.serverMustHoldAxeForTreeHarvest;
		}
		return ConfigHandler.mustHoldAxeForTreeHarvest;
	}

	public static boolean increaseHarvestingTimePerLog(Level level) {
		if (level.isClientSide && Variables.receivedServerHarvestConfig) {
			return Variables.serverIncreaseHarvestingTimePerLog;
		}
		return ConfigHandler.increaseHarvestingTimePerLog;
	}

	public static double increasedHarvestingTimePerLogModifier(Level level) {
		if (level.isClientSide && Variables.receivedServerHarvestConfig) {
			return Variables.serverIncreasedHarvestingTimePerLogModifier;
		}
		return ConfigHandler.increasedHarvestingTimePerLogModifier;
	}

	public static boolean hasDurabilityForHarvest(Player player) {
		if (!ConfigHandler.preventAxeBreakingOnTreeHarvest || !ConfigHandler.loseDurabilityPerHarvestedLog || player.isCreative()) {
			return true;
		}
		return !isAtLastDurability(player.getItemInHand(InteractionHand.MAIN_HAND));
	}

	public static boolean isAtLastDurability(ItemStack itemStack) {
		return itemStack.isDamageableItem() && itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1;
	}

	// Falls back to accepting any sapling when the tree type has none, so modded trees with differently named saplings still replant.
	public static boolean isSaplingOfTree(Block sapling, Block treeLeaf) {
		String treeType = getTreeTypeKey(treeLeaf);
		if (getTreeTypeKey(sapling).equals(treeType)) {
			return true;
		}
		return !Variables.treeTypeHasSapling.computeIfAbsent(treeType, Util::treeTypeHasSapling);
	}

	private static boolean treeTypeHasSapling(String treeType) {
		for (Block block : BuiltInRegistries.BLOCK) {
			if (isSapling(block) && getTreeTypeKey(block).equals(treeType)) {
				return true;
			}
		}
		return false;
	}

	private static String getTreeTypeKey(Block block) {
		ResourceLocation identifier = BuiltInRegistries.BLOCK.getKey(block);
		String path = identifier.getPath().replace("flowering_", "");
		for (String suffix : Arrays.asList("_leaves", "_sapling", "_propagule", "_block")) {
			if (path.endsWith(suffix)) {
				path = path.substring(0, path.length() - suffix.length());
				break;
			}
		}
		return identifier.getNamespace() + ":" + path;
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