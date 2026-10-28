package com.serilum.treeharvester.neoforge.events;

import com.serilum.treeharvester.events.SoundEvents;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class NeoForgeSoundEvents {
	@SubscribeEvent
	public static void onSoundEvent(PlaySoundEvent e) {
		if (!SoundEvents.onSoundEvent(e.getEngine(), e.getOriginalSound())) {
			e.setSound(null);
		}
	}
}
