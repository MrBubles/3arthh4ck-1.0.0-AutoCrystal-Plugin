package me.earth.crystalaura.module.crystalaura;

import me.earth.crystalaura.module.crystalaura.util.BreakData;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.misc.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

final class HelperBreak
        extends Wrapper<CrystalAura>
        implements Globals {
    public HelperBreak(CrystalAura module) {
        super(module);
    }

    public BreakData createData(List<EntityPlayer> players, List<Entity> crystals) {
        BreakData data = new BreakData();
        for (Entity crystal : crystals) {
            boolean safe;
            if (!this.isValid(crystal)) continue;
            float self = this.value.suicide.getValue() ? -1.0f : DamageUtil.calculate(crystal);
            float health = EntityUtil.getHealth(HelperBreak.mc.player);
            boolean bl = safe = (double) self < (double) health + 1.0;
            if ((safe || this.value.suicide.getValue().booleanValue()) && this.evaluate(data, crystal, players, self)) {
                data.setFallBack(crystal, self);
            }
            if (safe) continue;
            this.value.setUnsafe();
        }
        return data;
    }

    private boolean evaluate(BreakData data, Entity crystal, List<EntityPlayer> players, float self) {
        boolean validFallBack = self <= this.value.fallbackDmg.getValue().floatValue();
        boolean count = false;
        boolean countMin = false;
        Entity previous = data.getCrystal();
        float previousD = data.getDamage();
        for (EntityPlayer player : players) {
            if (!this.isValid(player, crystal)) continue;
            float damage = DamageUtil.calculate(crystal, player);
            boolean friend = Managers.FRIENDS.contains(player);
            if (this.value.noFriendP.getValue().booleanValue() && friend) {
                if (!(damage > EntityUtil.getHealth(player) + 1.0f)) continue;
                count = false;
                countMin = false;
                data.setCrystal(previous);
                data.setDamage(previousD);
                validFallBack = false;
                break;
            }
            if (friend || !this.counts(player, self, damage)) continue;
            if (!count) {
                boolean bl = count = damage > this.value.minDamage.getValue().floatValue() || this.value.countMin.getValue() && damage > this.value.minFP.getValue().floatValue();
            }
            if (!countMin) {
                boolean bl = countMin = damage > this.value.minFP.getValue().floatValue();
            }
            if (!(damage > data.getDamage())) continue;
            data.setCrystal(crystal);
            data.setDamage(damage);
        }
        if (count) {
            data.increment();
        }
        if (countMin) {
            data.incrementMinDmgCount();
        }
        return validFallBack;
    }

    private boolean isValid(Entity crystal) {
        if (crystal instanceof EntityEnderCrystal && !crystal.isDead) {
            double distance = HelperBreak.mc.player.getDistanceSq(crystal);
            if (distance > (double) MathUtil.square(this.value.breakRange.getValue().floatValue())) {
                return false;
            }
            if (distance > (double) MathUtil.square(this.value.breakTrace.getValue().floatValue())) {
                return HelperBreak.mc.player.canEntityBeSeen(crystal);
            }
            return true;
        }
        return false;
    }

    private boolean isValid(EntityPlayer player, Entity crystal) {
        if (player != null && !EntityUtil.isDead(player) && !player.equals(HelperBreak.mc.player)) {
            return player.getDistanceSq(crystal) <= (double) MathUtil.square(this.value.range.getValue().floatValue());
        }
        return false;
    }

    private boolean counts(EntityPlayer player, float self, float damage) {
        if (self > this.value.maxSelfB.getValue().floatValue() && damage > this.value.breakMinDmg.getValue().floatValue()) {
            float otherH = EntityUtil.getHealth(player);
            if (this.value.override.getValue().booleanValue() && damage > otherH + 1.0f) {
                return true;
            }
            if (this.value.suicide.getValue().booleanValue()) {
                return damage > this.value.minDamage.getValue().floatValue() || otherH < this.value.facePlace.getValue().floatValue() && damage > this.value.minFP.getValue().floatValue();
            }
            return false;
        }
        return damage > self;
    }
}

