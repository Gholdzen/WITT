package com.gold.witt.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class WittKeybinds {

    public static KeyBinding openConfig;

    public static void init() {
        openConfig = new KeyBinding("Open WITT Config", Keyboard.KEY_H, "WITT");
        ClientRegistry.registerKeyBinding(openConfig);
    }
}
