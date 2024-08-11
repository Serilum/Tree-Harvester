package com.natamus.treeharvester.processing;

import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.data.Variables;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import oshi.util.tuples.Triplet;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class SaplingProcessing {
    public static void onSaplingItem(Level level, Player player, ItemStack itemStack, BlockPos leafPos) {
        if (level.isClientSide) {
            return;
        }

        if (!ConfigHandler.replaceSaplingOnTreeHarvest || !ConfigHandler.automaticallyPickupItems) {
            return;
        }

        Block block = Block.byItem(itemStack.getItem());
        BlockPos yZeroPlayerPos = player.getOnPos().atY(0);
//        BlockPos yZeroItemPos = leafPos.atY(0);

        placeSaplings(level, yZeroPlayerPos, itemStack, block);
//        placeSaplings(level, yZeroItemPos, itemStack, block);

        if (itemStack.getCount() > 0) {
            if(!player.addItem(itemStack)) {
                Block.popResource(level, leafPos, itemStack);
            }
        }
    }

    public static void placeSaplings(Level level, BlockPos yZeroItemPos, ItemStack itemStack, Block block) {
        Date now = new Date();
        for (Triplet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>> triplet : Variables.saplingPositions) {
            long ms = (now.getTime()-triplet.getA().getTime());
            if (ms > 2000) {
                Variables.saplingPositions.remove(triplet);
                continue;
            }

            if (BlockPosFunctions.withinDistance(yZeroItemPos, triplet.getB().atY(0), 6)) {
                for (BlockPos lowerLog : triplet.getC()) {
                    if (itemStack.getCount() > 0) {
                        level.setBlock(lowerLog, block.defaultBlockState(), 3);
                        itemStack.shrink(1);
                        triplet.getC().remove(lowerLog);
                    }
                }

                if (triplet.getC().size() == 0) {
                    Variables.saplingPositions.remove(triplet);
                }
            }

            if (itemStack.getCount() == 0) {
                return;
            }
        }
    }
}
