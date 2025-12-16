package com.gold.witt.integration;

import com.gold.witt.api.IWittIntegration;
import com.gold.witt.api.WittContext;
import com.gold.witt.api.WittTooltip;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class WittForgeFluidsIntegration implements IWittIntegration {

    @Override
    public void addLines(WittContext ctx, WittTooltip out) {
        if (ctx == null || ctx.world == null || ctx.mop == null) return;
        if (ctx.mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        int x = ctx.mop.blockX;
        int y = ctx.mop.blockY;
        int z = ctx.mop.blockZ;

        TileEntity te = ctx.world.getTileEntity(x, y, z);
        if (!(te instanceof IFluidHandler)) return;

        IFluidHandler fh = (IFluidHandler) te;

        FluidTankInfo[] infos;
        try {
            infos = fh.getTankInfo(ForgeDirection.UNKNOWN);
        } catch (Throwable t) {
            infos = null;
        }
        if (infos == null || infos.length == 0) return;

        for (int i = 0; i < infos.length; i++) {
            FluidTankInfo info = infos[i];
            if (info == null) continue;

            int cap = info.capacity;
            FluidStack fs = info.fluid;

            if (fs == null || fs.getFluid() == null) {
                out.add("Tank " + (i + 1) + ": Empty / " + cap + " mB", 0x55AAFF);
            } else {
                String name;
                try {
                    name = fs.getFluid().getLocalizedName(fs);
                } catch (Throwable t) {
                    name = String.valueOf(fs.getFluid().getName());
                }
                out.add("Tank " + (i + 1) + ": " + name + " " + fs.amount + " / " + cap + " mB", 0x55AAFF);
            }
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
