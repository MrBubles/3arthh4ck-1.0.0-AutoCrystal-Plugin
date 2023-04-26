//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\KHALED IBRAHIM\Desktop\Minecraft-Deobfuscator3000-master\1.12 stable mappings"!

/*
 * Decompiled with CFR 0.150.
 */
package me.earth.crystalaura.module.crystalaura;

import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerDeath
        extends ModuleListener<CrystalAura, DeathEvent> {
    public ListenerDeath(CrystalAura module) {
        super(module, DeathEvent.class);
    }

    @Override
    public void invoke(DeathEvent event) {
        if (event.getEntity().equals(ListenerDeath.mc.player)) {
            this.module.reset();
        }
    }
}

