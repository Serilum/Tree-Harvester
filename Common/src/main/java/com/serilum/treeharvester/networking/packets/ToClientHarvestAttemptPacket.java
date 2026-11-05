package com.serilum.treeharvester.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.serilum.treeharvester.data.HarvestAttempt;
import com.serilum.treeharvester.util.Reference;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ToClientHarvestAttemptPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_harvest_attempt_packet");

	private final BlockPos pos;
	private final boolean isTreeHarvest;
	private final int logCount;

	public ToClientHarvestAttemptPacket(BlockPos posIn, boolean isTreeHarvestIn, int logCountIn) {
		this.pos = posIn;
		this.isTreeHarvest = isTreeHarvestIn;
		this.logCount = logCountIn;
	}

	public static ToClientHarvestAttemptPacket fromAttempt(HarvestAttempt attempt) {
		return new ToClientHarvestAttemptPacket(attempt.pos, attempt.isTreeHarvest, attempt.logCount);
	}

	public static ToClientHarvestAttemptPacket decode(FriendlyByteBuf buf) {
		BlockPos posIn = buf.readBlockPos();
		boolean isTreeHarvestIn = buf.readBoolean();
		int logCountIn = buf.readInt();

		return new ToClientHarvestAttemptPacket(posIn, isTreeHarvestIn, logCountIn);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeBoolean(isTreeHarvest);
		buf.writeInt(logCount);
	}

	public static void handle(PacketContext<ToClientHarvestAttemptPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientHarvestAttemptPacket packet = ctx.message();
			Util.useServerHarvestAttempt(packet.pos, packet.isTreeHarvest, packet.logCount);
		}
	}
}
