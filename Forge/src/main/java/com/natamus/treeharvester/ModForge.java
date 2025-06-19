package com.natamus.treeharvester;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.treeharvester.forge.config.IntegrateForgeConfig;
import com.natamus.treeharvester.forge.events.*;
import com.natamus.treeharvester.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		BusGroup busGroup = modLoadingContext.getModBusGroup();
		FMLLoadCompleteEvent.getBus(busGroup).addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(modLoadingContext);

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ForgeLeafEvents.registerEventsInBus();
		ForgeSaplingEvents.registerEventsInBus();
        ForgeTreeCutEvents.registerEventsInBus();
		ForgeWorldEvents.registerEventsInBus();

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			ForgeSoundEvents.registerEventsInBus();
		}
	}

	private static void setGlobalConstants() {

	}
}