package com.gold.witt.api.impl;

import com.gold.witt.api.IWailaEntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public final class EntityAccessor implements IWailaEntityAccessor {
    private final World world;
    private final EntityPlayer player;
    private final MovingObjectPosition mop;
    private final Entity entity;

    public EntityAccessor(World world, EntityPlayer player, MovingObjectPosition mop, Entity entity) {
        this.world = world;
        this.player = player;
        this.mop = mop;
        this.entity = entity;
    }

    public World getWorld() { return world; }
    public EntityPlayer getPlayer() { return player; }
    public MovingObjectPosition getMOP() { return mop; }
    public Entity getEntity() { return entity; }
}
