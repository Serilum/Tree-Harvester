package com.serilum.treeharvester.events;

import com.serilum.treeharvester.processing.ConfigLists;
import net.minecraft.world.level.Level;

public class WorldEvents {
	public static void onWorldLoad(Level level) {
		ConfigLists.processConfigLists(level);
	}
}