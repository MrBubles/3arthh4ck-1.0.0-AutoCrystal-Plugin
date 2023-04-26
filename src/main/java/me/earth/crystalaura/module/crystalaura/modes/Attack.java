package me.earth.crystalaura.module.crystalaura.modes;

import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

public enum Attack {
    Always {
        @Override
        public boolean shouldCalc() {
            return true;
        }

        @Override
        public boolean shouldAttack() {
            return true;
        }
    },
    BreakSlot {
        @Override
        public boolean shouldCalc() {
            return InventoryUtil.isHolding(Items.END_CRYSTAL);
        }

        @Override
        public boolean shouldAttack() {
            return InventoryUtil.isHolding(Items.END_CRYSTAL);
        }
    },
    Calc {
        @Override
        public boolean shouldCalc() {
            return true;
        }

        @Override
        public boolean shouldAttack() {
            return InventoryUtil.isHolding(Items.END_CRYSTAL);
        }
    };


    public abstract boolean shouldCalc();

    public abstract boolean shouldAttack();
}

