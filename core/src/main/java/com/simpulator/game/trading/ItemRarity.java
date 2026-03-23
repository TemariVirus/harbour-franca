package com.simpulator.game.trading;

import com.badlogic.gdx.graphics.Color;

public enum ItemRarity {
    COMMON(1, "Common", Color.LIGHT_GRAY),
    RARE(2, "Rare", Color.ROYAL),
    EPIC(3, "Epic", Color.PURPLE),
    LEGENDARY(4, "Legendary", Color.GOLD);

    private final int tier;
    private final String name;
    private final Color color;

    ItemRarity(int tier, String name, Color color) {
        this.tier = tier;
        this.name = name;
        this.color = color;
    }

    /** The rarity tier. Higher tiers are generally more valuable. */
    public int tier() {
        return tier;
    }

    @Override
    public String toString() {
        return name;
    }

    public Color color() {
        return color;
    }
}
