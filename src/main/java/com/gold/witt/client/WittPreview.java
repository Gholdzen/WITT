package com.gold.witt.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public final class WittPreview {

    private WittPreview() {}

    public static boolean render(ItemStack stack, int centerX, int centerY, Minecraft mc) {
        if (stack == null || stack.getItem() == null || mc == null || mc.theWorld == null) return false;

        IItemRenderer rEnt = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.ENTITY);
        IItemRenderer rInv = MinecraftForgeClient.getItemRenderer(stack, IItemRenderer.ItemRenderType.INVENTORY);

        final IItemRenderer renderer;
        final IItemRenderer.ItemRenderType type;

        if (rEnt != null) {
            renderer = rEnt;
            type = IItemRenderer.ItemRenderType.ENTITY;
        } else if (rInv != null) {
            renderer = rInv;
            type = IItemRenderer.ItemRenderType.INVENTORY;
        } else {
            return false;
        }

        long t = Minecraft.getSystemTime();
        float angle = (t % 4000L) / 4000.0F * 360.0F;

        EntityItem ent = null;
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            ent = new EntityItem(mc.theWorld, 0, 0, 0, stack);
            ent.hoverStart = 0;
            ent.age = 0;
            ent.ticksExisted = 0;
        }

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        float scale = 20F;
        float yOffset = 8F;

        GL11.glTranslatef(centerX, centerY + yOffset, 150F);
        GL11.glScalef(scale, scale, scale);

        GL11.glRotatef(15F, 1F, 0F, 0F);
        GL11.glRotatef(180F, 0F, 0F, 1F);

        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            if (renderer.shouldUseRenderHelper(type, stack, IItemRenderer.ItemRendererHelper.ENTITY_ROTATION)) {
                GL11.glRotatef(angle, 0F, 1F, 0F);
            }
        }

        RenderHelper.enableStandardItemLighting();

        try {
            if (type == IItemRenderer.ItemRenderType.ENTITY) {
                renderer.renderItem(type, stack, null, ent);
            } else {
                renderer.renderItem(type, stack);
            }
        } catch (Throwable ignored) {
            RenderHelper.disableStandardItemLighting();
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            return false;
        }

        RenderHelper.disableStandardItemLighting();

        GL11.glPopAttrib();
        GL11.glPopMatrix();
        return true;
    }
}
