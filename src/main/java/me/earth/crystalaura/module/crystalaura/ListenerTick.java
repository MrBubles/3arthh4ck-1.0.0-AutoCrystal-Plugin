/*
 * Decompiled with CFR 0.150.
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.modes.ThreadMode;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerTick
        extends ModuleListener<CrystalAura, TickEvent> {
    public ListenerTick(CrystalAura module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        this.module.setTick(false);
        this.module.checkKilled();
        this.module.runNonRotateThread(ThreadMode.Tick);
    }
}

