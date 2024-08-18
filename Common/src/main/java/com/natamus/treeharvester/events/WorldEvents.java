package com.natamus.treeharvester.events;

import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.processing.AxeBlacklist;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldEvents {
	public static void onWorldTick(ServerLevel serverLevel) {
		LeafEvents.onWorldTickLeaves(serverLevel);

		if (ConfigHandler.increaseHarvestingTimePerLog) {
			TreeCutEvents.onWorldTickHarvest(serverLevel);
		}
	}

	public static void onWorldLoad(Level level) {
		AxeBlacklist.attemptProcessingAxeBlacklist(level);
	}
}