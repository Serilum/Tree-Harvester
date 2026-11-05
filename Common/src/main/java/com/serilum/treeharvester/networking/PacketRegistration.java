package com.serilum.treeharvester.networking;

import com.natamus.collective.implementations.networking.api.Network;
import com.serilum.treeharvester.networking.packets.ToClientHarvestAttemptPacket;
import com.serilum.treeharvester.networking.packets.ToClientSyncHarvestConfigPacket;

public class PacketRegistration {

	public void init() {
		initClientPackets();
		initServerPackets();
	}

	private void initClientPackets() {
		Network.registerPacket(ToClientSyncHarvestConfigPacket.CHANNEL, ToClientSyncHarvestConfigPacket.class, ToClientSyncHarvestConfigPacket::encode, ToClientSyncHarvestConfigPacket::decode, ToClientSyncHarvestConfigPacket::handle);
		Network.registerPacket(ToClientHarvestAttemptPacket.CHANNEL, ToClientHarvestAttemptPacket.class, ToClientHarvestAttemptPacket::encode, ToClientHarvestAttemptPacket::decode, ToClientHarvestAttemptPacket::handle);
	}

	private void initServerPackets() {

	}
}
