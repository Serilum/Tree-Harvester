package com.serilum.treeharvester.forge.events;

import com.serilum.treeharvester.events.SoundEvents;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSoundEvents {
	@SubscribeEvent
	public static void onSoundEvent(PlaySoundEvent e) {
		if (!SoundEvents.onSoundEvent(e.getEngine(), e.getOriginalSound())) {
			e.setSound(null);
		}
	}
}
