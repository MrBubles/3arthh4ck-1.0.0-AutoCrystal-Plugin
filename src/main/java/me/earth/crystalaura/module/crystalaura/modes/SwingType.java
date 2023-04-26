/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.util.EnumHand
 */
package me.earth.crystalaura.module.crystalaura.modes;

import net.minecraft.util.EnumHand;

public enum SwingType {
    None {
        @Override
        public EnumHand getHand() {
            return null;
        }
    },
    MainHand {
        @Override
        public EnumHand getHand() {
            return EnumHand.MAIN_HAND;
        }
    },
    OffHand {
        @Override
        public EnumHand getHand() {
            return EnumHand.OFF_HAND;
        }
    };


    public abstract EnumHand getHand();
}

