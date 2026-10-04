package com.serilum.treeharvester.neoforge.events;

import com.serilum.treeharvester.events.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class NeoForgePlayerEvents {
	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		Player player = e.getEntity();
		PlayerEvents.onPlayerLogin(player.level(), player);
	}
}
