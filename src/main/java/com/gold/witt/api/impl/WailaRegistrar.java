package com.gold.witt.api.impl;

import com.gold.witt.api.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WailaRegistrar implements IWailaRegistrar {

    private final IWailaConfigHandler config = ConfigHandler.INSTANCE;

    private final List<BlockReg> headB = new ArrayList<BlockReg>();
    private final List<BlockReg> bodyB = new ArrayList<BlockReg>();
    private final List<BlockReg> tailB = new ArrayList<BlockReg>();
    private final List<BlockReg> stackB = new ArrayList<BlockReg>();

    private final List<EntityReg> headE = new ArrayList<EntityReg>();
    private final List<EntityReg> bodyE = new ArrayList<EntityReg>();
    private final List<EntityReg> tailE = new ArrayList<EntityReg>();

    private final List<BlockDecReg> decB = new ArrayList<BlockDecReg>();
    private final List<EntityDecReg> decE = new ArrayList<EntityDecReg>();

    private final List<NBTBlockReg> nbtB = new ArrayList<NBTBlockReg>();
    private final List<NBTEntityReg> nbtE = new ArrayList<NBTEntityReg>();

    public IWailaConfigHandler getConfig() {
        return config;
    }

    public void registerHeadProvider(IWailaDataProvider provider, Class<? extends Block> block) {
        if (provider == null || block == null) return;
        headB.add(new BlockReg(block, provider));
    }

    public void registerBodyProvider(IWailaDataProvider provider, Class<? extends Block> block) {
        if (provider == null || block == null) return;
        bodyB.add(new BlockReg(block, provider));
    }

    public void registerTailProvider(IWailaDataProvider provider, Class<? extends Block> block) {
        if (provider == null || block == null) return;
        tailB.add(new BlockReg(block, provider));
    }

    public void registerStackProvider(IWailaDataProvider provider, Class<? extends Block> block) {
        if (provider == null || block == null) return;
        stackB.add(new BlockReg(block, provider));
    }

    public void registerHeadProvider(IWailaEntityProvider provider, Class<? extends Entity> entity) {
        if (provider == null || entity == null) return;
        headE.add(new EntityReg(entity, provider));
    }

    public void registerBodyProvider(IWailaEntityProvider provider, Class<? extends Entity> entity) {
        if (provider == null || entity == null) return;
        bodyE.add(new EntityReg(entity, provider));
    }

    public void registerTailProvider(IWailaEntityProvider provider, Class<? extends Entity> entity) {
        if (provider == null || entity == null) return;
        tailE.add(new EntityReg(entity, provider));
    }

    public void registerDecorator(IWailaBlockDecorator decorator, Class<? extends Block> block) {
        if (decorator == null || block == null) return;
        decB.add(new BlockDecReg(block, decorator));
    }

    public void registerDecorator(IWailaEntityDecorator decorator, Class<? extends Entity> entity) {
        if (decorator == null || entity == null) return;
        decE.add(new EntityDecReg(entity, decorator));
    }

    public List<IWailaBlockDecorator> getDecorators(Block block) {
        if (block == null) return Collections.emptyList();
        ArrayList<IWailaBlockDecorator> out = new ArrayList<IWailaBlockDecorator>();
        Class c = block.getClass();
        for (int i = 0; i < decB.size(); i++) {
            BlockDecReg r = decB.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.d);
        }
        return out;
    }

    public List<IWailaEntityDecorator> getDecorators(Entity entity) {
        if (entity == null) return Collections.emptyList();
        ArrayList<IWailaEntityDecorator> out = new ArrayList<IWailaEntityDecorator>();
        Class c = entity.getClass();
        for (int i = 0; i < decE.size(); i++) {
            EntityDecReg r = decE.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.d);
        }
        return out;
    }

    public void registerNBTProvider(IWailaNBTProvider provider, Class<? extends TileEntity> te) {
        if (provider == null || te == null) return;
        nbtB.add(new NBTBlockReg(te, provider));
    }

    public void registerNBTProvider(IWailaEntityNBTProvider provider, Class<? extends Entity> entity) {
        if (provider == null || entity == null) return;
        nbtE.add(new NBTEntityReg(entity, provider));
    }

    public List<IWailaNBTProvider> getNBTProviders(TileEntity te) {
        if (te == null) return Collections.emptyList();
        ArrayList<IWailaNBTProvider> out = new ArrayList<IWailaNBTProvider>();
        Class c = te.getClass();
        for (int i = 0; i < nbtB.size(); i++) {
            NBTBlockReg r = nbtB.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.p);
        }
        return out;
    }

    public List<IWailaEntityNBTProvider> getNBTProviders(Entity entity) {
        if (entity == null) return Collections.emptyList();
        ArrayList<IWailaEntityNBTProvider> out = new ArrayList<IWailaEntityNBTProvider>();
        Class c = entity.getClass();
        for (int i = 0; i < nbtE.size(); i++) {
            NBTEntityReg r = nbtE.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.p);
        }
        return out;
    }

    public List<IWailaDataProvider> getHeadProviders(Block block) {
        return resolveBlock(block, headB);
    }

    public List<IWailaDataProvider> getBodyProviders(Block block) {
        return resolveBlock(block, bodyB);
    }

    public List<IWailaDataProvider> getTailProviders(Block block) {
        return resolveBlock(block, tailB);
    }

    public List<IWailaDataProvider> getStackProviders(Block block) {
        return resolveBlock(block, stackB);
    }

    public List<IWailaEntityProvider> getHeadProviders(Entity entity) {
        return resolveEntity(entity, headE);
    }

    public List<IWailaEntityProvider> getBodyProviders(Entity entity) {
        return resolveEntity(entity, bodyE);
    }

    public List<IWailaEntityProvider> getTailProviders(Entity entity) {
        return resolveEntity(entity, tailE);
    }

    private static List<IWailaDataProvider> resolveBlock(Block b, List<BlockReg> regs) {
        if (b == null) return Collections.emptyList();
        ArrayList<IWailaDataProvider> out = new ArrayList<IWailaDataProvider>();
        Class c = b.getClass();
        for (int i = 0; i < regs.size(); i++) {
            BlockReg r = regs.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.p);
        }
        return out;
    }

    private static List<IWailaEntityProvider> resolveEntity(Entity e, List<EntityReg> regs) {
        if (e == null) return Collections.emptyList();
        ArrayList<IWailaEntityProvider> out = new ArrayList<IWailaEntityProvider>();
        Class c = e.getClass();
        for (int i = 0; i < regs.size(); i++) {
            EntityReg r = regs.get(i);
            if (r.type.isAssignableFrom(c)) out.add(r.p);
        }
        return out;
    }

    private static final class BlockReg {
        final Class type;
        final IWailaDataProvider p;
        BlockReg(Class type, IWailaDataProvider p) {
            this.type = type;
            this.p = p;
        }
    }

    private static final class EntityReg {
        final Class type;
        final IWailaEntityProvider p;
        EntityReg(Class type, IWailaEntityProvider p) {
            this.type = type;
            this.p = p;
        }
    }

    private static final class BlockDecReg {
        final Class type;
        final IWailaBlockDecorator d;
        BlockDecReg(Class type, IWailaBlockDecorator d) {
            this.type = type;
            this.d = d;
        }
    }

    private static final class EntityDecReg {
        final Class type;
        final IWailaEntityDecorator d;
        EntityDecReg(Class type, IWailaEntityDecorator d) {
            this.type = type;
            this.d = d;
        }
    }

    private static final class NBTBlockReg {
        final Class type;
        final IWailaNBTProvider p;
        NBTBlockReg(Class type, IWailaNBTProvider p) {
            this.type = type;
            this.p = p;
        }
    }

    private static final class NBTEntityReg {
        final Class type;
        final IWailaEntityNBTProvider p;
        NBTEntityReg(Class type, IWailaEntityNBTProvider p) {
            this.type = type;
            this.p = p;
        }
    }
}
