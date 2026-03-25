package com.simpulator.game.levels;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.Level.MerchantConfig;
import com.simpulator.game.levels.maps.MiramarMap;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.simpulator.game.levels.maps.Level1Map;

public class LevelManager {

    private Map<String, Level> levelDatabase = new HashMap<>();
    private String currentLevelId;

    public LevelManager() {
        // Hardcoded Level
        initLevels();
    }

    // TODO: create enum for level ids
    private void initLevels() {
        // Level 0 - Tutorial
        Level tutorialLevel = new TutorialLevel();
        levelDatabase.put(tutorialLevel.levelId, tutorialLevel);

        // Level 1
        Level level1 = new Level();
        level1.levelId = "level_01";
        level1.nextLevelId = null;
        level1.displayName = "The Beginning";

        level1.skyboxPath = "Skyboxes/miramar";
        level1.bgmPath = "GameAudio.mp3";

        level1.valueGoal = 50;
        level1.startingItems = new Item[] {
            Item.FISH,
            Item.CLOTH,
            Item.CANDLE,
        };

        level1.playerStart = new Vector3(14, 1, 8); // Centers the player in the room        
        level1.map = new Level1Map();
        
        level1.merchants = new MerchantConfig[] {
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(6, 0, 2),
                new MerchantData(
                    "Chinese Merchant",
                    "Lam.jpeg",
                    Language.CHINESE,
                    "我很饿。有食物的话我什么都愿意给！",
                    "哦！太好了！谢谢你！这个能多吃六月！",
                    "谢谢你的购买！",
                    "我才没有那么笨！给我滚！",
                    "如果你不打算买的话就别浪费我的时间了！",
                    10f,
                    2f,
                    new Item[] { Item.MAP, Item.CANDLE, Item.SPICE },
                    new HashSet<>(Arrays.asList(Item.FISH))
                )
            ),
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(14, 0, 2),
                new MerchantData(
                    "Vietnamese Merchant",
                    "Nisha.jpeg",
                    Language.VIETNAMESE,
                    "Có dây thừng không? Có người thích kiểu\nchơi *đó* đấy.",
                    "Ồ, tuyệt quá! Cảm ơn nhé. Chắc chủ nhân của tôi sẽ rất thích món này.",
                    "Cảm ơn bạn đã mua hàng!",
                    "Hơi bị hớ đấy nhỉ? Đồ của tôi đáng giá hơn thế nhiều.",
                    "Nếu bạn không định mua thì đừng có lãng phí thời gian của tôi!",
                    8f,
                    2f,
                    new Item[] { Item.PENDANT, Item.CANDLE, Item.FISH },
                    new HashSet<>(Arrays.asList(Item.ROPE))
                )
            ),
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(22, 0, 2),
                new MerchantData(
                    "Japanese Merchant",
                    "Oran.jpeg",
                    Language.JAPANESE,
                    "家にもうちょっと光が欲しいなあ。",
                    "おお！これはいいね！",
                    "毎度ありがとうございます。",
                    "お前さあ…こんなゴミで商売してんの？",
                    "商売するつもりがないならさっさと消えろ！",
                    9f,
                    2f,
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
