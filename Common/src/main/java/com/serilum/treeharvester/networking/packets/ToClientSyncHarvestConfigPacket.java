package com.serilum.treeharvester.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class ToClientSyncHarvestConfigPacket {
	public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "to_client_sync_harvest_config_packet");

	private final boolean treeHarvestWithoutSneak;
	private final boolean mustHoldAxeForTreeHarvest;
	private final boolean increaseHarvestingTimePerLog;
	private final double increasedHarvestingTimePerLogModifier;

	public ToClientSyncHarvestConfigPacket(boolean treeHarvestWithoutSneakIn, boolean mustHoldAxeForTreeHarvestIn, boolean increaseHarvestingTimePerLogIn, double increasedHarvestingTimePerLogModifierIn) {
		this.treeHarvestWithoutSneak = treeHarvestWithoutSneakIn;
		this.mustHoldAxeForTreeHarvest = mustHoldAxeForTreeHarvestIn;
		this.increaseHarvestingTimePerLog = increaseHarvestingTimePerLogIn;
		this.increasedHarvestingTimePerLogModifier = increasedHarvestingTimePerLogModifierIn;
	}

	public static ToClientSyncHarvestConfigPacket fromServerConfig() {
		return new ToClientSyncHarvestConfigPacket(ConfigHandler.treeHarvestWithoutSneak, ConfigHandler.mustHoldAxeForTreeHarvest, ConfigHandler.increaseHarvestingTimePerLog, ConfigHandler.increasedHarvestingTimePerLogModifier);
	}

	public static ToClientSyncHarvestConfigPacket decode(FriendlyByteBuf buf) {
		boolean treeHarvestWithoutSneakIn = buf.readBoolean();
		boolean mustHoldAxeForTreeHarvestIn = buf.readBoolean();
		boolean increaseHarvestingTimePerLogIn = buf.readBoolean();
		double increasedHarvestingTimePerLogModifierIn = buf.readDouble();

		return new ToClientSyncHarvestConfigPacket(treeHarvestWithoutSneakIn, mustHoldAxeForTreeHarvestIn, increaseHarvestingTimePerLogIn, increasedHarvestingTimePerLogModifierIn);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(treeHarvestWithoutSneak);
		buf.writeBoolean(mustHoldAxeForTreeHarvest);
		buf.writeBoolean(increaseHarvestingTimePerLog);
		buf.writeDouble(increasedHarvestingTimePerLogModifier);
	}

	public static void handle(PacketContext<ToClientSyncHarvestConfigPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSyncHarvestConfigPacket packet = ctx.message();

			Variables.serverTreeHarvestWithoutSneak = packet.treeHarvestWithoutSneak;
			Variables.serverMustHoldAxeForTreeHarvest = packet.mustHoldAxeForTreeHarvest;
			Variables.serverIncreaseHarvestingTimePerLog = packet.increaseHarvestingTimePerLog;
			Variables.serverIncreasedHarvestingTimePerLogModifier = packet.increasedHarvestingTimePerLogModifier;
			Variables.receivedServerHarvestConfig = true;
		}
	}
}
