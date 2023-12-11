package com.natamus.treeharvester;

import com.natamus.collective.fabric.callbacks.CollectiveClientEvents;
import com.natamus.collective.fabric.callbacks.CollectiveSoundEvents;
import com.natamus.treeharvester.events.SoundEvents;
import com.natamus.treeharvester.events.WorldEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() { 
		registerEvents();
	}
	
	private void registerEvents() {
		CollectiveClientEvents.CLIENT_WORLD_LOAD.register((ClientLevel clientLevel) -> {
			WorldEvents.onWorldLoad(clientLevel);
		});

		CollectiveSoundEvents.SOUND_PLAY.register((SoundEngine soundEngine, SoundInstance soundInstance) -> {
			return SoundEvents.onSoundEvent(soundEngine, soundInstance);
		});
	}
}
