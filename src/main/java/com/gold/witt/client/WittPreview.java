package com.gold.witt.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public final class WittPreview {

    private WittPreview() {
    }

    public static boolean render(ItemStack stack, int centerX, int centerY, Minecraft mc) {
        if (stack == null || stack.getItem() == null) return false;

        IItemRenderer renderer = MinecraftForgeClient.getItemRenderer(
                stack,
                IItemRenderer.ItemRenderType.INVENTORY
        );
        if (renderer == null) return false;

        long t = Minecraft.getSystemTime();
        float angle = (t % 4000L) / 4000.0F * 360.0F;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);

        float scale = 20F;
        float yOffset = 8F;

        GL11.glTranslatef(centerX, centerY + yOffset, 150F);
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(210F, 1F, 0F, 0F);
        GL11.glRotatef(angle, 0F, 1F, 0F);

        renderer.renderItem(IItemRenderer.ItemRenderType.INVENTORY, stack, new Object[0]);

        GL11.glPopAttrib();
        GL11.glPopMatrix();

        return true;
    }
}
