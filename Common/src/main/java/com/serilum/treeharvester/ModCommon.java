package com.serilum.treeharvester;

import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.networking.PacketRegistration;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();
		registerPackets();

		load();
	}

	private static void load() {

	}

	public static void registerPackets() {
		new PacketRegistration().init();
	}
}
