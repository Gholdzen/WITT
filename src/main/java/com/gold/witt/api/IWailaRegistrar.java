package com.gold.witt.api;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import java.util.List;

public interface IWailaRegistrar {
    void registerHeadProvider(IWailaDataProvider provider, Class<? extends Block> block);
    void registerBodyProvider(IWailaDataProvider provider, Class<? extends Block> block);
    void registerTailProvider(IWailaDataProvider provider, Class<? extends Block> block);
    void registerStackProvider(IWailaDataProvider provider, Class<? extends Block> block);
    void registerNBTProvider(IWailaNBTProvider provider, Class<? extends net.minecraft.tileentity.TileEntity> te);
    void registerNBTProvider(IWailaEntityNBTProvider provider, Class<? extends net.minecraft.entity.Entity> entity);

    java.util.List<IWailaNBTProvider> getNBTProviders(net.minecraft.tileentity.TileEntity te);
    java.util.List<IWailaEntityNBTProvider> getNBTProviders(net.minecraft.entity.Entity entity);

    void registerHeadProvider(IWailaEntityProvider provider, Class<? extends Entity> entity);
    void registerBodyProvider(IWailaEntityProvider provider, Class<? extends Entity> entity);
    void registerTailProvider(IWailaEntityProvider provider, Class<? extends Entity> entity);
    void registerDecorator(IWailaBlockDecorator decorator, Class<? extends net.minecraft.block.Block> block);
    void registerDecorator(IWailaEntityDecorator decorator, Class<? extends net.minecraft.entity.Entity> entity);

    java.util.List<IWailaBlockDecorator> getDecorators(net.minecraft.block.Block block);
    java.util.List<IWailaEntityDecorator> getDecorators(net.minecraft.entity.Entity entity);


    List<IWailaDataProvider> getHeadProviders(Block block);
    List<IWailaDataProvider> getBodyProviders(Block block);
    List<IWailaDataProvider> getTailProviders(Block block);
    List<IWailaDataProvider> getStackProviders(Block block);

    List<IWailaEntityProvider> getHeadProviders(Entity entity);
    List<IWailaEntityProvider> getBodyProviders(Entity entity);
    List<IWailaEntityProvider> getTailProviders(Entity entity);

    IWailaConfigHandler getConfig();
}
