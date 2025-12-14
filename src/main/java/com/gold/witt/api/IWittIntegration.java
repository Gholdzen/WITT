package com.gold.witt.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IWittIntegration {
    void addLines(WittContext ctx, WittTooltip out);
    boolean renderStack(WittContext ctx, ItemStack stack, int x, int y);
    boolean renderEntity(WittContext ctx, EntityLivingBase entity, int x, int y);
}
