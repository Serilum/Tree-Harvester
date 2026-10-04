package com.serilum.treeharvester.data;

import net.minecraft.core.BlockPos;

public class HarvestAttempt {
	public final BlockPos pos;
	public final boolean isTreeHarvest;
	public final int logCount;
	public int lastSeenTick;

	public HarvestAttempt(BlockPos pos, boolean isTreeHarvest, int logCount, int lastSeenTick) {
		this.pos = pos;
		this.isTreeHarvest = isTreeHarvest;
		this.logCount = logCount;
		this.lastSeenTick = lastSeenTick;
	}
}
