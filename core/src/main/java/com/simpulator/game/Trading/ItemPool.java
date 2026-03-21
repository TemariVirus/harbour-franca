package com.simpulator.game.Trading;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ItemPool {
    public static final List<Item> COMMON_POOL = List.of(
            new Item("npc_trinket", "Trinket", ItemRarity.COMMON, 5),
            new Item("npc_rope", "Rope", ItemRarity.COMMON, 9),
            new Item("npc_cloth", "Cloth", ItemRarity.COMMON, 7),
            new Item("npc_candle", "Candle", ItemRarity.COMMON, 8),
            new Item("npc_fish", "Dried Fish", ItemRarity.COMMON, 6));

    public static final List<Item> RARE_POOL = List.of(
            new Item("npc_pendant", "Pendant", ItemRarity.RARE, 25),
            new Item("npc_compass", "Compass", ItemRarity.RARE, 28),
            new Item("npc_spice", "Spice Jar", ItemRarity.RARE, 26),
            new Item("npc_map", "Old Map", ItemRarity.RARE, 30),
            new Item("npc_lens", "Glass Lens", ItemRarity.RARE, 27));

    public static final List<Item> EPIC_POOL = List.of(
            new Item("npc_gemstone", "Gemstone", ItemRarity.EPIC, 50),
            new Item("npc_idol", "Gold Idol", ItemRarity.EPIC, 55),
            new Item("npc_sextant", "Sextant", ItemRarity.EPIC, 52),
            new Item("npc_chalice", "Chalice", ItemRarity.EPIC, 53),
            new Item("npc_astrolabe", "Astrolabe", ItemRarity.EPIC, 55));

    public static final List<Item> LEGENDARY_POOL = List.of(
            new Item("npc_dragon_scale", "Dragon Scale", ItemRarity.LEGENDARY, 100),
            new Item("npc_crown", "Ancient Crown", ItemRarity.LEGENDARY, 105),
            new Item("npc_orb", "Mystic Orb", ItemRarity.LEGENDARY, 110));

    public static List<Item> getPool(ItemRarity rarity) {
        switch (rarity) {
            case RARE:
                return RARE_POOL;
            case EPIC:
                return EPIC_POOL;
            case LEGENDARY:
                return LEGENDARY_POOL;
            default:
                return COMMON_POOL;
        }
    }

    public static Item drawUnique(ItemRarity rarity, Random rng, List<String> usedIds) {
        List<Item> pool = new ArrayList<>(getPool(rarity));
        pool.removeIf(item -> usedIds.contains(item.getId()));

        if (pool.isEmpty()) {
            // Fallback: all used, reset and allow repeats
            pool = new ArrayList<>(getPool(rarity));
        }

        Item chosen = pool.get(rng.nextInt(pool.size()));
        usedIds.add(chosen.getId());
        return chosen;
    }

    public static Item drawClosest(ItemRarity rarity, int targetValue, List<String> usedIds) {
        List<Item> pool = new ArrayList<>(getPool(rarity));
        pool.removeIf(item -> usedIds.contains(item.getId()));
        if (pool.isEmpty())
            pool = new ArrayList<>(getPool(rarity));

        return pool.stream()
                .min(Comparator.comparingInt(i -> Math.abs(i.getValue() - targetValue)))
                .get();
    }

    public static ItemRarity getRarityForValue(int value) {
        if (value >= 50)
            return ItemRarity.EPIC;
        if (value >= 25)
            return ItemRarity.RARE;
        return ItemRarity.COMMON;
    }
}