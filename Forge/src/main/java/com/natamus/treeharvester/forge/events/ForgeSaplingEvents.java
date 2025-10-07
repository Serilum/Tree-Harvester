package com.natamus.treeharvester.forge.events;

import com.natamus.treeharvester.events.SaplingEvents;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeSaplingEvents {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeSaplingEvents.class);

		EntityJoinLevelEvent.BUS.addListener(ForgeSaplingEvents::onScaffoldingItem);
	}

	@SubscribeEvent
	public static void onScaffoldingItem(EntityJoinLevelEvent e) {
		SaplingEvents.onSaplingItem(e.getLevel(), e.getEntity());
	}
}