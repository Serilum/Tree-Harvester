package com.natamus.treeharvester.forge.events;

import com.natamus.treeharvester.events.SaplingEvents;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeSaplingEvents {
	@SubscribeEvent
	public void onScaffoldingItem(EntityJoinLevelEvent e) {
		SaplingEvents.onSaplingItem(e.getLevel(), e.getEntity());
	}
}