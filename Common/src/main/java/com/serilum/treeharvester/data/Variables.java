package com.serilum.treeharvester.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Quartet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Variables {
	public static boolean processedAxeBlacklist = false;

	public static boolean receivedServerHarvestConfig = false;
	public static boolean serverTreeHarvestWithoutSneak = false;
	public static boolean serverMustHoldAxeForTreeHarvest = true;
	public static boolean serverIncreaseHarvestingTimePerLog = true;
	public static double serverIncreasedHarvestingTimePerLogModifier = 0.2;

	public static List<String> blacklistedAxes = new ArrayList<>();
	public static final ConcurrentHashMap<String, Boolean> treeTypeHasSapling = new ConcurrentHashMap<>();

	public static CopyOnWriteArrayList<Quartet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>, Block>> saplingPositions = new CopyOnWriteArrayList<>();

	public static final HashMap<Level, CopyOnWriteArrayList<BlockPos>> processTickLeaves = new HashMap<Level, CopyOnWriteArrayList<BlockPos>>();
	public static final HashMap<Level, CopyOnWriteArrayList<BlockPos>> processBreakLeaves = new HashMap<Level, CopyOnWriteArrayList<BlockPos>>();
	public static final ConcurrentHashMap<BlockPos, ItemStack> leafHarvestTools = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Pair<Level, Player>, HarvestAttempt> harvestAttempts = new ConcurrentHashMap<>();
}
