package com.natamus.treeharvester;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.fabric.callbacks.CollectiveBlockEvents;
import com.natamus.collective.fabric.callbacks.CollectivePlayerEvents;
import com.natamus.treeharvester.events.LeafEvents;
import com.natamus.treeharvester.events.SaplingEvents;
import com.natamus.treeharvester.events.TreeCutEvents;
import com.natamus.treeharvester.events.WorldEvents;
import com.natamus.treeharvester.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class ModFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		setGlobalConstants();
		ModCommon.init();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		ServerWorldEvents.LOAD.register((MinecraftServer server, ServerLevel level) -> {
			WorldEvents.onWorldLoad(level);
		});

		ServerTickEvents.START_WORLD_TICK.register((ServerLevel level) -> {
			LeafEvents.onWorldTick(level);
		});

		CollectiveBlockEvents.NEIGHBOUR_NOTIFY.register((Level level, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) -> {
			LeafEvents.onNeighbourNotify(level, pos, state, notifiedSides, forceRedstoneUpdate);
			return true;
		});

		ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerLevel world) -> {
			SaplingEvents.onSaplingItem(world, entity);
		});

		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, entity) -> {
			return TreeCutEvents.onTreeHarvest(level, player, pos, state, entity);
		});

		CollectivePlayerEvents.ON_PLAYER_DIG_SPEED_CALC.register((Level level, Player player, float digSpeed, BlockState state) -> {
			return TreeCutEvents.onHarvestBreakSpeed(level, player, digSpeed, state);
		});
	}

	private static void setGlobalConstants() {

	}
}
