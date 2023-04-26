//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\KHALED IBRAHIM\Desktop\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.util.EnumHand
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

final class ListenerPlace
        extends ModuleListener<CrystalAura, PacketEvent.Post<CPacketPlayerTryUseItemOnBlock>> {
    public ListenerPlace(CrystalAura module) {
        super(module, PacketEvent.Post.class, CPacketPlayerTryUseItemOnBlock.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketPlayerTryUseItemOnBlock> event) {
        CPacketPlayerTryUseItemOnBlock packet;
        if (this.module.predict.getValue().booleanValue() && !this.module.isPingBypass() && ListenerPlace.mc.player.getHeldItem((packet = event.getPacket()).getHand()).getItem() == Items.END_CRYSTAL) {
            mc.addScheduledTask(() -> {
                int id = this.getID();
                if (id != -1 && this.module.getBreakTimer().passed(this.module.breakDelay.getValue().intValue())) {
                    ICPacketUseEntity useEntity = (ICPacketUseEntity) new CPacketUseEntity();
                    useEntity.setAction(CPacketUseEntity.Action.ATTACK);
                    useEntity.setEntityId(id);
                    ListenerPlace.mc.player.connection.sendPacket((Packet) useEntity);
                    ListenerPlace.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    this.module.getBreakTimer().reset(this.module.breakDelay.getValue().intValue());
                }
            });
        }
    }

    private int getID() {
        if (ListenerPlace.mc.world == null || ListenerPlace.mc.player == null) {
            return -1;
        }
        for (EntityPlayer player : ListenerPlace.mc.world.playerEntities) {
            if (player == null || !player.isDead && !InventoryUtil.isHolding(player, Items.BOW) && !InventoryUtil.isHolding(player, Items.EXPERIENCE_BOTTLE))
                continue;
            return -1;
        }
        int highest = -1;
        for (Entity entity : ListenerPlace.mc.world.loadedEntityList) {
            if (entity == null || entity.getEntityId() <= highest) continue;
            highest = entity.getEntityId();
        }
        return highest + 1;
    }
}

