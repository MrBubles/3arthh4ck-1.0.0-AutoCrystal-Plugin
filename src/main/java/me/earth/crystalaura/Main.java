package me.earth.crystalaura;

import me.earth.crystalaura.module.crystalaura.CrystalAura;
import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;

@SuppressWarnings("unused")
public class Main implements Plugin {
    @Override
    public void load() {
        try {

            Managers.MODULES.register(new CrystalAura());

        } catch (AlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }
}
