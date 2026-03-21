package com.simpulator.game.Trading;

public class Item {
    private final String id;
    private final String name;
    private final ItemRarity rarity;
    private final int value;

    public Item(String id, String name, ItemRarity rarity, int value) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public int getValue() {
        return value;
    }
}