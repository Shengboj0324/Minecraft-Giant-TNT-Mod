package com.yourname.firearmsmod.items;

import net.minecraft.world.item.Item;

public class AmmoItem extends Item {
    private final AmmoType type;

    public enum AmmoType {
        PISTOL(8.0f),
        RIFLE(12.0f),
        SNIPER(25.0f),
        SHOTGUN(15.0f);

        private final float damage;

        AmmoType(float damage) {
            this.damage = damage;
        }

        public float getDamage() {
            return damage;
        }
    }

    public AmmoItem(Properties properties, AmmoType type) {
        super(properties);
        this.type = type;
    }

    public AmmoType getAmmoType() {
        return type;
    }

    public float getDamage() {
        return type.getDamage();
    }
} 