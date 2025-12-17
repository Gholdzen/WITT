package com.gold.witt.api;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IWailaEntityProvider {
    List<String> getWailaHead(ItemStack stack, List<String> tip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
    List<String> getWailaBody(ItemStack stack, List<String> tip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
    List<String> getWailaTail(ItemStack stack, List<String> tip, IWailaEntityAccessor accessor, IWailaConfigHandler config);
}
