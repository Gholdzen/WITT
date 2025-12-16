package com.gold.witt.proxy;

import com.gold.witt.client.WittKeyInputHandler;
import com.gold.witt.client.WittKeybinds;
import com.gold.witt.client.WittOverlayHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {

        MinecraftForge.EVENT_BUS.register(new WittOverlayHandler());

        WittKeybinds.init();


        FMLCommonHandler.instance().bus().register(new WittKeyInputHandler());

    }
}
