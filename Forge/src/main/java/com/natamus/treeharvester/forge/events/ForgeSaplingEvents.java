package com.natamus.treeharvester.forge.events;

import com.natamus.treeharvester.events.SaplingEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeSaplingEvents {
	@SubscribeEvent
	public void onScaffoldingItem(EntityJoinWorldEvent e) {
		SaplingEvents.onSaplingItem(e.getWorld(), e.getEntity());
	}
}