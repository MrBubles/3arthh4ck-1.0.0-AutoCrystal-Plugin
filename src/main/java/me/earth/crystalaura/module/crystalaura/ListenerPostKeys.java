/*
 * Decompiled with CFR 0.150.
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.modes.ThreadMode;
import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPostKeys
        extends ModuleListener<CrystalAura, KeyboardEvent.Post> {
    public ListenerPostKeys(CrystalAura module) {
        super(module, KeyboardEvent.Post.class);
    }

    @Override
    public void invoke(KeyboardEvent.Post event) {
        this.module.runNonRotateThread(ThreadMode.Keys);
    }
}

