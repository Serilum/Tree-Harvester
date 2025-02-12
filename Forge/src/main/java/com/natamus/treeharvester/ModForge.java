package com.natamus.treeharvester;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.treeharvester.forge.config.IntegrateForgeConfig;
import com.natamus.treeharvester.forge.events.*;
import com.natamus.treeharvester.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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

		IEventBus modEventBus = modLoadingContext.getModEventBus();
		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(modLoadingContext);

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(ForgeLeafEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeSaplingEvents.class);
        MinecraftForge.EVENT_BUS.register(ForgeTreeCutEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeWorldEvents.class);

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			MinecraftForge.EVENT_BUS.register(ForgeSoundEvents.class);
		}
	}

	private static void setGlobalConstants() {

	}
}