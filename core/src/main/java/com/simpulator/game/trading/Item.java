package com.simpulator.game.trading;

import com.badlogic.gdx.graphics.Texture;
import com.simpulator.engine.scene.TextureCache;

public enum Item {
    // Common
    TRINKET("Trinket", 5, "Items/trinket.png"),
    ROPE("Rope", 9, "Items/rope.png"),
    CLOTH("Cloth", 7, "Items/cloth.png"),
    CANDLE("Candle", 8, "Items/candle.png"),
    FISH("Dried Fish", 6, "Items/fish.png"),
    // Rare
    PENDANT("Pendant", 25, "Items/pendant.png"),
    COMPASS("Compass", 28, "Items/compass.png"),
    SPICE("Spice Jar", 26, "Items/spice.png"),
    MAP("Old Map", 30, "Items/map.png"),
    LENS("Glass Lens", 27, "Items/lens.png"),
    // Epic
    GEMSTONE("Gemstone", 50, "Items/gemstone.png"),
    IDOL("Gold Idol", 55, "Items/idol.png"),
    SEXTANT("Sextant", 52, "Items/sextant.png"),
    CHALICE("Chalice", 53, "Items/chalice.png"),
    ASTROLABE("Astrolabe", 55, "Items/astrolabe.png"),
    // Legendary
    DRAGON_SCALE("Dragon Scale", 100, "Items/dragon_scale.png"),
    CROWN("Ancient Crown", 105, "Items/crown.png"),
    ORB("Mystic Orb", 110, "Items/orb.png");

    private final String name;
    private final ItemRarity rarity;
    private final int value;
    private final String imagePath;

    Item(String name, int value, String imagePath) {
        this.name = name;
        if (value >= 100) {
            this.rarity = ItemRarity.LEGENDARY;
        } else if (value >= 50) {
            this.rarity = ItemRarity.EPIC;
        } else if (value >= 25) {
            this.rarity = ItemRarity.RARE;
        } else {
            this.rarity = ItemRarity.COMMON;
        }
        this.value = value;
        this.imagePath = imagePath;
    }

    Item(String name, ItemRarity rarity, int value, String imagePath) {
        this.name = name;
        this.rarity = rarity;
        this.value = value;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name;
    }

    public ItemRarity rarity() {
        return rarity;
    }

    public int value() {
        return value;
    }

    public String imagePath() {
        return imagePath;
    }

    public Texture getTexture(TextureCache textures) {
        return textures.get(imagePath);
    }
}
