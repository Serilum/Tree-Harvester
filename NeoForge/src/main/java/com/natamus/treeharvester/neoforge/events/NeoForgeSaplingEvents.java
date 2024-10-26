package com.natamus.treeharvester.neoforge.events;

import com.natamus.treeharvester.events.SaplingEvents;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeSaplingEvents {
	@SubscribeEvent
	public static void onScaffoldingItem(EntityJoinLevelEvent e) {
		SaplingEvents.onSaplingItem(e.getLevel(), e.getEntity());
	}
}