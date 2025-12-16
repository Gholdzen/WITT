package com.gold.witt.integration;

import com.gold.witt.api.IWittIntegration;
import com.gold.witt.api.WittContext;
import com.gold.witt.api.WittTooltip;
import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WittHatsIntegration implements IWittIntegration {

    @Override
    public void addLines(WittContext ctx, WittTooltip out) {
        if (ctx == null || ctx.mop == null) return;
        if (!(ctx.mop.entityHit instanceof EntityLivingBase)) return;
        if (!Loader.isModLoaded("Hats") && !Loader.isModLoaded("hats")) return;

        EntityLivingBase ent = (EntityLivingBase) ctx.mop.entityHit;

        Object hatObj = findHatObject(ent);
        if (hatObj == null) return;

        String name = extractHatName(hatObj);
        if (name != null && name.length() > 0) out.add("Hat: " + name, 0xFFD27F);
        else out.add("Hat:", 0xFFD27F);
    }

    private Object findHatObject(EntityLivingBase ent) {
        Entity rider = ent.riddenByEntity;
        int guard = 0;
        while (rider != null && guard++ < 8) {
            String cn = rider.getClass().getName();
            String ln = cn == null ? "" : cn.toLowerCase();
            if (ln.contains("hats") || ln.contains("entityhat") || ln.endsWith(".hat") || ln.contains(".hat")) {
                Object o;
                o = getFieldValue(rider, "hat");
                if (o != null) return o;
                o = getFieldValue(rider, "theHat");
                if (o != null) return o;
                o = getFieldValue(rider, "hatData");
                if (o != null) return o;
                return rider;
            }
            rider = rider.riddenByEntity;
        }
        return null;
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

        Object inner = getFieldValue(hatObj, "hat");
        if (inner != null && inner != hatObj) {
            s = extractHatName(inner);
            if (s != null) return s;
        }

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
            return s.length() == 0 ? null : s;
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
            return s.length() == 0 ? null : s;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private Object getFieldValue(Object o, String field) {
        try {
            Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
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
