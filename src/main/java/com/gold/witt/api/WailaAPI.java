package com.gold.witt.api;

public final class WailaAPI {
    private static IWailaRegistrar registrar;

    private WailaAPI() {}

    public static IWailaRegistrar instance() {
        return registrar;
    }

    public static void setRegistrar(IWailaRegistrar r) {
        registrar = r;
    }
}
