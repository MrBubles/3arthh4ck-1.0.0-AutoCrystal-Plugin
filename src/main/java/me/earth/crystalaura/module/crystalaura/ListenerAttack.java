package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.util.ThreadUtil;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

final class ListenerAttack
        extends ModuleListener<CrystalAura, PacketEvent.Post<CPacketUseEntity>> {
    public ListenerAttack(CrystalAura module) {
        super(module, PacketEvent.Post.class, CPacketUseEntity.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketUseEntity> event) {
        if (event.getPacket().getAction() == CPacketUseEntity.Action.ATTACK && !this.module.isPingBypass()) {
            ICPacketUseEntity packet = (ICPacketUseEntity) event.getPacket();
            this.module.attacked.add(packet.getEntityID());
            if (event.getPacket().getEntityFromWorld(ListenerAttack.mc.world) instanceof EntityEnderCrystal && this.module.antiFeetPlace.getValue().booleanValue()) {
                BlockPos antiPos = event.getPacket().getEntityFromWorld(ListenerAttack.mc.world).getPosition().down();
                ThreadUtil.run(() -> {
                    CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(antiPos, EnumFacing.UP, this.getHand(), 0.5f, 1.0f, 0.5f);
                    CPacketAnimation animation1 = new CPacketAnimation(this.getHand());
                    ListenerAttack.mc.player.connection.sendPacket(place);
                    ListenerAttack.mc.player.connection.sendPacket(animation1);
                }, (long) (Managers.TICK.getServerTickLengthMS() + 11) - Managers.TICK.getTickTimeAdjusted());
            }
        }
    }

    private EnumHand getHand() {
        return ListenerAttack.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }
}

