package com.simpulator.game.levels;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.game.ExploreScene.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.Level.MerchantConfig;
import com.simpulator.game.levels.maps.MiramarMap;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LevelManager {

    private Map<String, Level> levelDatabase = new HashMap<>();
    private String currentLevelId;

    public LevelManager() {
        // Hardcoded Level
        initLevels();
    }

    private void initLevels() {
        // Level 1
        Level level1 = new Level();
        level1.levelId = "level_01";
        level1.displayName = "The Beginning";

        level1.skyboxPath = "Skyboxes/miramar";
        level1.bgmPath = "GameAudio.mp3";

        level1.valueGoal = 50;
        level1.wantsThreshold = 4f;
        level1.normalThreshold = 1.4f;
        level1.startingItems = new Item[] {
            Item.FISH,
            Item.CLOTH,
            Item.CANDLE,
        };

        level1.playerStart = new Vector3(0, 1, 0);
        level1.map = new MiramarMap();
        level1.merchants = new MerchantConfig[] {
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(2, 0, 2),
                new MerchantData(
                    "Chinese Merchant",
                    "Lam.jpeg",
                    Language.CHINESE,
                    "我很饿。有食物的话我什么都愿意给！",
                    new Item[] { Item.MAP, Item.CANDLE, Item.SPICE },
                    new HashSet<>(Arrays.asList(Item.FISH))
                )
            ),
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(-2, 0, 2),
                new MerchantData(
                    "Vietnamese Merchant",
                    "Nisha.jpeg",
                    Language.VIETNAMESE,
                    "Có dây thừng không? Có người thích kiểu\nchơi *đó* đấy.",
                    new Item[] { Item.PENDANT, Item.CANDLE, Item.FISH },
                    new HashSet<>(Arrays.asList(Item.ROPE))
                )
            ),
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(0, 0, -2),
                new MerchantData(
                    "Japanese Merchant",
                    "Oran.jpeg",
                    Language.JAPANESE,
                    "家にもうちょっと光が欲しいなあ。",
                    new Item[] { Item.ROPE, Item.LENS, Item.COMPASS },
                    new HashSet<>(Arrays.asList(Item.CANDLE))
                )
            ),
        };

        levelDatabase.put(level1.levelId, level1);
    }

    public Level getLevel(String levelId) {
        return levelDatabase.get(levelId);
    }

    public void setCurrentLevelId(String levelId) {
        this.currentLevelId = levelId;
    }

    public Level getCurrentLevel() {
        return levelDatabase.get(currentLevelId);
    }
}
