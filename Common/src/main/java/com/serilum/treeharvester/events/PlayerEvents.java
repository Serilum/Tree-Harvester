package com.serilum.treeharvester.events;

import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.networking.packets.ToClientSyncHarvestConfigPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerEvents {
	public static void onPlayerLogin(Level level, Player player) {
		if (level.isClientSide()) {
			return;
		}

		if (!(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Dispatcher.sendToClient(ToClientSyncHarvestConfigPacket.fromServerConfig(), serverPlayer);
	}

	public static void onWorldTick(ServerLevel level) {
		if (!Variables.harvestConfigChanged) {
			return;
		}

		Variables.harvestConfigChanged = false;
		for (ServerPlayer serverPlayer : level.getServer().getPlayerList().getPlayers()) {
			Dispatcher.sendToClient(ToClientSyncHarvestConfigPacket.fromServerConfig(), serverPlayer);
		}
	}
}
