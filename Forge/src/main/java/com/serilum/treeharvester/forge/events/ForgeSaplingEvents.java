package com.serilum.treeharvester.forge.events;

import com.serilum.treeharvester.events.SaplingEvents;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSaplingEvents {
	@SubscribeEvent
	public static void onScaffoldingItem(EntityJoinLevelEvent e) {
		SaplingEvents.onSaplingItem(e.getLevel(), e.getEntity());
	}
}