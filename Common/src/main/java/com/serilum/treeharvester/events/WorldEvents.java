package com.serilum.treeharvester.events;

import com.serilum.treeharvester.processing.AxeBlacklist;
import net.minecraft.world.level.Level;

public class WorldEvents {
	public static void onWorldLoad(Level level) {
		AxeBlacklist.attemptProcessingAxeBlacklist();
	}
}