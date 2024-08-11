package com.natamus.treeharvester.events;

import com.natamus.treeharvester.config.ConfigHandler;
import com.natamus.treeharvester.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import static com.natamus.treeharvester.processing.SaplingProcessing.placeSaplings;

public class SaplingEvents {
	public static void onSaplingItem(Level level, Entity entity) {
		if (level.isClientSide) {
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

		BlockPos itemPos = itemEntity.blockPosition();
		BlockPos yZeroItemPos = itemPos.atY(0);

		placeSaplings(level, yZeroItemPos, itemStack, block);
	}
}