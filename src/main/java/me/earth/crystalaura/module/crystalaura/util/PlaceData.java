/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 */
package me.earth.crystalaura.module.crystalaura.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PlaceData {
    private final List<Entity> entities;
    private float damage;
    private float selfDamage = -2.0f;
    private EntityPlayer target;
    private BlockPos pos;

    public PlaceData(EntityPlayer target, List<Entity> crystals) {
        this.target = target;
        this.entities = crystals;
    }

    public EntityPlayer getTarget() {
        return this.target;
    }

    public void setTarget(EntityPlayer target) {
        this.target = target;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public float getSelfDamage() {
        return this.selfDamage;
    }

    public void setSelfDamage(float selfDamage) {
        this.selfDamage = selfDamage;
    }

    public List<Entity> getEntities() {
        return this.entities;
    }
}

