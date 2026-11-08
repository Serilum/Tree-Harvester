package com.serilum.treeharvester;

import com.natamus.collective.features.PlayerPlacedBlocksFeature;
import com.natamus.collective.globalcallbacks.ConfigCallback;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.networking.PacketRegistration;
import com.serilum.treeharvester.util.Reference;
import com.serilum.treeharvester.util.Util;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();
		registerPackets();

		load();
	}

	private static void load() {
		PlayerPlacedBlocksFeature.trackBlocksMatching(blockState -> Util.isTreeLog(blockState.getBlock()));
		Util.moveAxeBlacklistFileIntoConfig();

		ConfigCallback.CONFIG_CHANGED.register((String modid) -> {
			if (!modid.equals(Reference.MOD_ID)) {
				return;
			}

			Variables.harvestConfigChanged = true;
		});
	}

	public static void registerPackets() {
		new PacketRegistration().init();
	}
}
