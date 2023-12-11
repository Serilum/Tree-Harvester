package com.natamus.treeharvester.neoforge.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.treeharvester.util.Reference;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.fml.ModLoadingContext;

public class IntegrateNeoForgeConfig {
	public static void registerScreen(ModLoadingContext modLoadingContext) {
		modLoadingContext.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
			return new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
				return DuskConfig.DuskConfigScreen.getScreen(screen, Reference.MOD_ID);
			});
		});
	}
}
