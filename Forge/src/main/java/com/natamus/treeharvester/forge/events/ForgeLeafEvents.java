package com.natamus.treeharvester.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.treeharvester.events.LeafEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeLeafEvents {
	@SubscribeEvent
	public void onWorldTick(TickEvent.LevelTickEvent e) {
		Level level = e.level;
		if (level.isClientSide || !e.phase.equals(TickEvent.Phase.START)) {
			return;
		}

		LeafEvents.onWorldTick((ServerLevel)level);
	}

	@SubscribeEvent
	public void onNeighbourNotice(BlockEvent.NeighborNotifyEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		LeafEvents.onNeighbourNotify(level, e.getPos(), e.getState(), e.getNotifiedSides(), e.getForceRedstoneUpdate());
	}
}