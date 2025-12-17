package com.gold.witt.client;

import com.gold.witt.WITT;
import com.gold.witt.api.IWittIntegration;
import com.gold.witt.api.IWailaBlockDecorator;
import com.gold.witt.api.IWailaDataAccessor;
import com.gold.witt.api.IWailaDataProvider;
import com.gold.witt.api.IWailaEntityAccessor;
import com.gold.witt.api.IWailaEntityDecorator;
import com.gold.witt.api.IWailaEntityProvider;
import com.gold.witt.api.IWailaRegistrar;
import com.gold.witt.api.WailaAPI;
import com.gold.witt.api.impl.DataAccessor;
import com.gold.witt.api.impl.EntityAccessor;
import com.gold.witt.api.WittContext;
import com.gold.witt.api.WittTooltip;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WittOverlayHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final RenderBlocks renderBlocks = new RenderBlocks();
    private final Map<String, SaplingGrowth> saplingGrowthMap = new HashMap<String, SaplingGrowth>();

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop == null) return;

        TargetInfo info = null;

        if (mop.typeOfHit == MovingObjectType.BLOCK) {
            info = getBlockInfo(mc.theWorld, mop);
        } else if (mop.typeOfHit == MovingObjectType.ENTITY) {
            info = getEntityInfo(mop);
        }

        if (info == null || info.displayName == null || info.displayName.isEmpty()) return;

        WittContext ctx = new WittContext(mc, mc.theWorld, mc.thePlayer, mop);
        WittTooltip tip = new WittTooltip();

        IWailaRegistrar reg = WailaAPI.instance();
        if (reg != null) {
            if (mop.typeOfHit == MovingObjectType.BLOCK) {
                int bx = mop.blockX;
                int by = mop.blockY;
                int bz = mop.blockZ;

                Block b = mc.theWorld.getBlock(bx, by, bz);
                if (b != null) {
                    int meta = mc.theWorld.getBlockMetadata(bx, by, bz);
                    TileEntity te = mc.theWorld.getTileEntity(bx, by, bz);

                    IWailaDataAccessor acc = new DataAccessor(mc.theWorld, mc.thePlayer, mop, b, meta, bx, by, bz, te, info.stack, null);


                    List<IWailaBlockDecorator> decs = reg.getDecorators(b);
                    for (int i = 0; i < decs.size(); i++) {
                        IWailaBlockDecorator d = decs.get(i);
                        if (d == null) continue;
                        try {
                            d.decorateBlock(acc, reg.getConfig());
                        } catch (Throwable ignored) {
                        }
                    }

                    List<IWailaDataProvider> stackPs = reg.getStackProviders(b);
                    for (int i = 0; i < stackPs.size(); i++) {
                        IWailaDataProvider p = stackPs.get(i);
                        if (p == null) continue;
                        try {
                            ItemStack ov = p.getWailaStack(acc, reg.getConfig());
                            if (ov != null) {
                                info.stack = ov;
                                acc = new DataAccessor(mc.theWorld, mc.thePlayer, mop, b, meta, bx, by, bz, te, info.stack, null);
                                break;
                            }
                        } catch (Throwable ignored) {
                        }
                    }

                    List<String> head = new ArrayList<String>();
                    List<IWailaDataProvider> headPs = reg.getHeadProviders(b);
                    for (int i = 0; i < headPs.size(); i++) {
                        IWailaDataProvider p = headPs.get(i);
                        if (p == null) continue;
                        try {
                            head = p.getWailaHead(info.stack, head, acc, reg.getConfig());
                        } catch (Throwable ignored) {
                        }
                    }
                    if (!head.isEmpty()) info.displayName = String.valueOf(head.get(0));
                    for (int i = 1; i < head.size(); i++) tip.add(String.valueOf(head.get(i)), 0xFFFFFF);

                    List<String> body = new ArrayList<String>();
                    List<IWailaDataProvider> bodyPs = reg.getBodyProviders(b);
                    for (int i = 0; i < bodyPs.size(); i++) {
                        IWailaDataProvider p = bodyPs.get(i);
                        if (p == null) continue;
                        try {
                            body = p.getWailaBody(info.stack, body, acc, reg.getConfig());
                        } catch (Throwable ignored) {
                        }
                    }
                    for (int i = 0; i < body.size(); i++) tip.add(String.valueOf(body.get(i)), 0xFFFFFF);

                    List<String> tail = new ArrayList<String>();
                    List<IWailaDataProvider> tailPs = reg.getTailProviders(b);
                    for (int i = 0; i < tailPs.size(); i++) {
                        IWailaDataProvider p = tailPs.get(i);
                        if (p == null) continue;
                        try {
                            tail = p.getWailaTail(info.stack, tail, acc, reg.getConfig());
                        } catch (Throwable ignored) {
                        }
                    }
                    for (int i = 0; i < tail.size(); i++) tip.add(String.valueOf(tail.get(i)), 0xFFFFFF);
                }
            } else if (mop.typeOfHit == MovingObjectType.ENTITY && mop.entityHit != null) {
                IWailaEntityAccessor eacc = new EntityAccessor(mc.theWorld, mc.thePlayer, mop, mop.entityHit);

                List<IWailaEntityDecorator> decs = reg.getDecorators(mop.entityHit);
                for (int i = 0; i < decs.size(); i++) {
                    IWailaEntityDecorator d = decs.get(i);
                    if (d == null) continue;
                    try {
                        d.decorateEntity(eacc, reg.getConfig());
                    } catch (Throwable ignored) {
                    }
                }

                List<String> head = new ArrayList<String>();
                List<IWailaEntityProvider> headPs = reg.getHeadProviders(mop.entityHit);
                for (int i = 0; i < headPs.size(); i++) {
                    IWailaEntityProvider p = headPs.get(i);
                    if (p == null) continue;
                    try {
                        head = p.getWailaHead(info.stack, head, eacc, reg.getConfig());
                    } catch (Throwable ignored) {
                    }
                }
                if (!head.isEmpty()) info.displayName = String.valueOf(head.get(0));
                for (int i = 1; i < head.size(); i++) tip.add(String.valueOf(head.get(i)), 0xFFFFFF);

                List<String> body = new ArrayList<String>();
                List<IWailaEntityProvider> bodyPs = reg.getBodyProviders(mop.entityHit);
                for (int i = 0; i < bodyPs.size(); i++) {
                    IWailaEntityProvider p = bodyPs.get(i);
                    if (p == null) continue;
                    try {
                        body = p.getWailaBody(info.stack, body, eacc, reg.getConfig());
                    } catch (Throwable ignored) {
                    }
                }
                for (int i = 0; i < body.size(); i++) tip.add(String.valueOf(body.get(i)), 0xFFFFFF);

                List<String> tail = new ArrayList<String>();
                List<IWailaEntityProvider> tailPs = reg.getTailProviders(mop.entityHit);
                for (int i = 0; i < tailPs.size(); i++) {
                    IWailaEntityProvider p = tailPs.get(i);
                    if (p == null) continue;
                    try {
                        tail = p.getWailaTail(info.stack, tail, eacc, reg.getConfig());
                    } catch (Throwable ignored) {
                    }
                }
                for (int i = 0; i < tail.size(); i++) tip.add(String.valueOf(tail.get(i)), 0xFFFFFF);
            }
        }

        IWittIntegration[] integrations = WITT.getWittIntegrations();
        if (integrations != null) {
            for (int i = 0; i < integrations.length; i++) {
                try {
                    IWittIntegration integ = integrations[i];
                    if (integ != null) integ.addLines(ctx, tip);
                } catch (Throwable ignored) {
                }
            }
        }

        if (info.displayName == null || info.displayName.isEmpty()) return;

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        int centerX = width / 2;
        int y = 4;
        int fontH = mc.fontRenderer.FONT_HEIGHT;

        drawCentered(centerX, y, info.displayName, 0xFFFFFF);
        y += fontH + 2;

        if (info.entity != null && info.maxHealth > 0f) {
            drawCentered(centerX, y, "HP: " + (int) info.health + " / " + (int) info.maxHealth, 0xFF5555);
            y += fontH + 2;
        }

        if (info.growthPercent >= 0) {
            drawCentered(centerX, y, "Growth: " + info.growthPercent + "%", 0x66FF66);
            y += fontH + 2;
        }

        if (WittConfig.showModId && info.prettyModName != null && info.fullId != null) {
            drawCentered(centerX, y, info.prettyModName + " (" + info.fullId + ")", 0xAAAAAA);
            y += fontH + 2;
        }

        if (WittConfig.showHarvestInfo && info.hasHarvestInfo) {
            String harvestLine = info.harvestable ? "✔ Harvest" : "✘ Harvest";
            int harvestColor = info.harvestable ? 0x55FF55 : 0xFF5555;
            drawCentered(centerX, y, harvestLine, harvestColor);
            y += fontH + 2;

            drawCentered(centerX, y, "Tool: " + info.harvestTool + " (" + info.harvestLevelName + ")", 0xCCCCCC);
            y += fontH + 2;
        }

        if (info.stack != null && info.stack.getItem() instanceof ItemBlock) {
            Block b = Block.getBlockFromItem(info.stack.getItem());
            if (b instanceof BlockChest) {
                drawCentered(centerX, y, "isDoubleChest: " + (info.isDoubleChest ? "True" : "False"), 0xCCCCCC);
                y += fontH + 2;
            }
        }

        for (int i = 0; i < tip.lines().size(); i++) {
            WittTooltip.Line line = tip.lines().get(i);
            drawCentered(centerX, y, line.text, line.color);
            y += fontH + 2;
        }

        int previewX;
        int previewY;

        switch (WittConfig.overlayCorner) {
            case TOP_RIGHT:
                previewX = width - 24;
                previewY = 32;
                break;
            case BOTTOM_LEFT:
                previewX = 24;
                previewY = height - 32;
                break;
            case BOTTOM_RIGHT:
                previewX = width - 24;
                previewY = height - 32;
                break;
            case TOP_LEFT:
            default:
                previewX = 24;
                previewY = 32;
                break;
        }

        boolean handled = false;

        if (integrations != null) {
            for (int i = 0; i < integrations.length; i++) {
                try {
                    IWittIntegration integ = integrations[i];
                    if (integ == null) continue;

                    if (info.entity != null && WittConfig.showEntityOverlay) {
                        if (integ.renderEntity(ctx, info.entity, previewX, previewY)) {
                            handled = true;
                            break;
                        }
                    } else if (info.stack != null && info.stack.getItem() != null && WittConfig.showBlockOverlay) {
                        if (integ.renderStack(ctx, info.stack, previewX, previewY)) {
                            handled = true;
                            break;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        if (!handled) {
            if (info.entity != null && WittConfig.showEntityOverlay) {
                renderSpinningEntity(info.entity, previewX, previewY);
            } else if (info.stack != null && info.stack.getItem() != null && WittConfig.showBlockOverlay) {
                renderSpinningBlock(info.stack, previewX, previewY, info);
            }
        }
    }

    private void drawCentered(int centerX, int y, String text, int color) {
        int textWidth = mc.fontRenderer.getStringWidth(text);
        int x = centerX - textWidth / 2;
        GL11.glPushMatrix();
        GL11.glScalef(0.9F, 0.9F, 1F);
        mc.fontRenderer.drawStringWithShadow(text, (int) (x / 0.9F), (int) (y / 0.9F), color);
        GL11.glPopMatrix();
    }

    private TargetInfo getBlockInfo(World world, MovingObjectPosition mop) {
        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        Block block = world.getBlock(x, y, z);
        if (block == null) return null;

        int meta = world.getBlockMetadata(x, y, z);

        if (block instanceof BlockDoublePlant) {
            if ((meta & 8) != 0) {
                y--;
                block = world.getBlock(x, y, z);
                if (block == null) return null;
                meta = world.getBlockMetadata(x, y, z);
            }
            meta = meta & 7;
        }

        ItemStack stack = null;
        String displayName;

        try {
            stack = block.getPickBlock(mop, world, x, y, z);
        } catch (Throwable t) {
            stack = null;
        }

        if (stack != null && stack.getItem() != null) {
            displayName = stack.getDisplayName();
        } else {
            Item item = Item.getItemFromBlock(block);
            if (item != null) {
                stack = new ItemStack(item, 1, meta);
                displayName = stack.getDisplayName();
            } else {
                displayName = block.getLocalizedName();
            }
        }

        TargetInfo info = new TargetInfo();
        info.displayName = displayName;
        info.stack = stack;
        info.growthPercent = -1;

        info.isDoubleChest = false;
        info.doubleChestAlongX = false;

        if (block instanceof BlockChest) {
            boolean xChest = false;
            boolean zChest = false;

            if (world.getBlock(x - 1, y, z) instanceof BlockChest) xChest = true;
            if (world.getBlock(x + 1, y, z) instanceof BlockChest) xChest = true;
            if (world.getBlock(x, y, z - 1) instanceof BlockChest) zChest = true;
            if (world.getBlock(x, y, z + 1) instanceof BlockChest) zChest = true;

            info.isDoubleChest = xChest || zChest;
            info.doubleChestAlongX = xChest;
        }

        GameRegistry.UniqueIdentifier uid = null;
        if (stack != null && stack.getItem() != null) {
            uid = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        }
        if (uid == null) {
            uid = GameRegistry.findUniqueIdentifierFor(block);
        }

        if (uid != null) {
            String modid = uid.modId;
            String name = uid.name;
            if (meta > 0) {
                info.fullId = modid + ":" + name + "@" + meta;
            } else {
                info.fullId = modid + ":" + name;
            }
            info.prettyModName = getPrettyModName(modid);
        }

        EntityPlayer player = mc.thePlayer;
        if (player != null) {
            info.harvestable = false;
            info.hasHarvestInfo = false;

            String requiredToolClass = block.getHarvestTool(meta);
            int requiredLevel = block.getHarvestLevel(meta);

            if (block.getMaterial() == Material.cloth || block.getMaterial() == Material.leaves) {
                requiredToolClass = "shears";
                requiredLevel = -1;
            }

            if (block.getMaterial() == Material.ground
                    || block.getMaterial() == Material.grass
                    || block.getMaterial() == Material.sand) {
                requiredToolClass = "any";
                requiredLevel = -1;
            }

            if (requiredToolClass == null || requiredToolClass.isEmpty()) {
                requiredToolClass = guessToolClass(block);
            }

            if (requiredToolClass != null && !requiredToolClass.isEmpty()) {
                info.hasHarvestInfo = true;
                info.harvestTool = formatToolName(requiredToolClass);
                info.harvestLevelName = getHarvestLevelName(requiredLevel);

                if ("any".equalsIgnoreCase(requiredToolClass) || requiredLevel < 0) {
                    info.harvestable = true;
                } else {
                    ItemStack held = player.getHeldItem();
                    if (held != null) {
                        boolean toolMatches;

                        if ("shears".equalsIgnoreCase(requiredToolClass)) {
                            toolMatches = held.getItem() == Items.shears;
                        } else {
                            String heldToolClass = null;
                            for (Object s : held.getItem().getToolClasses(held)) {
                                heldToolClass = String.valueOf(s);
                                break;
                            }
                            toolMatches = heldToolClass != null && heldToolClass.equals(requiredToolClass);
                        }

                        int heldLevel = held.getItem().getHarvestLevel(held, requiredToolClass);
                        boolean levelOK = requiredLevel <= heldLevel || requiredLevel < 0;

                        if (toolMatches && levelOK) {
                            info.harvestable = true;
                        }
                    }
                }
            }
        }

        if (block instanceof BlockSapling) {
            info.growthPercent = getSaplingGrowthPercent(world, x, y, z, meta);
        } else {
            clearSaplingGrowth(world, x, y, z);
        }

        return info;
    }

    private TargetInfo getEntityInfo(MovingObjectPosition mop) {
        Entity e = mop.entityHit;
        if (e == null) return null;

        if (!(e instanceof EntityLivingBase)) {
            TargetInfo info = new TargetInfo();
            info.displayName = e.getCommandSenderName();
            info.growthPercent = -1;
            return info;
        }

        EntityLivingBase living = (EntityLivingBase) e;

        TargetInfo info = new TargetInfo();
        info.displayName = living.getCommandSenderName();
        info.entity = living;
        info.health = living.getHealth();
        info.maxHealth = living.getMaxHealth();
        info.growthPercent = -1;

        String entityKey = null;
        try {
            entityKey = EntityList.getEntityString(living);
        } catch (Throwable ignored) {
            entityKey = null;
        }

        if (entityKey == null || entityKey.length() == 0) {
            entityKey = living.getClass().getSimpleName();
            info.prettyModName = "Unknown";
            info.fullId = "unknown:" + entityKey;
            return info;
        }

        String modid = null;
        String name = entityKey;

        int colon = entityKey.indexOf(':');
        if (colon > 0) {
            modid = entityKey.substring(0, colon);
            name = entityKey.substring(colon + 1);
        } else {
            int dot = entityKey.indexOf('.');
            if (dot > 0) {
                modid = entityKey.substring(0, dot);
                name = entityKey.substring(dot + 1);
            }
        }

        if (modid == null || modid.length() == 0) {
            modid = "minecraft";
        }

        info.prettyModName = getPrettyModName(modid);
        info.fullId = modid + ":" + name;

        return info;
    }

    private int getSaplingGrowthPercent(World world, int x, int y, int z, int meta) {
        int dim = world.provider.dimensionId;
        String key = dim + ":" + x + ":" + y + ":" + z;

        long now = world.getTotalWorldTime();
        boolean stageOne = (meta & 8) != 0;

        SaplingGrowth g = saplingGrowthMap.get(key);
        if (g == null) {
            g = new SaplingGrowth();
            g.plantedTime = now;
            g.stageOneSeen = stageOne;
            saplingGrowthMap.put(key, g);
        } else {
            if (stageOne && !g.stageOneSeen) {
                g.stageOneSeen = true;
                g.stageOneTime = now;
            }
        }

        long baseDuration = 20L * 60L * 5L;
        long stageOneDuration = 20L * 60L * 2L;

        int pct;
        if (!g.stageOneSeen) {
            long elapsed = now - g.plantedTime;
            if (elapsed < 0) elapsed = 0;
            float f = (float) elapsed / (float) baseDuration;
            if (f > 1.0F) f = 1.0F;
            pct = (int) (f * 80.0F);
        } else {
            if (g.stageOneTime <= 0L) g.stageOneTime = now;
            long elapsed0 = g.stageOneTime - g.plantedTime;
            if (elapsed0 < 0) elapsed0 = 0;
            float f0 = (float) elapsed0 / (float) baseDuration;
            if (f0 > 1.0F) f0 = 1.0F;
            int basePct = (int) (f0 * 80.0F);
            if (basePct < 40) basePct = 40;

            long elapsed1 = now - g.stageOneTime;
            if (elapsed1 < 0) elapsed1 = 0;
            float f1 = (float) elapsed1 / (float) stageOneDuration;
            if (f1 > 1.0F) f1 = 1.0F;
            int extra = (int) (f1 * 60.0F);

            pct = basePct + extra;
        }

        if (pct < 0) pct = 0;
        if (pct > 99) pct = 99;

        g.lastPercent = pct;
        return pct;
    }

    private void clearSaplingGrowth(World world, int x, int y, int z) {
        int dim = world.provider.dimensionId;
        String key = dim + ":" + x + ":" + y + ":" + z;
        saplingGrowthMap.remove(key);
    }

    private void renderSpinningBlock(ItemStack stack, int centerX, int centerY, TargetInfo info) {
        if (mc.theWorld == null || stack == null || stack.getItem() == null) return;

        Item item = stack.getItem();
        Block block = null;

        boolean forceEntityRender = false;

        if (item instanceof ItemBlock) {
            block = Block.getBlockFromItem(item);
            if (block != null) {
                int rt;
                try {
                    rt = block.getRenderType();
                } catch (Throwable ignored) {
                    rt = 0;
                }

                if (rt == -1) {
                    forceEntityRender = true;
                }
            }
        }

        try {
            if (WittPreview.render(stack, centerX, centerY, mc)) return;
        } catch (Throwable ignored) {
            renderFlatItem(stack, centerX, centerY);
            return;
        }

        if (info != null && info.isDoubleChest && block instanceof BlockChest && forceEntityRender) {
            renderSpinningDoubleChest(stack, centerX, centerY, info.doubleChestAlongX);
            return;
        }

        boolean oldInFrame = RenderItem.renderInFrame;

        try {
            long t = Minecraft.getSystemTime();
            float angle = (t % 4000L) / 4000.0F * 360.0F;

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_CULL_FACE);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            GL11.glShadeModel(GL11.GL_FLAT);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

            boolean isTallPlant = false;
            if (block != null) {
                if (block instanceof BlockDoublePlant || block instanceof BlockTallGrass) {
                    isTallPlant = true;
                }
            }

            float scale = 20F;
            float yOffset = 6F;

            if (isTallPlant && block != null) {
                scale = 16F;
                yOffset = 10F;
            }

            boolean isTranslucent = false;
            if (block != null) {
                try {
                    isTranslucent = block.getRenderBlockPass() == 1;
                } catch (Throwable ignored) {
                }
            }

            if (isTranslucent) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
            } else {
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }

            boolean isPane = block instanceof BlockPane;
            if (isPane) {
                scale = 24F;
                yOffset = 7F;
            }

            GL11.glTranslatef(centerX, centerY + yOffset, 150F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(150F, 1F, 0F, 0F);

            if (isPane) {
                GL11.glRotatef(45F, 0F, 1F, 0F);
            }

            if (!forceEntityRender && block != null) {
                GL11.glRotatef(angle, 0F, 1F, 0F);

                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

                if (isPane) {
                    try {
                        block.setBlockBoundsForItemRender();
                        renderBlocks.setRenderBoundsFromBlock(block);
                    } catch (Throwable ignored) {
                    }
                }

                renderBlocks.renderBlockAsItem(block, stack.getItemDamage(), 1.0F);

                if (isPane) {
                    try {
                        block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
                        renderBlocks.setRenderBoundsFromBlock(block);
                    } catch (Throwable ignored) {
                    }
                }
            } else {
                IItemRenderer irInv = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);
                IItemRenderer irEnt = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);

                if (irInv != null) {
                    RenderHelper.enableStandardItemLighting();
                    irInv.renderItem(IItemRenderer.ItemRenderType.INVENTORY, stack);
                    RenderHelper.disableStandardItemLighting();
                } else if (irEnt != null) {
                    EntityItem ent = new EntityItem(mc.theWorld, 0, 0, 0, stack);
                    ent.hoverStart = 0;
                    ent.age = 0;
                    ent.ticksExisted = 0;
                    ent.rotationYaw = 0F;
                    ent.prevRotationYaw = 0F;

                    RenderHelper.enableStandardItemLighting();
                    irEnt.renderItem(IItemRenderer.ItemRenderType.ENTITY, stack, renderBlocks, ent);
                    RenderHelper.disableStandardItemLighting();
                } else {
                    mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);

                    EntityItem ent = new EntityItem(mc.theWorld, 0, 0, 0, stack);
                    ent.hoverStart = 0;
                    ent.age = 0;
                    ent.ticksExisted = 0;
                    ent.rotationYaw = 0F;
                    ent.prevRotationYaw = 0F;

                    RenderItem ri = new RenderItem();
                    ri.setRenderManager(RenderManager.instance);

                    RenderItem.renderInFrame = true;
                    RenderHelper.enableGUIStandardItemLighting();
                    ri.doRender(ent, 0, 0, 0, 0F, 0F);
                    RenderHelper.disableStandardItemLighting();
                    RenderItem.renderInFrame = oldInFrame;
                }
            }

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        } catch (Throwable ignored) {
            RenderItem.renderInFrame = oldInFrame;
            renderFlatItem(stack, centerX, centerY);
        }
    }

    private void renderSpinningDoubleChest(ItemStack stack, int centerX, int centerY, boolean alongX) {
        if (mc.theWorld == null || stack == null || stack.getItem() == null) return;

        float oldBx = OpenGlHelper.lastBrightnessX;
        float oldBy = OpenGlHelper.lastBrightnessY;

        boolean oldInFrame = RenderItem.renderInFrame;

        try {
            long t = Minecraft.getSystemTime();
            float angle = (t % 4000L) / 4000.0F * 360.0F;

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_CULL_FACE);

            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            GL11.glShadeModel(GL11.GL_FLAT);

            GL11.glColor4f(1F, 1F, 1F, 1F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glDisable(GL11.GL_BLEND);

            float scale = 18F;
            float yOffset = 6F;

            GL11.glTranslatef(centerX, centerY + yOffset, 150F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(150F, 1F, 0F, 0F);
            GL11.glRotatef(angle, 0F, 1F, 0F);

            float off = 0.55F;

            mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);

            EntityItem e1 = new EntityItem(mc.theWorld, 0, 0, 0, stack);
            EntityItem e2 = new EntityItem(mc.theWorld, 0, 0, 0, stack);
            e1.hoverStart = 0;
            e2.hoverStart = 0;

            RenderHelper.enableGUIStandardItemLighting();

            RenderItem ri = new RenderItem();
            ri.setRenderManager(RenderManager.instance);
            RenderItem.renderInFrame = true;

            GL11.glPushMatrix();
            if (alongX) GL11.glTranslatef(-off, 0F, 0F); else GL11.glTranslatef(0F, 0F, -off);
            ri.doRender(e1, 0, 0, 0, 0F, 0F);
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            if (alongX) GL11.glTranslatef(off, 0F, 0F); else GL11.glTranslatef(0F, 0F, off);
            ri.doRender(e2, 0, 0, 0, 0F, 0F);
            GL11.glPopMatrix();

            RenderItem.renderInFrame = oldInFrame;

            RenderHelper.disableStandardItemLighting();

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldBx, oldBy);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        } catch (Throwable ignored) {
            RenderItem.renderInFrame = oldInFrame;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldBx, oldBy);
            renderFlatItem(stack, centerX, centerY);
        }
    }

    private void renderFlatItem(ItemStack stack, int centerX, int centerY) {
        RenderItem ri = new RenderItem();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float scale = 3.0F;
        int x = (int) ((centerX - 8 * scale) / scale);
        int y = (int) ((centerY - 8 * scale) / scale);

        GL11.glTranslatef(0F, 0F, 200F);
        GL11.glScalef(scale, scale, 1F);

        ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        ri.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void renderSpinningEntity(EntityLivingBase entity, int centerX, int centerY) {
        if (mc.theWorld == null || entity == null) return;

        float oldBx = OpenGlHelper.lastBrightnessX;
        float oldBy = OpenGlHelper.lastBrightnessY;

        float oldYaw = entity.rotationYaw;
        float oldPrevYaw = entity.prevRotationYaw;
        float oldRenderYaw = entity.renderYawOffset;
        float oldYawHead = entity.rotationYawHead;
        float oldPrevYawHead = entity.prevRotationYawHead;

        long t = Minecraft.getSystemTime();
        float angle = (t % 4000L) / 4000.0F * 360.0F;

        entity.rotationYaw = angle;
        entity.prevRotationYaw = angle;
        entity.renderYawOffset = angle;
        entity.rotationYawHead = angle;
        entity.prevRotationYawHead = angle;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        GL11.glTranslatef(centerX, centerY + 6, 150F);

        float h = entity.height;
        float w = entity.width;
        if (h < 0.1F) h = 0.1F;
        if (w < 0.1F) w = 0.1F;

        float targetHeight = 2.0F;
        float targetWidth = 1.0F;
        float baseScale = 16F;

        float scaleH = baseScale * (targetHeight / h);
        float scaleW = baseScale * (targetWidth / w);
        float scale = Math.min(scaleH, scaleW);

        if (scale > baseScale) scale = baseScale;
        if (scale < 6F) scale = 6F;

        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(15F, 1F, 0F, 0F);

        RenderHelper.enableStandardItemLighting();
        RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, 0F, 1F);
        RenderHelper.disableStandardItemLighting();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldBx, oldBy);

        GL11.glPopAttrib();
        GL11.glPopMatrix();

        entity.rotationYaw = oldYaw;
        entity.prevRotationYaw = oldPrevYaw;
        entity.renderYawOffset = oldRenderYaw;
        entity.rotationYawHead = oldYawHead;
        entity.prevRotationYawHead = oldPrevYawHead;
    }

    private String getPrettyModName(String modid) {
        for (ModContainer c : Loader.instance().getModList()) {
            if (c.getModId().equalsIgnoreCase(modid)) return c.getName();
        }
        return modid;
    }

    private String getHarvestLevelName(int lvl) {
        if (lvl < 0) return "Any";
        switch (lvl) {
            case 0:
                return "Wood";
            case 1:
                return "Stone";
            case 2:
                return "Iron";
            case 3:
                return "Diamond";
            default:
                return "Lvl " + lvl;
        }
    }

    private String guessToolClass(Block block) {
        Material m = block.getMaterial();

        if (m == Material.rock || m == Material.iron || m == Material.anvil) {
            return "pickaxe";
        }

        if (m == Material.clay || m == Material.snow) {
            return "shovel";
        }

        if (m == Material.wood || m == Material.plants || m == Material.leaves || m == Material.vine || m == Material.cactus || m == Material.gourd) {
            return "axe";
        }

        return null;
    }

    private String formatToolName(String toolClass) {
        if (toolClass == null || toolClass.isEmpty()) return "Unknown";
        if ("pickaxe".equalsIgnoreCase(toolClass)) return "Pickaxe";
        if ("shovel".equalsIgnoreCase(toolClass) || "spade".equalsIgnoreCase(toolClass)) return "Shovel";
        if ("axe".equalsIgnoreCase(toolClass)) return "Axe";
        if ("hoe".equalsIgnoreCase(toolClass)) return "Hoe";
        if ("sword".equalsIgnoreCase(toolClass)) return "Sword";
        if ("shears".equalsIgnoreCase(toolClass)) return "Shears";
        if ("any".equalsIgnoreCase(toolClass)) return "Any";
        return Character.toUpperCase(toolClass.charAt(0)) + toolClass.substring(1);
    }

    private static class TargetInfo {
        String displayName;
        String prettyModName;
        String fullId;
        ItemStack stack;
        EntityLivingBase entity;
        float health;
        float maxHealth;
        boolean hasHarvestInfo;
        boolean harvestable;
        String harvestTool;
        String harvestLevelName;
        int growthPercent = -1;
        boolean isDoubleChest;
        boolean doubleChestAlongX;
    }

    private static class SaplingGrowth {
        long plantedTime;
        long stageOneTime;
        boolean stageOneSeen;
        int lastPercent;
    }
}
