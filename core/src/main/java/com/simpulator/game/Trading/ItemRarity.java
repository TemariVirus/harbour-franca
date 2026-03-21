package com.simpulator.game.Trading;

public enum ItemRarity {
    COMMON(1),
    RARE(2),
    EPIC(3),
    LEGENDARY(4);

    private final int tier;

    ItemRarity(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}