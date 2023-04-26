package me.earth.crystalaura.module.crystalaura;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

final class ListenerRender
        extends ModuleListener<CrystalAura, Render3DEvent> {
    public ListenerRender(CrystalAura module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if (this.module.alphaAnimation != null) {
            this.module.alphaAnimation.add(event.getPartialTicks());
        }
        if (this.module.renderPos != null && !this.module.isPingBypass()) {
            this.doRender(this.module.renderPos);
        }
    }

    public void doRender(BlockPos pos) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        AxisAlignedBB bb = Interpolation.interpolatePos(pos, 1.0f);
        Color boxColor = new Color(this.module.fillColor.getRed(), this.module.fillColor.getGreen(), this.module.fillColor.getBlue(), this.module.fillColor.getAlpha());
        Color outlineColor = new Color(this.module.outlineColor.getRed(), this.module.outlineColor.getGreen(), this.module.outlineColor.getBlue(), this.module.outlineColor.getAlpha());
        RenderUtil.startRender();
        RenderUtil.drawOutline(bb, 1.5f, outlineColor);
        RenderUtil.endRender();
        RenderUtil.startRender();
        RenderUtil.drawBox(bb, boxColor);
        RenderUtil.endRender();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}

