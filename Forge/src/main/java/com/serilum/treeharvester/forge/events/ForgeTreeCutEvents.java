package com.serilum.treeharvester.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.serilum.treeharvester.events.TreeCutEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeTreeCutEvents {
	@SubscribeEvent
	public static void onTreeHarvest(BlockEvent.BreakEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}
		
		if (!TreeCutEvents.onTreeHarvest(level, e.getPlayer(), e.getPos(), e.getState(), null)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onHarvestBreakSpeed(PlayerEvent.BreakSpeed e) {
		Player player = e.getEntity();
		Level level = player.level();

		float originalSpeed = e.getOriginalSpeed();
		float newSpeed = TreeCutEvents.onHarvestBreakSpeed(level, player, originalSpeed, e.getState());

		if (originalSpeed != newSpeed) {
			e.setNewSpeed(newSpeed);
		}
	}
}