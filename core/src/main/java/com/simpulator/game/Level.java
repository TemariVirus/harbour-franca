package com.simpulator.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.simpulator.game.Trading.Item;
import com.simpulator.game.Trading.ItemPool;
import com.simpulator.game.Trading.ItemRarity;
import com.simpulator.game.Trading.PlayerInventory;

public class Level {
    public String levelId;
    public String displayName;
    public String skyboxPath;
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

    public void generateStarterInventory(PlayerInventory inventory) {
        inventory.clear();
        List<String> usedIds = new ArrayList<>();

        ItemRarity rarity = ItemPool.getRarityForValue(startInventoryValue / 3);
        List<Item> pool = new ArrayList<>(ItemPool.getPool(rarity));

        // Shuffle and pick first 3 unique items
        Collections.shuffle(pool, new Random());
        for (int i = 0; i < Math.min(3, pool.size()); i++) {
            Item item = pool.get(i);
            if (!usedIds.contains(item.getId())) {
                inventory.addItem(item);
                usedIds.add(item.getId());
            }
        }

        inventory.recalculateTotalValue();
    }
}