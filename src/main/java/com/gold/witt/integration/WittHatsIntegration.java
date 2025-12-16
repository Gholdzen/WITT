package com.gold.witt.integration;

import com.gold.witt.api.IWittIntegration;
import com.gold.witt.api.WittContext;
import com.gold.witt.api.WittTooltip;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class WittHatsIntegration implements IWittIntegration {

    private static volatile boolean scanned = false;
    private static volatile Method hatGetter = null;

    @Override
    public void addLines(WittContext ctx, WittTooltip out) {
        if (ctx == null || ctx.mop == null) return;
        if (!(ctx.mop.entityHit instanceof EntityLivingBase)) return;
        if (!Loader.isModLoaded("Hats") && !Loader.isModLoaded("hats")) return;

        ensureScanned();
        if (hatGetter == null) return;

        EntityLivingBase ent = (EntityLivingBase) ctx.mop.entityHit;

        Object hatObj = invokeHatGetter(ent);
        if (hatObj == null) return;

        String name = extractHatName(hatObj);
        if (name != null && name.length() > 0) out.add("Hat: " + name, 0xFFD27F);
        else out.add("Hat:", 0xFFD27F);
    }

    private void ensureScanned() {
        if (scanned) return;
        scanned = true;

        ModContainer mc = Loader.instance().getIndexedModList().get("Hats");
        if (mc == null) mc = Loader.instance().getIndexedModList().get("hats");
        if (mc == null) return;

        File src = mc.getSource();
        if (src == null || !src.isFile()) return;

        ZipFile zip = null;
        try {
            zip = new ZipFile(src);

            for (java.util.Enumeration e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                if (ze == null) continue;

                String n = ze.getName();
                if (n == null) continue;
                if (!n.endsWith(".class")) continue;

                String ln = n.toLowerCase();
                if (!ln.startsWith("hats/")) continue;
                if (ln.contains("$")) continue;
                if (ln.contains("/client/") == false && ln.contains("/common/") == false && ln.contains("/core/") == false) continue;
                if (!(ln.contains("hat") || ln.contains("handler") || ln.contains("tracker") || ln.contains("data"))) continue;

                String cn = n.substring(0, n.length() - 6).replace('/', '.');

                Class c;
                try {
                    c = Class.forName(cn, false, getClass().getClassLoader());
                } catch (Throwable t) {
                    continue;
                }
                if (c == null) continue;

                Method[] ms;
                try {
                    ms = c.getDeclaredMethods();
                } catch (Throwable t) {
                    continue;
                }
                if (ms == null) continue;

                for (int i = 0; i < ms.length; i++) {
                    Method m = ms[i];
                    if (m == null) continue;
                    if ((m.getModifiers() & Modifier.STATIC) == 0) continue;

                    String mn = m.getName();
                    if (mn == null) continue;
                    String mln = mn.toLowerCase();
                    if (!(mln.contains("get") && mln.contains("hat"))) continue;

                    Class[] p = m.getParameterTypes();
                    if (p == null || p.length != 1) continue;

                    Class rt = m.getReturnType();
                    if (rt == Void.TYPE) continue;
                    if (rt == Boolean.TYPE || rt == Boolean.class) continue;

                    String rtn = rt.getName().toLowerCase();
                    if (!rtn.contains("hat")) continue;

                    if (p[0] != int.class && !p[0].isAssignableFrom(Entity.class) && !p[0].isAssignableFrom(EntityLivingBase.class)) continue;

                    m.setAccessible(true);
                    hatGetter = m;
                    return;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            try {
                if (zip != null) zip.close();
            } catch (Throwable ignored) {
            }
        }
    }

    private Object invokeHatGetter(EntityLivingBase ent) {
        try {
            Method m = hatGetter;
            if (m == null) return null;

            Class[] p = m.getParameterTypes();
            if (p == null || p.length != 1) return null;

            Object arg = null;

            if (p[0] == int.class || p[0] == Integer.class) arg = Integer.valueOf(ent.getEntityId());
            else if (p[0].isAssignableFrom(Entity.class)) arg = (Entity) ent;
            else if (p[0].isAssignableFrom(EntityLivingBase.class) || p[0].isAssignableFrom(ent.getClass())) arg = ent;

            if (arg == null) return null;

            return m.invoke(null, arg);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private String extractHatName(Object hatObj) {
        String s;

        s = callStringGetter(hatObj, "getName");
        if (s != null) return s;

        s = callStringGetter(hatObj, "getHatName");
        if (s != null) return s;

        s = callStringGetter(hatObj, "getDisplayName");
        if (s != null) return s;

        s = getStringField(hatObj, "name");
        if (s != null) return s;

        s = getStringField(hatObj, "hatName");
        if (s != null) return s;

        s = getStringField(hatObj, "id");
        if (s != null) return s;

        s = String.valueOf(hatObj);
        if (s != null) {
            s = s.trim();
            if (s.length() > 0 && !"null".equalsIgnoreCase(s)) return s;
        }

        return null;
    }

    private String callStringGetter(Object o, String method) {
        try {
            Method m = o.getClass().getMethod(method);
            if (m.getReturnType() != String.class) return null;
            Object r = m.invoke(o);
            if (!(r instanceof String)) return null;
            String s = ((String) r).trim();
            if (s.length() == 0) return null;
            return s;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private String getStringField(Object o, String field) {
        try {
            Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            Object r = f.get(o);
            if (!(r instanceof String)) return null;
            String s = ((String) r).trim();
            if (s.length() == 0) return null;
            return s;
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Override
    public boolean renderStack(WittContext ctx, ItemStack stack, int x, int y) {
        return false;
    }

    @Override
    public boolean renderEntity(WittContext ctx, EntityLivingBase entity, int x, int y) {
        return false;
    }
}

