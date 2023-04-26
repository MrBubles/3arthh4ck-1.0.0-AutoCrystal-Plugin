//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\KHALED IBRAHIM\Desktop\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class FloodThread
        implements Runnable,
        Globals {
    private final CrystalAura module;
    private final EntityPlayer target;
    private BlockPos pos;
    private long last;

    public FloodThread(BlockPos pos, CrystalAura module) {
        this.pos = pos;
        this.module = module;
        this.target = EntityUtil.getClosestEnemy();
        this.last = System.currentTimeMillis();
        Earthhack.getLogger().info("wowza");
        module.shouldPlace = false;
    }

    @Override
    public void run() {
        while (true) {
            CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, this.getHand(), 0.5f, 1.0f, 0.5f);
            CPacketAnimation animation = new CPacketAnimation(this.getHand());
            FloodThread.mc.player.connection.sendPacket(place);
            FloodThread.mc.player.connection.sendPacket(animation);
            this.last = System.currentTimeMillis();
            if (!this.module.isEnabled() || this.target == null || this.module.shouldStop) {
                this.module.shouldPlace = true;
                this.module.floodService.shutdownNow();
                Earthhack.getLogger().info("unfortunate");
                this.module.floodService = null;
                this.module.currentRunnable = null;
                this.module.shouldStop = false;
                return;
            }
            try {
                Thread.sleep(this.module.floodDelay.getValue().intValue(), this.module.floodDelayNs.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private EnumHand getHand() {
        return FloodThread.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }
}

