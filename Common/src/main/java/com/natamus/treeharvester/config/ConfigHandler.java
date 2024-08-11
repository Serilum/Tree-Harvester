package com.natamus.treeharvester.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.treeharvester.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static boolean mustHoldAxeForTreeHarvest = true;
	@Entry public static boolean treeHarvestWithoutSneak = false;
	@Entry public static boolean automaticallyFindBottomBlock = true;
	@Entry public static boolean enableFastLeafDecay = true;
	@Entry public static boolean enableNetherTrees = true;
	@Entry public static boolean enableHugeMushrooms = true;
	@Entry public static boolean ignorePlayerMadeTrees = true;
	@Entry public static boolean replaceSaplingOnTreeHarvest = true;
	@Entry public static boolean replaceMushroomOnMushroomHarvest = true;
	@Entry public static boolean loseDurabilityPerHarvestedLog = true;
	@Entry(min = 0.001, max = 1.0) public static double loseDurabilityModifier = 1.0;
	@Entry public static boolean increaseExhaustionPerHarvestedLog = true;
	@Entry(min = 0.001, max = 1.0) public static double increaseExhaustionModifier = 1.0;
	@Entry public static boolean increaseHarvestingTimePerLog = true;
	@Entry(min = 0.01, max = 10.0) public static double increasedHarvestingTimePerLogModifier = 0.2;
	@Entry(min = 1, max = 16) public static int amountOfLeavesBrokenPerTick = 5;
	public static boolean automaticallyPickupItems = false;

	public static void initConfig() {
		configMetaData.put("mustHoldAxeForTreeHarvest", Arrays.asList(
			"If enabled, tree harvesting only works when a player is holding an axe in the main hand."
		));
		configMetaData.put("treeHarvestWithoutSneak", Arrays.asList(
			"If enabled, tree harvesting works when not holding the sneak button. If disabled it's reversed, and only works when sneaking."
		));
		configMetaData.put("automaticallyFindBottomBlock", Arrays.asList(
			"Whether the mod should attempt to find the actual bottom log of the tree and start there. This means you can break a tree in the middle and it will still completely be felled."
		));
		configMetaData.put("enableFastLeafDecay", Arrays.asList(
			"If enabled, the leaves around a broken tree will quickly disappear. Only works with 'instantBreakLeavesAround' disabled."
		));
		configMetaData.put("enableNetherTrees", Arrays.asList(
			"If enabled, the warped stem/crimson trees in the nether will also be chopped down quickly."
		));
		configMetaData.put("enableHugeMushrooms", Arrays.asList(
			"If enabled, giant/huge mushrooms will also be chopped down quickly."
		));
		configMetaData.put("ignorePlayerMadeTrees", Arrays.asList(
			"If enabled, trees with leaves placed by players won't be destroyed."
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
		configMetaData.put("automaticallyPickupItems", Arrays.asList(
			"If enabled, automatically picks up items that are dropped when a tree is harvested including the leaf drops."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}