package com.natamus.treeharvester.events;

import com.natamus.treeharvester.processing.AxeBlacklist;
import net.minecraft.server.level.ServerLevel;

public class WorldEvents {
	public static void onWorldLoad(ServerLevel level) {
		AxeBlacklist.attemptProcessingAxeBlacklist(level);
	}
}