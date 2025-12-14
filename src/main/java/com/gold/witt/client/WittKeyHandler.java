package com.gold.witt.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class WittKeyHandler {

    private static final KeyBinding openConfigKey =
            new KeyBinding("WITT", Keyboard.KEY_H, "What Is That Thing");

    public static void register() {
        ClientRegistry.registerKeyBinding(openConfigKey);
        FMLCommonHandler.instance().bus().register(new WittKeyHandler());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;

        if (openConfigKey.isPressed()) {
            mc.displayGuiScreen(new GuiWittConfig(null));
        }
    }
}
