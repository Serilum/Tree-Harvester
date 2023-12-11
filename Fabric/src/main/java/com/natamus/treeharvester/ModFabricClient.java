package com.natamus.treeharvester;

import com.natamus.collective.fabric.callbacks.CollectivePlayerEvents;
import com.natamus.collective.fabric.callbacks.CollectiveSoundEvents;
import com.natamus.treeharvester.events.SoundEvents;
import com.natamus.treeharvester.events.TreeCutEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() { 
		registerEvents();
	}
	
	private void registerEvents() {
		CollectivePlayerEvents.ON_PLAYER_DIG_SPEED_CALC.register((Level world, Player player, float digSpeed, BlockState state) -> {
			return TreeCutEvents.onHarvestBreakSpeed(world, player, digSpeed, state);
		});

		CollectiveSoundEvents.SOUND_PLAY.register((SoundEngine soundEngine, SoundInstance soundInstance) -> {
			return SoundEvents.onSoundEvent(soundEngine, soundInstance);
		});
	}
}
