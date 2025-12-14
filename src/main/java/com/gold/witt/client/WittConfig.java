package com.gold.witt.client;

public class WittConfig {

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
}
