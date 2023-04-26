package me.earth.crystalaura.module.crystalaura;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

final class ListenerSound
        extends ModuleListener<CrystalAura, PacketEvent.Receive<SPacketSoundEffect>> {
    public ListenerSound(CrystalAura module) {
        super(module, PacketEvent.Receive.class, SPacketSoundEffect.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketSoundEffect> event) {
        SPacketSoundEffect packet = event.getPacket();
        if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
            if (this.module.getPositions().remove(pos) && !this.module.isPingBypass()) {
                this.module.confirmed = true;
            }
            if (this.module.soundR.getValue().booleanValue()) {
                this.killEntities(pos);
            }
        }
    }

    private void killEntities(BlockPos pos) {
        mc.addScheduledTask(() -> {
            for (Entity entity : ListenerSound.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(pos) <= 36.0)) continue;
                this.module.getPositions().remove(PositionUtil.getPosition(entity));
                entity.setDead();
            }
        });
    }
}

