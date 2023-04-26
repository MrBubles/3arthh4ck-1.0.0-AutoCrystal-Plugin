//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\KHALED IBRAHIM\Desktop\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.modes.Rotate;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

final class ListenerSpawnObject
        extends ModuleListener<CrystalAura, PacketEvent.Receive<SPacketSpawnObject>> {
    public ListenerSpawnObject(CrystalAura module) {
        super(module, PacketEvent.Receive.class, SPacketSpawnObject.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSpawnObject> event) {
        if (this.module.instant.getValue().booleanValue() && !this.module.isPingBypass()) {
            SPacketSpawnObject packet = event.getPacket();
            EntityPlayer target = this.module.getTarget();
            if (packet.getType() == 51 && target != null && !EntityUtil.isDead(target) && ListenerSpawnObject.mc.player != null) {
                float damage;
                BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                if (this.module.getPositions().contains(pos) && this.isValid(pos) && this.rotationCheck(pos) && (damage = DamageUtil.calculate(pos.down())) <= this.module.maxSelfB.getValue().floatValue() && (double) damage < (double) EntityUtil.getHealth(ListenerSpawnObject.mc.player) + 1.0) {
                    this.attack(packet, this.module.slow.remove(pos) ? this.module.slowDelay.getValue().intValue() : this.module.breakDelay.getValue().intValue());
                }
            }
        }
    }

    private void attack(SPacketSpawnObject packetIn, int delay) {
        if (this.module.getBreakTimer().passed(delay)) {
            ICPacketUseEntity useEntity = (ICPacketUseEntity) new CPacketUseEntity();
            useEntity.setAction(CPacketUseEntity.Action.ATTACK);
            useEntity.setEntityId(packetIn.getEntityID());
            ListenerSpawnObject.mc.player.connection.sendPacket((Packet) useEntity);
            ListenerSpawnObject.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.addScheduledTask(((CrystalAura) this.module)::swing);
            this.module.getBreakTimer().reset(delay);
        }
    }

    private boolean isValid(BlockPos pos) {
        if (ListenerSpawnObject.mc.player.getDistanceSq((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5) > (double) MathUtil.square(this.module.breakRange.getValue().floatValue())) {
            return false;
        }
        if (ListenerSpawnObject.mc.player.getDistanceSq((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5) > (double) MathUtil.square(this.module.breakTrace.getValue().floatValue())) {
            return RayTraceUtil.canBeSeen(new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + 1.700000047683716, (double) pos.getZ() + 0.5), ListenerSpawnObject.mc.player);
        }
        return true;
    }

    private boolean rotationCheck(BlockPos pos) {
        return this.module.rotate.getValue().noRotate(Rotate.Break) || RotationUtil.isLegit(pos) || RotationUtil.isLegit(pos.up());
    }
}

