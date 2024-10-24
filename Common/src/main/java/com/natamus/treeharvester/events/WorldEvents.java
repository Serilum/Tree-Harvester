package com.natamus.treeharvester.events;

import com.natamus.treeharvester.processing.AxeBlacklist;
import net.minecraft.world.level.Level;

public class WorldEvents {
	public static void onWorldLoad(Level level) {
		AxeBlacklist.attemptProcessingAxeBlacklist(level);
	}
}