package com.simpulator.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.simpulator.game.Trading.Item;
import com.simpulator.game.Trading.ItemPool;
import com.simpulator.game.Trading.ItemRarity;
import com.simpulator.game.Trading.PlayerInventory;

public class Level {
    public String levelId;
    public String displayName;
    public String skyboxTexturePrefix;
    public String backgroundMusic;
    public int startInventoryValue;

    public float playerStartX;
    public float playerStartY;
    public float playerStartZ;

    public List<EntityConfig> entitiesToSpawn = new ArrayList<>();

    public static class EntityConfig {
        public String entityType;
        public float x, y, z;

        public EntityConfig(String type, float x, float y, float z) {
            this.entityType = type;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public void generateStarterInventory(PlayerInventory inventory, Random rng) {
        inventory.clear();
        List<String> usedIds = new ArrayList<>();

        int perItem = startInventoryValue / 3;
        int remainder = startInventoryValue % 3;

        for (int i = 0; i < 3; i++) {
            int targetValue = (i == 2) ? perItem + remainder : perItem;
            ItemRarity rarity = getRarityForValue(targetValue);
            Item item = drawClosest(rarity, targetValue, rng, usedIds); // real pool item

            inventory.addItem(item);
        }

        inventory.recalculateTotalValue();
    }

    private Item drawClosest(ItemRarity rarity, int targetValue, Random rng, List<String> usedIds) {
        List<Item> pool = new ArrayList<>(ItemPool.getPool(rarity));
        pool.removeIf(item -> usedIds.contains(item.getId()));
        if (pool.isEmpty())
            pool = new ArrayList<>(ItemPool.getPool(rarity));

        Item closest = pool.stream()
                .min(Comparator.comparingInt(i -> Math.abs(i.getValue() - targetValue)))
                .get();

        usedIds.add(closest.getId());
        return closest;
    }

    private ItemRarity getRarityForValue(int value) {
        if (value >= 50)
            return ItemRarity.EPIC;
        if (value >= 25)
            return ItemRarity.RARE;
        return ItemRarity.COMMON;
    }

}