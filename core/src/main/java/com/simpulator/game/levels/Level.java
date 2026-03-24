package com.simpulator.game.levels;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.ExploreScene.MerchantEntity;
import com.simpulator.game.trading.Inventory;
import com.simpulator.game.trading.Item;
import java.util.ArrayList;

// TODO: encapsulation
public class Level {

    public static class MerchantConfig {

        public Class<? extends MerchantEntity> merchantClass;
        public Vector3 position;
        public MerchantData data;

        public MerchantConfig(
            Class<? extends MerchantEntity> merchantClass,
            Vector3 position,
            MerchantData data
        ) {
            this.merchantClass = merchantClass;
            this.position = position;
            this.data = data;
        }
    }

    public String levelId;
    public String displayName;

    public String skyboxPath;
    public String bgmPath;

    public int valueGoal;
    public Item[] startingItems;

    public Vector3 playerStart;
    public LevelMap map;
    public MerchantConfig[] merchants;

    public Inventory createInventory() {
        Inventory inventory = new Inventory();
        for (Item item : startingItems) {
            inventory.add(item);
        }
        return inventory;
    }

    public ArrayList<MerchantEntity> createMerchants(
        TextureCache textures,
        Entity player
    ) {
        ArrayList<MerchantEntity> outEntities = new ArrayList<>();
        for (MerchantConfig config : merchants) {
            try {
                MerchantEntity merchant = config.merchantClass
                    .getConstructor(
                        Vector3.class,
                        TextureCache.class,
                        MerchantData.class,
                        Entity.class
                    )
                    .newInstance(
                        config.position,
                        textures,
                        config.data,
                        player
                    );
                outEntities.add(merchant);
            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to spawn merchant of class " +
                        config.merchantClass.getName(),
                    e
                );
            }
        }
        return outEntities;
    }
}
