package com.gold.witt.api;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IWailaDataAccessor extends IWailaCommonAccessor {
    Block getBlock();
    int getMetadata();
    int getX();
    int getY();
    int getZ();
    TileEntity getTileEntity();
    ItemStack getStack();
}
