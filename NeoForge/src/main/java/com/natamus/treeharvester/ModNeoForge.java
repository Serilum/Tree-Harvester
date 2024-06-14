package com.natamus.treeharvester;

import com.natamus.collective.check.RegisterMod;
import com.natamus.treeharvester.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.treeharvester.neoforge.events.*;
import com.natamus.treeharvester.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModNeoForge {
	
	public ModNeoForge(IEventBus modEventBus) {
		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(NeoForgeLeafEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeSaplingEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeTreeCutEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeWorldEvents.class);

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			NeoForge.EVENT_BUS.register(NeoForgeSoundEvents.class);
		}
	}

	private static void setGlobalConstants() {

	}
}