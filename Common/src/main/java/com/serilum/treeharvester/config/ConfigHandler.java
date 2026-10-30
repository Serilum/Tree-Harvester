package com.serilum.treeharvester.config;

import com.natamus.collective.config.DuskConfig;
import com.serilum.treeharvester.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean mustHoldAxeForTreeHarvest = true;
	@Entry(registry = "item", filter = "tools") public static String axeWhitelist = "easy_emerald:*_paxel,piercingpaxels:*_paxel,randomisfits:*_multitool,tetra:modular_double";
	@Entry public static boolean treeHarvestWithoutSneak = false;
	@Entry public static boolean treeHarvestIgnoresSneak = false;
	@Entry public static boolean automaticallyFindBottomBlock = true;
	@Entry public static boolean enableFastLeafDecay = true;
	@Entry public static boolean enableNetherTrees = true;
	@Entry public static boolean enableHugeMushrooms = true;
	@Entry public static boolean ignorePlayerMadeTrees = true;
	@Entry(registry = "block", filter = "logs") public static String logBlockBlacklist = "";
	@Entry public static boolean replaceSaplingOnTreeHarvest = true;
	@Entry public static boolean replaceMushroomOnMushroomHarvest = true;
	@Entry public static boolean loseDurabilityPerHarvestedLog = true;
	@Entry(min = 0.001, max = 1.0) public static double loseDurabilityModifier = 1.0;
	@Entry public static boolean preventAxeBreakingOnTreeHarvest = true;
	@Entry public static boolean increaseExhaustionPerHarvestedLog = true;
	@Entry(min = 0.001, max = 1.0) public static double increaseExhaustionModifier = 1.0;
	@Entry public static boolean increaseHarvestingTimePerLog = true;
	@Entry(min = 0.01, max = 10.0) public static double increasedHarvestingTimePerLogModifier = 0.2;
	@Entry(min = 1, max = 16) public static int amountOfLeavesBrokenPerTick = 5;
	@Entry(min = -1, max = 100000) public static int maxAmountOfLogsBrokenPerHarvest = -1;
	@Entry public static boolean triggerBlockBreakEventPerHarvestedLog = true;

	public static void initConfig() {
		configMetaData.put("mustHoldAxeForTreeHarvest", Arrays.asList(
			"If enabled, tree harvesting only works when a player is holding an axe in the main hand."
		));
		configMetaData.put("axeWhitelist", Arrays.asList(
			"A comma separated list of item ids that can harvest trees too, for modded tools that are not detected as an axe, like paxels and multitools. Use * as a wildcard. An id without a namespace matches items of every mod. Applies after rejoining the world."
		));
		configMetaData.put("treeHarvestWithoutSneak", Arrays.asList(
			"If enabled, tree harvesting works when not holding the sneak button. If disabled it's reversed, and only works when sneaking."
		));
		configMetaData.put("treeHarvestIgnoresSneak", Arrays.asList(
			"If enabled, tree harvesting always works and sneaking has no effect, so a tree can't be broken one log at a time. Overrides 'treeHarvestWithoutSneak'."
		));
		configMetaData.put("automaticallyFindBottomBlock", Arrays.asList(
			"Whether the mod should attempt to find the actual bottom log of the tree and start there. This means you can break a tree in the middle and it will still completely be felled."
		));
		configMetaData.put("enableFastLeafDecay", Arrays.asList(
			"If enabled, the leaves around a broken tree will quickly disappear."
		));
		configMetaData.put("enableNetherTrees", Arrays.asList(
			"If enabled, the warped stem/crimson trees in the nether will also be chopped down quickly."
		));
		configMetaData.put("enableHugeMushrooms", Arrays.asList(
			"If enabled, giant/huge mushrooms will also be chopped down quickly."
		));
		configMetaData.put("ignorePlayerMadeTrees", Arrays.asList(
			"If enabled, trees with leaves placed by players, or trees connected to a player-made build, won't be destroyed."
		));
		configMetaData.put("logBlockBlacklist", Arrays.asList(
			"A comma separated list of block ids that are never harvested as a tree, also when they're detected as a log. Use * as a wildcard. An id without a namespace matches blocks of every mod. For example 'minecraft:jungle_log,*crimson_stem'. Applies after rejoining the world."
		));
		configMetaData.put("replaceSaplingOnTreeHarvest", Arrays.asList(
			"If enabled, automatically replaces the sapling from the drops when a tree is harvested."
		));
		configMetaData.put("replaceMushroomOnMushroomHarvest", Arrays.asList(
			"If enabled, automatically replaces the sapling from the drops when a huge mushroom is harvested and 'enableHugeMushrooms' is enabled."
		));
		configMetaData.put("loseDurabilityPerHarvestedLog", Arrays.asList(
			"If enabled, for every log harvested, the axe held loses durability."
		));
		configMetaData.put("loseDurabilityModifier", Arrays.asList(
			"Here you can set how much durability chopping down a tree should take from the axe. For example if set to 0.1, this means that every 10 logs take 1 durability."
		));
		configMetaData.put("preventAxeBreakingOnTreeHarvest", Arrays.asList(
			"If enabled, harvesting a tree never breaks the axe. The whole tree is still felled, but the axe stops at 1 durability. An axe with 1 durability left can't harvest trees until it's repaired."
		));
		configMetaData.put("increaseExhaustionPerHarvestedLog", Arrays.asList(
			"If enabled, players' exhaustion level increases 0.005 per harvested log (Minecraft's default per broken block) * increaseExhaustionModifier."
		));
		configMetaData.put("increaseExhaustionModifier", Arrays.asList(
			"This determines how much exhaustion should be added to the player per harvested log. By default 0.005 * 1.0."
		));
		configMetaData.put("increaseHarvestingTimePerLog", Arrays.asList(
			"If enabled, harvesting time will increase per existing log in the tree. The amount is determined by 'increasedHarvestingTimePerLogModifier'."
		));
		configMetaData.put("increasedHarvestingTimePerLogModifier", Arrays.asList(
			"How much longer it takes to harvest a tree with 'increaseHarvestingTimePerLog' enabled. The actual speed is: newSpeed = originalSpeed / (1 + (logCount * increasedHarvestingTimePerLogModifier))."
		));
		configMetaData.put("amountOfLeavesBrokenPerTick", Arrays.asList(
			"How many leaves should be broken per tick after a tree has been harvested. Increasing this will speed up the fast leaf decay, but costs more processing power per tick."
		));
		configMetaData.put("maxAmountOfLogsBrokenPerHarvest", Arrays.asList(
			"The maximum amount of logs broken in one harvest. The logs closest to the broken log go first, so a bigger tree is only partly felled. Useful against lag from huge modded trees or forests with connected logs. -1 means no limit."
		));
		configMetaData.put("triggerBlockBreakEventPerHarvestedLog", Arrays.asList(
			"If enabled, every harvested log in a tree triggers a block break event, as if broken by hand. This lets other mods see each log, like skill mods that give experience for chopping or claim mods that protect an area. A log is skipped when another mod cancels its event."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}