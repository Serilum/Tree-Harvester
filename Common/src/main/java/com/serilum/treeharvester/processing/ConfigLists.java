package com.serilum.treeharvester.processing;

import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class ConfigLists {
	public static void processConfigLists(Level level) {
		Registry<Item> itemRegistry = level.registryAccess().lookupOrThrow(Registries.ITEM);
		Set<Item> whitelistedAxes = new HashSet<>();
		for (Item item : itemRegistry) {
			Identifier rl = itemRegistry.getKey(item);
			if (rl != null && Util.isInIdList(ConfigHandler.axeWhitelist, rl.toString())) {
				whitelistedAxes.add(item);
			}
		}

		Registry<Block> blockRegistry = level.registryAccess().lookupOrThrow(Registries.BLOCK);
		Set<Block> blacklistedLogBlocks = new HashSet<>();
		for (Block block : blockRegistry) {
			Identifier rl = blockRegistry.getKey(block);
			if (rl != null && Util.isInIdList(ConfigHandler.logBlockBlacklist, rl.toString())) {
				blacklistedLogBlocks.add(block);
			}
		}

		Variables.whitelistedAxes = whitelistedAxes;
		Variables.blacklistedLogBlocks = blacklistedLogBlocks;
	}
}
