package com.gold.witt.api.impl;

import com.gold.witt.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public final class DataAccessor implements IWailaDataAccessor {
    private final World world;
    private final EntityPlayer player;
    private final MovingObjectPosition mop;
    private final Block block;
    private final int meta;
    private final int x;
    private final int y;
    private final int z;
    private final TileEntity te;
    private final ItemStack stack;

    public DataAccessor(World world, EntityPlayer player, MovingObjectPosition mop, Block block, int meta, int x, int y, int z, TileEntity te, ItemStack stack, Object o) {
        this.world = world;
        this.player = player;
        this.mop = mop;
        this.block = block;
        this.meta = meta;
        this.x = x;
        this.y = y;
        this.z = z;
        this.te = te;
        this.stack = stack;
        this.nbt = nbt;
    }

    private NBTTagCompound nbt = null;
    public NBTTagCompound getNBTData() { return nbt; }


    public World getWorld() { return world; }
    public EntityPlayer getPlayer() { return player; }
    public MovingObjectPosition getMOP() { return mop; }

    public Block getBlock() { return block; }
    public int getMetadata() { return meta; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public TileEntity getTileEntity() { return te; }
    public ItemStack getStack() { return stack; }
}
