package com.gold.witt.test;

import com.gold.witt.api.IWailaRegistrar;
import com.gold.witt.api.IWailaRegistrarCallback;
import net.minecraft.block.Block;

public class TestReg implements IWailaRegistrarCallback {
    public void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new TestProvider(), Block.class);
    }
}
