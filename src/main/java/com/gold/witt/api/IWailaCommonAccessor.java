package com.gold.witt.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IWailaCommonAccessor {
    World getWorld();
    EntityPlayer getPlayer();
    MovingObjectPosition getMOP();
}
