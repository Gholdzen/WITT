package com.gold.witt.client;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class WittConfig {

    private static Configuration config;

    public static boolean showBlockOverlay = true;
    public static boolean showEntityOverlay = true;
    public static boolean showModId = true;
    public static boolean showHarvestInfo = true;

    public enum OverlayCorner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public static OverlayCorner overlayCorner = OverlayCorner.TOP_LEFT;


    public static void init(File configFile) {
        config = new Configuration(configFile);
        load();
    }

    public static void load() {
        if (config == null) return;

        try {
            config.load();

            showBlockOverlay  = config.get("overlay", "showBlockOverlay",  true).getBoolean(true);
            showEntityOverlay = config.get("overlay", "showEntityOverlay", true).getBoolean(true);
            showModId         = config.get("overlay", "showModId",         true).getBoolean(true);
            showHarvestInfo   = config.get("overlay", "showHarvestInfo",   true).getBoolean(true);

            String corner = config.get("overlay", "overlayCorner", OverlayCorner.TOP_LEFT.name()).getString();
            overlayCorner = parseCorner(corner);

        } catch (Exception ignored) {
            overlayCorner = OverlayCorner.TOP_LEFT;
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static void save() {
        if (config == null) return;

        config.get("overlay", "showBlockOverlay",  true).set(showBlockOverlay);
        config.get("overlay", "showEntityOverlay", true).set(showEntityOverlay);
        config.get("overlay", "showModId",         true).set(showModId);
        config.get("overlay", "showHarvestInfo",   true).set(showHarvestInfo);
        config.get("overlay", "overlayCorner", OverlayCorner.TOP_LEFT.name()).set(overlayCorner.name());

        if (config.hasChanged()) config.save();
    }

    private static OverlayCorner parseCorner(String s) {
        if (s == null) return OverlayCorner.TOP_LEFT;
        try {
            return OverlayCorner.valueOf(s.trim().toUpperCase());
        } catch (Exception ignored) {
            return OverlayCorner.TOP_LEFT;
        }
    }

    public static void cycleCorner() {
        switch (overlayCorner) {
            case TOP_LEFT:     overlayCorner = OverlayCorner.TOP_RIGHT;     break;
            case TOP_RIGHT:    overlayCorner = OverlayCorner.BOTTOM_RIGHT;  break;
            case BOTTOM_RIGHT: overlayCorner = OverlayCorner.BOTTOM_LEFT;   break;
            case BOTTOM_LEFT:
            default:           overlayCorner = OverlayCorner.TOP_LEFT;      break;
        }
    }
}
