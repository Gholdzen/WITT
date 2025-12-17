package com.gold.witt.api;

public interface IWailaConfigHandler {
    boolean getConfig(String key);
    boolean getConfig(String key, boolean def);
}
