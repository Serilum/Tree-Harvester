package com.serilum.treeharvester.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.features.PlayerPlacedBlocksFeature;
import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.collective.services.Services;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.HarvestAttempt;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.networking.packets.ToClientHarvestAttemptPacket;
import com.serilum.treeharvester.processing.AxeBlacklist;
import com.serilum.treeharvester.processing.TreeProcessing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Util {
	public static boolean isTreeLog(Block block) {
		if (Variables.blacklistedLogBlocks.contains(block)) {
			return false;
		}

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
	public static boolean isTreeLeafOf(Block leaf, Block log) {
		if (isGiantMushroomStemBlock(log)) {
			return isGiantMushroomLeafBlock(leaf);
		}
		if (isNetherTreeLeaf(leaf)) {
			return ConfigHandler.enableNetherTrees && isNetherStem(log);
		}
		return isTreeLeaf(leaf) && !isGiantMushroomLeafBlock(leaf);
	}

	// Overworld logs burn, so a nether wart or shroomlight ceiling above a wooden build never counts as their leaves.
	private static boolean isNetherStem(Block log) {
		BlockState defaultState = log.defaultBlockState();
		return defaultState.is(BlockTags.LOGS) && !defaultState.is(BlockTags.LOGS_THAT_BURN);
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
		if (treeHarvestIgnoresCrouching(player.level())) {
			return true;
		}
		if (treeHarvestWithoutCrouching(player.level())) {
			return !player.isCrouching();
		}
		return player.isCrouching();
	}

	public static boolean isHoldingHarvestAxe(Player player) {
		if (!mustHoldAxeForTreeHarvest(player.level())) {
			return true;
		}

		ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
		if (!Services.TOOLFUNCTIONS.isAxe(hand) && !Variables.whitelistedAxes.contains(hand.getItem())) {
			return false;
		}
		return !AxeBlacklist.isBlacklisted(hand.getItem());
	}

	// A client predicts the log break speed, so it has to use the server's settings instead of its own config file.
	public static boolean treeHarvestWithoutCrouching(Level level) {
		if (level.isClientSide() && Variables.receivedServerHarvestConfig) {
			return Variables.serverTreeHarvestWithoutCrouching;
		}
		return ConfigHandler.treeHarvestWithoutCrouching;
	}

	public static boolean treeHarvestIgnoresCrouching(Level level) {
		if (level.isClientSide() && Variables.receivedServerHarvestConfig) {
			return Variables.serverTreeHarvestIgnoresCrouching;
		}
		return ConfigHandler.treeHarvestIgnoresCrouching;
	}

	public static boolean mustHoldAxeForTreeHarvest(Level level) {
		if (level.isClientSide() && Variables.receivedServerHarvestConfig) {
			return Variables.serverMustHoldAxeForTreeHarvest;
		}
		return ConfigHandler.mustHoldAxeForTreeHarvest;
	}

	public static boolean increaseHarvestingTimePerLog(Level level) {
		if (level.isClientSide() && Variables.receivedServerHarvestConfig) {
			return Variables.serverIncreaseHarvestingTimePerLog;
		}
		return ConfigHandler.increaseHarvestingTimePerLog;
	}

	public static double increasedHarvestingTimePerLogModifier(Level level) {
		if (level.isClientSide() && Variables.receivedServerHarvestConfig) {
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
		Identifier identifier = BuiltInRegistries.BLOCK.getKey(block);
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
		return block.defaultBlockState().is(BlockTags.WART_BLOCKS) || block.equals(Blocks.SHROOMLIGHT);
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
		return block instanceof HugeMushroomBlock && !block.defaultMapColor().equals(MapColor.WOOL);
	}

	public static boolean shouldDecayLikeVanilla(BlockState leafState) {
		return leafState.getBlock() instanceof LeavesBlock && leafState.hasProperty(LeavesBlock.DISTANCE) && Services.MODLOADER.isModLoaded("immersive_weathering");
	}

	public static void decayLeaf(ServerLevel level, BlockPos leafPos, BlockState leafState) {
		BlockState decayingState = leafState.setValue(LeavesBlock.DISTANCE, LeavesBlock.DECAY_DISTANCE);
		decayingState.randomTick(level, leafPos, level.getRandom());
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
		if (oneIdentifier.equals(twoIdentifier)) {
			return true;
		}

		return shareWoodTypeTag(one, two);
	}

	private static boolean shareWoodTypeTag(Block one, Block two) {
		BlockState twoState = two.defaultBlockState();
		Iterator<TagKey<Block>> oneBlockTags = one.defaultBlockState().tags().iterator();
		while (oneBlockTags.hasNext()) {
			TagKey<Block> tag = oneBlockTags.next();
			if (isWoodTypeTagOf(tag.location().getPath(), one, two) && twoState.is(tag)) {
				return true;
			}
		}

		ItemStack twoStack = new ItemStack(two);
		Iterator<TagKey<Item>> oneItemTags = new ItemStack(one).tags().iterator();
		while (oneItemTags.hasNext()) {
			TagKey<Item> tag = oneItemTags.next();
			if (isWoodTypeTagOf(tag.location().getPath(), one, two) && twoStack.is(tag)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isWoodTypeTagOf(String tagPath, Block one, Block two) {
		if (!tagPath.endsWith("_logs")) {
			return false;
		}

		String woodType = tagPath.substring(0, tagPath.length() - "_logs".length());
		return one.getDescriptionId().contains(woodType) && two.getDescriptionId().contains(woodType);
	}

	public static boolean isPlayerPlacedLog(Level level, BlockPos logPos) {
		return ConfigHandler.ignorePlayerMadeTrees && PlayerPlacedBlocksFeature.isPlacedByPlayer(level, logPos);
	}

	// The harvest mode is locked when a log starts being broken, so crouching or standing up halfway cannot skip the extra harvesting time.
	public static HarvestAttempt getOrStartHarvestAttempt(Level level, Player player, BlockPos bpos, Block block) {
		Pair<Level, Player> attemptKey = new Pair<>(level, player);

		HarvestAttempt attempt = Variables.harvestAttempts.get(attemptKey);
		if (attempt != null && attempt.pos.equals(bpos) && isRecentHarvestAttempt(attempt, player)) {
			attempt.lastSeenTick = player.tickCount;
			return attempt;
		}

		boolean isTreeHarvest = isTreeHarvestMode(player) && isHoldingHarvestAxe(player) && hasDurabilityForHarvest(player) && !isPlayerPlacedLog(level, bpos);

		int logCount = -1;
		if (isTreeHarvest) {
			logCount = TreeProcessing.isTreeAndReturnLogAmount(level, bpos, block);
		}

		attempt = new HarvestAttempt(bpos.immutable(), isTreeHarvest, logCount, player.tickCount);
		Variables.harvestAttempts.keySet().removeIf(key -> key.getSecond().isRemoved());
		Variables.harvestAttempts.put(attemptKey, attempt);
		sendHarvestAttemptToClient(player, attempt);
		return attempt;
	}

	// The break speed is checked every tick while a log is being broken, so a longer gap means the player stopped and this is a new attempt.
	public static boolean isRecentHarvestAttempt(HarvestAttempt attempt, Player player) {
		return player.tickCount - attempt.lastSeenTick <= 2;
	}

	public static void sendHarvestAttemptToClient(Player player, HarvestAttempt attempt) {
		if (player instanceof ServerPlayer serverPlayer) {
			Dispatcher.sendToClient(ToClientHarvestAttemptPacket.fromAttempt(attempt), serverPlayer);
		}
	}

	public static void useServerHarvestAttempt(BlockPos pos, boolean isTreeHarvest, int logCount) {
		for (Map.Entry<Pair<Level, Player>, HarvestAttempt> entry : Variables.harvestAttempts.entrySet()) {
			HarvestAttempt clientAttempt = entry.getValue();
			if (entry.getKey().getFirst().isClientSide() && clientAttempt.pos.equals(pos)) {
				entry.setValue(new HarvestAttempt(pos, isTreeHarvest, logCount, clientAttempt.lastSeenTick));
			}
		}
	}

	public static boolean isConnectedToBuilding(Level level, List<BlockPos> logPositions) {
		for (BlockPos logPos : logPositions) {
			for (Direction direction : Direction.values()) {
				if (isBuildingBlock(level.getBlockState(logPos.relative(direction)))) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isBuildingBlock(BlockState blockState) {
		return blockState.is(BlockTags.PLANKS) || blockState.is(BlockTags.STAIRS) || blockState.is(BlockTags.SLABS) || blockState.is(BlockTags.DOORS) || blockState.is(BlockTags.TRAPDOORS);
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