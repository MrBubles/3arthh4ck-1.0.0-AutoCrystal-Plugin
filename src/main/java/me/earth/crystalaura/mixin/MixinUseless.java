package me.earth.crystalaura.mixin;

import me.earth.earthhack.impl.Earthhack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

@Mixin(value = Earthhack.class, remap = false)
public class MixinUseless {
    @Inject(method = "init", at = @At("HEAD"))
    private static void initHook(CallbackInfo info) {
        System.out.println("Welcome to 3arthh4ck " + mc.player.getName());
    }
}
