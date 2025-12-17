package com.gold.witt.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IWailaEntityNBTProvider {
    NBTTagCompound getNBTData(EntityPlayerMP player, Entity entity, NBTTagCompound tag, World world);
}
