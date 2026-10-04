package com.serilum.treeharvester.events;

import com.natamus.collective.functions.BlockPosFunctions;
import com.serilum.treeharvester.config.ConfigHandler;
import com.serilum.treeharvester.data.Variables;
import com.serilum.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import oshi.util.tuples.Quartet;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class SaplingEvents {
	public static void onSaplingItem(Level level, Entity entity) {
		if (level.isClientSide()) {
			return;
		}

		if (!(entity instanceof ItemEntity)) {
			return;
		}

		if (!ConfigHandler.replaceSaplingOnTreeHarvest) {
			return;
		}

		ItemEntity itemEntity = (ItemEntity)entity;
		ItemStack itemStack = itemEntity.getItem();
		Item item = itemStack.getItem();
		if (!(item instanceof BlockItem)) {
			return;
		}

		Block block = Block.byItem(item);
		if (!Util.isSapling(block)) {
			return;
		}

		if (block instanceof MushroomBlock && !ConfigHandler.replaceMushroomOnMushroomHarvest) {
			return;
		}

		BlockPos itemPos = itemEntity.blockPosition();
		BlockPos yZeroItemPos = itemPos.atY(0);

		Date now = new Date();
		for (Quartet<Date, BlockPos, CopyOnWriteArrayList<BlockPos>, Block> quartet : Variables.saplingPositions) {
			long ms = (now.getTime()-quartet.getA().getTime());
			if (ms > 2000) {
				Variables.saplingPositions.remove(quartet);
				continue;
			}

			if (!Util.isSaplingOfTree(block, quartet.getD())) {
				continue;
			}

			if (BlockPosFunctions.withinDistance(yZeroItemPos, quartet.getB().atY(0), 6)) {
				for (BlockPos lowerLog : quartet.getC()) {
					if (itemStack.getCount() > 0) {
						level.setBlock(lowerLog, block.defaultBlockState(), 3);
						itemStack.shrink(1);
						quartet.getC().remove(lowerLog);
					}
				}

				if (quartet.getC().size() == 0) {
					Variables.saplingPositions.remove(quartet);
				}
			}

			if (itemStack.getCount() == 0) {
				return;
			}
		}
	}
}