package com.simpulator.game.levels;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.Level.MerchantConfig;
import com.simpulator.game.levels.maps.Level1Map;
import com.simpulator.game.levels.maps.Level2Map;
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

    // TODO: create enum for level ids
    private void initLevels() {
        // Level 0 - Tutorial
        Level tutorialLevel = new TutorialLevel();
        levelDatabase.put(tutorialLevel.levelId, tutorialLevel);

        // Level 1
        Level level1 = new Level();
        level1.levelId = "level_01";
        level1.nextLevelId = "level_02";
        level1.displayName = "The Beginning";

        level1.skyboxPath = "Skyboxes/miramar";
        level1.bgmPath = "GameAudio.ogg";

        level1.valueGoal = 50;
        level1.levelHint = "HINT: Chinese wants Fish | Vietnamese wants Rope | Japanese wants Candle";
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
                        "zh-merchant.png",
                        Language.CHINESE,
                        "我很饿。有食物的话我什么都愿意给！",
                        "哦！太好了！谢谢你！这个能多吃六月！",
                        "谢谢你的购买！",
                        "我才没有那么笨！给我滚！",
                        "如果你不打算买的话就别浪费我的时间了！",
                        10f, 2f,
                        new Item[] { Item.MAP, Item.CANDLE, Item.SPICE },
                        new HashSet<>(Arrays.asList(Item.FISH)),
                        "(I'll give anything for food!)" //HINT
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(14, 0, 2),
                    new MerchantData(
                        "Vietnamese Merchant",
                        "vi-merchant.png",
                        Language.VIETNAMESE,
                        "Có dây thừng không? Có người thích kiểu\nchơi *đó* đấy.",
                        "Ồ, tuyệt quá! Cảm ơn nhé.",
                        "Cảm ơn bạn đã mua hàng!",
                        "Hơi bị hớ đấy nhỉ? Đồ của tôi đáng giá hơn thế.",
                        "Nếu bạn không định mua thì đừng có lãng phí thời gian!",
                        8f, 2f,
                        new Item[] { Item.PENDANT, Item.CANDLE, Item.FISH },
                        new HashSet<>(Arrays.asList(Item.ROPE)),
                        "(Got any rope?)" //HINT
                        
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(22, 0, 2),
                    new MerchantData(
                        "Japanese Merchant",
                        "jp-merchant.png",
                        Language.JAPANESE,
                        "家にもうちょっと光が欲しいなあ。",
                        "おお！これはいいね！",
                        "毎度ありがとうございます。",
                        "お前さあ…こんなゴミで商売してんの？",
                        "商売するつもりがないならさっさと消えろ！",
                        9f, 2f,
                        new Item[] { Item.ROPE, Item.LENS, Item.COMPASS },
                        new HashSet<>(Arrays.asList(Item.CANDLE)),
                        "(I want a bit more light)" //HINT
                    )
                ),
            };

        levelDatabase.put(level1.levelId, level1);

        // LEVEL 2

        Level level2 = new Level();
        level2.levelId = "level_02";
        level2.nextLevelId = "null"; // Ends the game for now
        level2.displayName = "The Grand Market";
        
        level2.skyboxPath = "Skyboxes/miramar";
        level2.bgmPath = "GameAudio.ogg";
        
        level2.valueGoal = 100; 
        level2.map = new Level2Map();
    
        level2.playerStart = new Vector3(14, 1, 2); 
        
        level2.startingItems = new Item[] {
                Item.ROPE,    
                Item.FISH,   
                Item.TRINKET  
            };

        level2.merchants = new MerchantConfig[] {
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(8, 0, 12), // Middle-Left Booth
                    new MerchantData(
                        "Vietnamese Merchant", "zh-merchant.png", Language.VIETNAMESE,
                        "Chào mừng! Bạn muốn mua gì?", "Thỏa thuận tuyệt vời!", "Cảm ơn bạn!", "Bạn đang đùa tôi à?", "Không mua thì đi đi!",
                        8f, 2f,
                        // Sells Rares, wants a specific Common.
                        new Item[] { Item.COMPASS, Item.SPICE, Item.CANDLE },
                        new HashSet<>(Arrays.asList(Item.ROPE)), "(I need some rope...)" 
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(20, 0, 6), // Top-Right Booth
                    new MerchantData(
                        "Japanese Merchant", "jp-merchant.png", Language.JAPANESE,
                        "いらっしゃいませ！", "素晴らしい取引だ！", "ありがとうございます。", "ふざけるな！", "冷やかしなら帰ってくれ！",
                        9f, 2f,
                        // Sells Epics, wants a specific Rare
                        new Item[] { Item.CHALICE, Item.GEMSTONE, Item.LENS },
                        new HashSet<>(Arrays.asList(Item.COMPASS)), "(I lost my way...)" 
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(20, 0, 18), // Bottom-Right Booth
                    new MerchantData(
                        "Chinese Merchant", "vi-merchant.png", Language.CHINESE,
                        "欢迎！来看看我的货物吧。", "太好了，成交！", "谢谢惠顾。", "你在开玩笑吗？", "没钱就别来烦我！",
                        10f, 2f,
                        // Sells a Legendary, wants a specific Epic
                        new Item[] { Item.DRAGON_SCALE, Item.IDOL, Item.CLOTH },
                        new HashSet<>(Arrays.asList(Item.CHALICE)), "(A golden cup would be nice...)" 
                    )
                )
            };
        
        levelDatabase.put(level2.levelId, level2);
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
