package me.earth.crystalaura.module.crystalaura.modes;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public enum Target implements Globals {
    Closest {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, double range) {
            return EntityUtil.getClosestEnemy(players);
        }
    },
    FOV {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, double range) {
            EntityPlayer closest = null;
            double closestAngle = 360.0;
            for (EntityPlayer player : players) {
                double angle;
                if (!EntityUtil.isValid(player, range) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle) || !(angle < (double) (mc.gameSettings.fovSetting / 2.0f)))
                    continue;
                closest = player;
                closestAngle = angle;
            }
            return closest;
        }
    },
    Angle {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, double range) {
            EntityPlayer closest = null;
            double closestAngle = 360.0;
            for (EntityPlayer player : players) {
                double angle;
                if (!EntityUtil.isValid(player, range) || !((angle = RotationUtil.getAngle(player, 1.4)) < closestAngle))
                    continue;
                closest = player;
                closestAngle = angle;
            }
            return closest;
        }
    },
    Damage {
        @Override
        public EntityPlayer getTarget(List<EntityPlayer> players, double range) {
            return null;
        }
    };


    public abstract EntityPlayer getTarget(List<EntityPlayer> var1, double var2);
}