package com.gold.witt.test;

import com.gold.witt.api.IWailaConfigHandler;
import com.gold.witt.api.IWailaDataAccessor;
import com.gold.witt.api.IWailaDataProvider;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TestProvider implements IWailaDataProvider {

    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    public List<String> getWailaHead(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return tip;
    }

    public List<String> getWailaBody(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        tip.add("WITT WailaCompat OK");
        return tip;
    }


    public List<String> getWailaTail(ItemStack stack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return tip;
    }
}
