package com.gold.witt;

import com.gold.witt.api.IWittIntegration;
import com.gold.witt.api.IWailaRegistrarCallback;
import com.gold.witt.api.WailaAPI;
import com.gold.witt.api.impl.WailaRegistrar;
import com.gold.witt.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ProgressManager;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(
        modid = "WITT",
        name = "WITT - What Is That Thing",
        version = "WITT-1.7.10-1.0.2.4",
        acceptedMinecraftVersions = "1.7.10"
)
public class WITT {

    @SidedProxy(
            clientSide = "com.gold.witt.proxy.ClientProxy",
            serverSide = "com.gold.witt.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    private static final List INTEGRATIONS = new ArrayList();
    private static final WailaRegistrar REGISTRAR = new WailaRegistrar();

    public static WailaRegistrar waila() {
        return REGISTRAR;
    }

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
    public void construct(FMLConstructionEvent event) {
        boolean stable = true;

        int size = INTEGRATIONS.size();
        long duration = 5000L + Math.min(5000L, (long) size * 250L);

        int steps = 3 + Math.min(97, size);
        ProgressManager.ProgressBar bar = ProgressManager.push("WITT", steps);
        try {
            long per = duration / (long) steps;

            bar.step("getBlockInfo");
            sleep(per);

            bar.step("getEntityInfo");
            sleep(per);

            for (int i = 0; i < steps - 3; i++) {
                bar.step("Loading " + (i + 1) + "/" + (steps - 3));
                sleep(per);
            }

            bar.step("1.7.10-1.0.2.4 stable:" + stable);
            sleep(per);
        } finally {
            ProgressManager.pop(bar);
        }
    }

    private void sleep(long ms) {
        if (ms <= 0L) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        WailaAPI.setRegistrar(REGISTRAR);
        proxy.init();
    }

    @Mod.EventHandler
    public void imc(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage m : event.getMessages()) {
            if (!"register".equals(m.key)) continue;
            if (!m.isStringMessage()) continue;

            String cls = m.getStringValue();
            try {
                Object o = Class.forName(cls).newInstance();
                if (o instanceof IWailaRegistrarCallback) {
                    ((IWailaRegistrarCallback) o).register(REGISTRAR);
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
