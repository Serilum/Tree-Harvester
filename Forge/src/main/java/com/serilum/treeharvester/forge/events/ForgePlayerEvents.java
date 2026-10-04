package com.serilum.treeharvester.forge.events;

import com.serilum.treeharvester.events.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

public class ForgePlayerEvents {
	public static void registerEventsInBus() {
		PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ForgePlayerEvents::onPlayerLogin);
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		Player player = e.getEntity();
		PlayerEvents.onPlayerLogin(player.level(), player);
	}
}
