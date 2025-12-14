package com.gold.witt.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;

public class WittKeyInputHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) return;
        if (mc.currentScreen != null) return;

        if (WittKeybinds.openConfig != null && WittKeybinds.openConfig.isPressed()) {
            mc.displayGuiScreen(new GuiWittConfig(null));
        }
    }
}
