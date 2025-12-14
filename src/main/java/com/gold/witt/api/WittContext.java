package com.gold.witt.api;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public final class WittContext {
    public final Minecraft mc;
    public final World world;
    public final EntityPlayer player;
    public final MovingObjectPosition mop;

    public WittContext(Minecraft mc, World world, EntityPlayer player, MovingObjectPosition mop) {
        this.mc = mc;
        this.world = world;
        this.player = player;
        this.mop = mop;
    }
}
