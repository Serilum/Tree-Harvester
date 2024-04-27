package com.natamus.treeharvester.neoforge.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.treeharvester.util.Reference;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModLoadingContext;

public class IntegrateNeoForgeConfig {
	public static void registerScreen(ModLoadingContext modLoadingContext) {
		modLoadingContext.registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, screen) -> {
			return DuskConfig.DuskConfigScreen.getScreen(screen, Reference.MOD_ID);
		});
	}
}
