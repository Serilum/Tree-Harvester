package com.natamus.treeharvester.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import oshi.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Variables {
    public static boolean processedAxeBlacklist = false;

	public static List<Item> allowedAxes = new ArrayList<Item>();

	public static HashMap<BlockPos, Integer> highestleaf = new HashMap<BlockPos, Integer>();
	public static CopyOnWriteArrayList<Triplet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>>> saplingPositions = new CopyOnWriteArrayList<Triplet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>>>();

	public static final HashMap<Level, CopyOnWriteArrayList<BlockPos>> processTickLeaves = new HashMap<>();
	public static final HashMap<Level, CopyOnWriteArrayList<Pair<BlockPos, Player>>> processBreakLeaves = new HashMap<>();
	public static final HashMap<Pair<Level, Player>, Pair<Date, Integer>> harvestSpeedCache = new HashMap<>();
}
