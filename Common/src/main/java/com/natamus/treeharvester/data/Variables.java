package com.natamus.treeharvester.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Triplet;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Variables {
    public static boolean processedAxeBlacklist = false;

	public static List<Item> allowedAxes = new ArrayList<>();

	public static HashMap<BlockPos, Integer> highestleaf = new HashMap<>();
	public static CopyOnWriteArrayList<Triplet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>>> saplingPositions = new CopyOnWriteArrayList<>();

	public static final HashMap<Level, CopyOnWriteArrayList<BlockPos>> processTickLeaves = new HashMap<>();
	public static final HashMap<Level, CopyOnWriteArrayList<BlockPos>> processBreakLeaves = new HashMap<>();

	public static final HashMap<Pair<ResourceKey<Level>, UUID>, Pair<Date, Integer>> harvestSpeedCache = new HashMap<>();
	public static final HashMap<Player, Boolean> harvestAllowed = new HashMap<>();
}
