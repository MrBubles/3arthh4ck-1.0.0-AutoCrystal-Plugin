//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\KHALED IBRAHIM\Desktop\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.modes.Attack;
import me.earth.crystalaura.module.crystalaura.modes.AutoSwitch;
import me.earth.crystalaura.module.crystalaura.modes.Rotate;
import me.earth.crystalaura.module.crystalaura.util.*;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

final class Calculation
        extends Wrapper<CrystalAura>
        implements Runnable,
        Globals {
    private static final ModuleCache<LegSwitch> LEG_SWITCH = Caches.getModule(LegSwitch.class);
    private static final ModuleCache<Offhand> OFFHAND = Caches.getModule(Offhand.class);
    private final List<Packet<?>> packets = new CopyOnWriteArrayList();
    private final List<EntityPlayer> players;
    private final List<Entity> crystals;
    private final HelperBreak breakHelper;
    private final HelperPlace placeHelper;
    private final Random random = new Random();
    private BlockPos pos;
    private EntityPlayer target;
    private Entity crystal;
    private float[] rotations;
    private boolean attacking;
    private BreakData breakData;
    private boolean doneRotating;

    public Calculation(CrystalAura module, List<EntityPlayer> players, List<Entity> crystals) {
        super(module);
        this.players = players;
        this.crystals = crystals;
        this.breakHelper = module.breakHelper;
        this.placeHelper = module.placeHelper;
    }

    @Override
    public void run() {
        if (this.value.legSwitch.getValue().booleanValue() && LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false).booleanValue()) {
            return;
        }
        if ((this.value.attack.getValue().shouldCalc() || this.value.isSwitching()) && Managers.SWITCH.getLastSwitch() >= (long) this.value.cooldown.getValue().intValue()) {
            float damage;
            PlaceData data;
            boolean flag = false;
            this.value.setSafe = false;
            int count = this.explode();
            if (count != 6 && this.value.place.getValue().booleanValue() && this.value.getPlaceTimer().passed(this.value.placeDelay.getValue().intValue()) && (count < this.value.multiPlace.getValue() || this.value.antiSurr.getValue().booleanValue()) && this.shouldPlaceCalc()) {
                float damage2;
                PlaceData data2 = this.placeHelper.createData(this.players, this.crystals);
                if (this.checkPos(data2.getPos(), count) && ((damage2 = data2.getDamage()) > this.value.minDamage.getValue().floatValue() || EntityUtil.getHealth(data2.getTarget()) <= this.value.facePlace.getValue().floatValue() && (!this.value.noFaceSpam.getValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) || this.value.shouldFacePlace() && (!this.value.noFaceSpam.getValue().booleanValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) && (double) damage2 > 2.0)) {
                    this.target = data2.getTarget();
                    this.place(data2);
                    flag = true;
                }
            } else if (this.value.place.getValue().booleanValue() && this.value.useForPlace.getValue().booleanValue() && !flag && this.shouldPlaceCalc() && this.checkPos((data = this.placeHelper.createData(this.players, this.crystals)).getPos(), count) && ((damage = data.getDamage()) > this.value.minDamage.getValue().floatValue() || EntityUtil.getHealth(data.getTarget()) <= this.value.facePlace.getValue().floatValue() && (!this.value.noFaceSpam.getValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) || this.value.shouldFacePlace() && (!this.value.noFaceSpam.getValue().booleanValue() || this.attacking || this.breakData != null && this.breakData.getMinDmgCount() == 0) && (double) damage > 2.0)) {
                this.target = data.getTarget();
                this.updatePlaceRotations(data);
            }
        }
    }

    public boolean isRotating() {
        return this.rotations != null;
    }

    public float[] getRotations() {
        return this.rotations;
    }

    public List<Packet<?>> getPackets() {
        return this.packets;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    private int explode() {
        this.breakData = this.breakHelper.createData(this.players, this.crystals);
        this.crystal = this.breakData.getCrystal();
        if (!(!this.attack(this.crystal, this.breakData.getDamage()) || this.value.rotate.getValue().noRotate(Rotate.Place) && this.value.multiTask.getValue().booleanValue())) {
            return 6;
        }
        return this.breakData.getCount();
    }

    private boolean attack(Entity crystal, float damage) {
        block13:
        {
            block18:
            {
                CPacketAnimation animation;
                CPacketUseEntity useEntity;
                block16:
                {
                    block17:
                    {
                        block15:
                        {
                            block14:
                            {
                                int delay = damage <= this.value.slowBreak.getValue().floatValue() ? this.value.slowDelay.getValue().intValue() : this.value.breakDelay.getValue().intValue();
                                EntityPlayer closest = EntityUtil.getClosestEnemy();
                                if (crystal != null && closest != null && this.value.getTarget() != null && this.value.slowLegBreak.getValue().booleanValue() && PlayerUtil.isValidFootCrystal(crystal, closest) && PlayerUtil.isInHole(closest)) {
                                    delay = this.value.legDelay.getValue();
                                }
                                if (!this.value.explode.getValue().booleanValue() || crystal == null || !this.value.attack.getValue().shouldAttack() || !this.value.getBreakTimer().passed(delay))
                                    break block13;
                                mc.addScheduledTask(() -> this.value.setCurrentCrystal(crystal));
                                useEntity = new CPacketUseEntity(crystal);
                                animation = new CPacketAnimation(EnumHand.MAIN_HAND);
                                this.value.getBreakTimer().reset(delay);
                                this.attacking = true;
                                if (this.value.multiThread.getValue().booleanValue() && this.value.rotate.getValue() == Rotate.None) {
                                    mc.addScheduledTask(((CrystalAura) this.value)::swing);
                                }
                                if (this.value.setDead.getValue().booleanValue() && !this.value.useYawLimit.getValue().booleanValue()) {
                                    crystal.setDead();
                                    if (this.value.dangerous.getValue().booleanValue()) {
                                        Calculation.mc.world.removeEntityDangerously(crystal);
                                    }
                                    this.value.killed.put(crystal.getEntityId(), new EntityTime(crystal, System.nanoTime()));
                                }
                                if (this.value.rotate.getValue().noRotate(Rotate.Break) || RotationUtil.isLegit(crystal) || RotationUtil.isLegitRaytrace(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()))
                                    break block14;
                                if (!Arrays.equals(new int[]{Math.round(Managers.ROTATION.getServerYaw()), Math.round(Managers.ROTATION.getServerPitch())}, new int[]{Math.round(MathHelper.wrapDegrees(RotationUtil.getRotations(crystal)[0])), Math.round(RotationUtil.getRotations(crystal)[1])}))
                                    break block15;
                            }
                            boolean flag = false;
                            int toolSlot = InventoryUtil.findHotbarItem(Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE);
                            int lastSlot = Calculation.mc.player.inventory.currentItem;
                            if (!DamageUtil.canBreakWeakness(true) && toolSlot != -1) {
                                InventoryUtil.switchTo(toolSlot);
                                flag = true;
                            }
                            Calculation.mc.player.connection.sendPacket(useEntity);
                            Calculation.mc.player.connection.sendPacket(animation);
                            if (flag) {
                                InventoryUtil.switchTo(lastSlot);
                            }
                            return !this.value.multiTask.getValue();
                        }
                        if (!this.value.useYawLimit.getValue().booleanValue()) break block16;
                        float[] rotation = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
                        float[] target = RotationUtil.getRotations(crystal);
                        this.rotations = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
                        if (Arrays.equals(this.rotations, RotationUtil.getRotations(crystal)) || RotationUtil.isLegitRaytrace(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()) || RotationUtil.isLegit(crystal, Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch()))
                            break block17;
                        if (!Arrays.equals(new int[]{Math.round(Managers.ROTATION.getServerYaw()), Math.round(Managers.ROTATION.getServerPitch())}, new int[]{Math.round(MathHelper.wrapDegrees(RotationUtil.getRotations(crystal)[0])), Math.round(RotationUtil.getRotations(crystal)[1])}))
                            break block18;
                    }
                    this.packets.add(useEntity);
                    this.packets.add(animation);
                    break block18;
                }
                this.rotations = RotationUtil.getRotations(crystal);
                this.packets.add(useEntity);
                this.packets.add(animation);
            }
            return true;
        }
        if (this.value.explode.getValue().booleanValue() && crystal != null && !this.value.rotate.getValue().noRotate(Rotate.Break) && this.value.useYawLimit.getValue().booleanValue()) {
            this.rotations = RotationUtil.getRotationsMaxYaw(crystal, (float) (this.value.limit.getValue() + (this.random.nextBoolean() ? -this.random.nextInt(this.value.jitter.getValue()) : this.random.nextInt(this.value.jitter.getValue()))), Managers.ROTATION.getServerYaw());
            return false;
        }
        return false;
    }

    private void place(PlaceData data) {
        if (InventoryUtil.isHolding(Items.END_CRYSTAL) && this.value.shouldPlace) {
            RayTraceResult result;
            this.pos = data.getPos();
            if (!this.value.rotate.getValue().noRotate(Rotate.Place)) {
                float[] rotation = RotationUtil.getRotations(this.pos.up(), EnumFacing.UP);
                if (this.value.useForPlace.getValue().booleanValue()) {
                    this.rotations = RotationUtil.getRotationsMaxYaw(this.pos.up(), (float) this.value.limit.getValue().intValue(), Managers.ROTATION.getServerYaw());
                    result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
                } else {
                    this.rotations = RotationUtil.getRotations(this.pos.up(), EnumFacing.UP);
                    result = RayTraceUtil.getRayTraceResult(this.rotations[0], this.rotations[1], this.value.placeRange.getValue().floatValue());
                }
            } else {
                result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            }
            if (data.getDamage() < this.value.minDamage.getValue().floatValue() && this.value.shouldFacePlace()) {
                this.value.slow.add(this.pos.up());
            }
            CPacketPlayerTryUseItemOnBlock place = new CPacketPlayerTryUseItemOnBlock(this.pos, result.sideHit, this.getHand(), (float) result.hitVec.x, (float) result.hitVec.y, (float) result.hitVec.z);
            CPacketAnimation animation = new CPacketAnimation(this.getHand());
            this.value.getPlaceTimer().reset(this.value.placeDelay.getValue().intValue());
            this.value.getPositions().add(this.pos.up());
            if ((this.value.rotate.getValue().noRotate(Rotate.Place) || RotationUtil.isLegit(this.pos)) && this.packets.isEmpty()) {
                InventoryUtil.syncItem();
                Calculation.mc.player.connection.sendPacket(place);
                Calculation.mc.player.connection.sendPacket(animation);
            } else if (this.value.useForPlace.getValue().booleanValue() && RotationUtil.isLegit(this.pos)) {
                this.packets.add(place);
                this.packets.add(animation);
            } else if (!this.value.useForPlace.getValue().booleanValue()) {
                this.packets.add(place);
                this.packets.add(animation);
            }
            this.setRenderPos(data);
        } else if (this.value.isSwitching()) {
            Runnable runnable = () -> {
                if (this.value.mainHand.getValue().booleanValue()) {
                    int slot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
                    InventoryUtil.switchTo(slot);
                } else {
                    OFFHAND.computeIfPresent(o -> o.setMode(OffhandMode.CRYSTAL));
                }
                this.value.setSwitching(false);
            };
            mc.addScheduledTask(() -> {
                this.value.postRunnable = runnable;
                return this.value.postRunnable;
            });
        }
    }

    private void updatePlaceRotations(PlaceData data) {
        if (InventoryUtil.isHolding(Items.END_CRYSTAL) && BlockUtil.canPlaceCrystal(data.getPos(), false, false)) {
            this.pos = data.getPos();
            if (!this.value.rotate.getValue().noRotate(Rotate.Place)) {
                float[] rotation = RotationUtil.getRotations(this.pos.up(), EnumFacing.UP);
                if (this.value.useForPlace.getValue().booleanValue()) {
                    this.rotations = RotationUtil.getRotationsMaxYaw(this.pos.up(), (float) this.value.limit.getValue().intValue(), Managers.ROTATION.getServerYaw());
                    RayTraceResult result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
                } else {
                    this.rotations = RotationUtil.getRotations(this.pos.up(), EnumFacing.UP);
                    RayTraceResult result = RayTraceUtil.getRayTraceResult(this.rotations[0], this.rotations[1], this.value.placeRange.getValue().floatValue());
                }
            } else {
                RayTraceResult result = new RayTraceResult(new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP);
            }
            if (data.getDamage() < this.value.minDamage.getValue().floatValue() && this.value.shouldFacePlace()) {
                this.value.slow.add(this.pos.up());
            }
        }
    }

    private boolean checkPos(BlockPos pos, int count) {
        boolean rotating = false;
        if (!this.attacking && this.value.fallBack.getValue().booleanValue()) {
            Entity fallBack = this.breakData.getFallBack();
            if (this.value.antiSurr.getValue().booleanValue()) {
                if (pos != null) {
                    for (Entity entity : this.crystals) {
                        BlockPos entityPos;
                        if (!(entity instanceof EntityEnderCrystal) || entity.isDead || !entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.up())) && (this.value.newerVer.getValue().booleanValue() || !entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.up(2)))) || (entityPos = PositionUtil.getPosition(entity)).equals(pos.up()))
                            continue;
                        if (fallBack != null) {
                            boolean bl = rotating = this.attack(fallBack, this.breakData.getFallBackDamage()) && !this.value.rotate.getValue().noRotate(Rotate.Place) && !RotationUtil.isLegit(pos);
                        }
                        if (fallBack == null || !this.attacking) {
                            return false;
                        }
                        break;
                    }
                }
            } else if (pos == null && fallBack != null) {
                this.attack(fallBack, this.breakData.getFallBackDamage());
                return false;
            }
        }
        return pos != null && !rotating && (count < this.value.multiPlace.getValue() || this.value.antiSurr.getValue() && !BlockUtil.canPlaceCrystal(pos, false, this.value.newerVer.getValue()));
    }

    private boolean shouldPlaceCalc() {
        return InventoryUtil.isHolding(Items.END_CRYSTAL) || this.value.attack.getValue() == Attack.Calc || this.value.autoSwitch.getValue() == AutoSwitch.Always || this.value.isSwitching();
    }

    private EnumHand getHand() {
        return Calculation.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    private void setRenderPos(PlaceData data) {
        if (this.value.multiThread.getValue().booleanValue() && this.value.rotate.getValue() == Rotate.None) {
            mc.addScheduledTask(() -> {
                this.value.setRenderPos(data.getPos());
                this.value.setTarget(data.getTarget());
            });
        }
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean isAttacking() {
        return this.attacking;
    }

    public Entity getCrystal() {
        return this.crystal;
    }
}

