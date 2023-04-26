/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  org.lwjgl.opengl.GL11
 */
package me.earth.crystalaura.module.crystalaura.util;

import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderBox {
    public static void renderBox(BlockPos pos) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
        Color boxColor = new Color(1.0f, 1.0f, 1.0f, 0.9f);
        RenderUtil.startRender();
        RenderUtil.drawOutline(bb, 1.5f, boxColor);
        RenderUtil.endRender();
        boxColor = new Color(1.0f, 1.0f, 1.0f, 0.3f);
        RenderUtil.startRender();
        RenderUtil.drawBox(bb, boxColor);
        RenderUtil.endRender();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}

