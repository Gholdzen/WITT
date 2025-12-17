package com.gold.witt.api;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IWailaDataProvider {
    ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config);
    List<String> getWailaHead(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config);
    List<String> getWailaBody(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config);
    List<String> getWailaTail(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config);
}
