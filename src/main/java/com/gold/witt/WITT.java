package com.gold.witt;

import com.gold.witt.api.IWittIntegration;
import com.gold.witt.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = "WITT",
        name = "WITT - What Is That Thing",
        version = "WITT-1.7.10-1.0",
        acceptedMinecraftVersions = "1.7.10"
)
public class WITT {

    @SidedProxy(
            clientSide = "com.gold.witt.proxy.ClientProxy",
            serverSide = "com.gold.witt.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    private static final List INTEGRATIONS = new ArrayList();

    public static void createWittIntegration(String modid, IWittIntegration integration) {
        if (modid == null) return;
        if (integration == null) return;

        String id = modid.trim();
        if (id.length() == 0) return;

        for (int i = 0; i < INTEGRATIONS.size(); i++) {
            Entry e = (Entry) INTEGRATIONS.get(i);
            if (e.modid.equalsIgnoreCase(id)) return;
        }

        INTEGRATIONS.add(new Entry(id, integration));
    }

    public static IWittIntegration[] getWittIntegrations() {
        IWittIntegration[] arr = new IWittIntegration[INTEGRATIONS.size()];
        for (int i = 0; i < INTEGRATIONS.size(); i++) {
            arr[i] = ((Entry) INTEGRATIONS.get(i)).integration;
        }
        return arr;
    }

    private static final class Entry {
        public final String modid;
        public final IWittIntegration integration;

        private Entry(String modid, IWittIntegration integration) {
            this.modid = modid;
            this.integration = integration;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }
}
