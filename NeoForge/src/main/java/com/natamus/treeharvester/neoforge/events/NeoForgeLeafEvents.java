package com.natamus.treeharvester.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.treeharvester.events.LeafEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NeoForgeLeafEvents {
	@SubscribeEvent
	public static void onWorldTick(TickEvent.LevelTickEvent e) {
		Level level = e.level;
		if (level.isClientSide || !e.phase.equals(TickEvent.Phase.START)) {
			return;
		}

		LeafEvents.onWorldTick((ServerLevel)level);
	}

	@SubscribeEvent
	public static void onNeighbourNotice(BlockEvent.NeighborNotifyEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		LeafEvents.onNeighbourNotify(level, e.getPos(), e.getState(), e.getNotifiedSides(), e.getForceRedstoneUpdate());
	}
}