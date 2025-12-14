package com.gold.witt.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiWittConfig extends GuiScreen {

    private final GuiScreen parent;
    private GuiButton btnBlock;
    private GuiButton btnEntity;
    private GuiButton btnModId;
    private GuiButton btnCorner;
    private GuiButton btnHarvest;
    private GuiButton btnDone;
    private GuiButton btnTitle;

    public GuiWittConfig(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int midX = this.width / 2;
        int y = this.height / 7;

        btnTitle = new GuiButton(10, midX - 40, y, 80, 20, "WITT");
        this.buttonList.add(btnTitle);
        y += 30;

        btnBlock = new GuiButton(1, midX - 100, y, 200, 20, "");
        this.buttonList.add(btnBlock);
        y += 24;

        btnEntity = new GuiButton(2, midX - 100, y, 200, 20, "");
        this.buttonList.add(btnEntity);
        y += 24;

        btnModId = new GuiButton(3, midX - 100, y, 200, 20, "");
        this.buttonList.add(btnModId);
        y += 24;

        btnCorner = new GuiButton(4, midX - 100, y, 200, 20, "");
        this.buttonList.add(btnCorner);
        y += 24;

        btnHarvest = new GuiButton(5, midX - 100, y, 200, 20, "");
        this.buttonList.add(btnHarvest);
        y += 30;

        btnDone = new GuiButton(0, midX - 100, y, 200, 20, "Done");
        this.buttonList.add(btnDone);

        updateButtonText();
    }

    private void updateButtonText() {
        btnBlock.displayString   = "Block preview: " + (WittConfig.showBlockOverlay  ? "ON" : "OFF");
        btnEntity.displayString  = "Entity preview: " + (WittConfig.showEntityOverlay ? "ON" : "OFF");
        btnModId.displayString   = "Show mod id: "   + (WittConfig.showModId         ? "ON" : "OFF");
        btnCorner.displayString  = "Overlay corner: " + WittConfig.overlayCorner.name();
        btnHarvest.displayString = "Harvest info: "  + (WittConfig.showHarvestInfo   ? "ON" : "OFF");
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) return;

        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(parent);
                return;

            case 1:
                WittConfig.showBlockOverlay = !WittConfig.showBlockOverlay;
                break;

            case 2:
                WittConfig.showEntityOverlay = !WittConfig.showEntityOverlay;
                break;

            case 3:
                WittConfig.showModId = !WittConfig.showModId;
                break;

            case 4:
                switch (WittConfig.overlayCorner) {
                    case TOP_LEFT:
                        WittConfig.overlayCorner = WittConfig.OverlayCorner.TOP_RIGHT;
                        break;
                    case TOP_RIGHT:
                        WittConfig.overlayCorner = WittConfig.OverlayCorner.BOTTOM_RIGHT;
                        break;
                    case BOTTOM_RIGHT:
                        WittConfig.overlayCorner = WittConfig.OverlayCorner.BOTTOM_LEFT;
                        break;
                    case BOTTOM_LEFT:
                    default:
                        WittConfig.overlayCorner = WittConfig.OverlayCorner.TOP_LEFT;
                        break;
                }
                break;

            case 5:
                WittConfig.showHarvestInfo = !WittConfig.showHarvestInfo;
                break;
        }

        updateButtonText();
    }

    @Override
    public void onGuiClosed() {
        Minecraft.getMinecraft().gameSettings.saveOptions();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
