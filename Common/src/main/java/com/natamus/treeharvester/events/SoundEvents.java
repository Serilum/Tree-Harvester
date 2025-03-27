package com.natamus.treeharvester.events;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

import java.util.Date;

public class SoundEvents {
	public static Date lastplayedlog = null;
	public static Date lastplayedleaf = null;
	
	public static boolean onSoundEvent(SoundEngine soundEngine, SoundInstance soundInstance) {
		String name = soundInstance.getLocation().toString();

		if (name.endsWith("block.grass.break") || name.endsWith("block.wood.break")) {
			Date now = new Date();
			Date then;
			
			if (name.equals("block.grass.break")) {
				then = lastplayedleaf;
				lastplayedleaf = now;
			}
			else {
				then = lastplayedlog;
				lastplayedlog = now;
			}
			
			if (then != null) {
				long ms = (now.getTime()-then.getTime());
				return ms >= 10;
			}
		}

		return true;
	}
}
