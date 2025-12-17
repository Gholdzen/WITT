package com.gold.witt.api.impl;

import com.gold.witt.api.IWailaConfigHandler;

public final class ConfigHandler implements IWailaConfigHandler {
    public static final ConfigHandler INSTANCE = new ConfigHandler();

    private ConfigHandler() {}

    public boolean getConfig(String key) {
        return true;
    }

    public boolean getConfig(String key, boolean def) {
        return true;
    }
}
