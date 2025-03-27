package com.natamus.treeharvester.forge.events;

import com.natamus.treeharvester.events.SaplingEvents;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSaplingEvents {
	@SubscribeEvent
	public static void onScaffoldingItem(EntityJoinLevelEvent e) {
		SaplingEvents.onSaplingItem(e.getLevel(), e.getEntity());
	}
}