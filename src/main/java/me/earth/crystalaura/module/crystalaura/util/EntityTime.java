/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package me.earth.crystalaura.module.crystalaura.util;

import net.minecraft.entity.Entity;

public class EntityTime {
    private final long time;
    private final Entity entity;

    public EntityTime(Entity entity, long time) {
        this.entity = entity;
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

