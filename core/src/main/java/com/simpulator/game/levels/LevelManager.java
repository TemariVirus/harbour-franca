package com.simpulator.game.levels;

import com.badlogic.gdx.math.Quaternion;
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

    private Map<LevelId, Level> levelDatabase = new HashMap<>();
    private LevelId currentLevelId;

    public LevelManager() {
        // Hardcoded Level
        initLevels();
    }

    private void initLevels() {
        // Level 0 - Tutorial
        Level tutorialLevel = new TutorialLevel();
        levelDatabase.put(tutorialLevel.getLevelId(), tutorialLevel);

        // Level 1
        Level level1 = new Level(
            LevelId.LEVEL_1,
            LevelId.LEVEL_2,
            "The Beginning",
            "Skyboxes/miramar",
            "GameAudio.ogg",
            50,
            new Item[] { Item.FISH, Item.CLOTH, Item.CANDLE },
            new Vector3(14, 1, 8),
            new Quaternion().idt(),
            new Level1Map(),
            new MerchantConfig[] {
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
                        10f,
                        2f,
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
                        8f,
                        2f,
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
                        9f,
                        2f,
                        new Item[] { Item.ROPE, Item.LENS, Item.COMPASS },
                        new HashSet<>(Arrays.asList(Item.CANDLE)),
                        "(I want a bit more light)" //HINT
                    )
                ),
            },
            new Vector3[] { new Vector3(14, 1, 11) }
        );
        levelDatabase.put(level1.getLevelId(), level1);

        // LEVEL 2
        Level level2 = new Level(
            LevelId.LEVEL_2,
            null,
            "The Grand Market",
            "Skyboxes/miramar",
            "GameAudio.ogg",
            100,
            new Item[] { Item.ROPE, Item.FISH, Item.TRINKET },
            new Vector3(14, 1, 2),
            new Quaternion().idt().setFromAxis(Vector3.Y, 180f),
            new Level2Map(),
            new MerchantConfig[] {
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(8, 0, 12), // Middle-Left Booth
                    new MerchantData(
                        "Vietnamese Merchant",
                        "zh-merchant.png",
                        Language.VIETNAMESE,
                        "Nó mềm mại. Bạn dùng nó để may áo quần.",
                        "Ồ, tuyệt quá! Cảm ơn nhé.",
                        "Cảm ơn bạn đã mua hàng!",
                        "Hơi bị hớ đấy nhỉ? Đồ của tôi đáng giá hơn thế.",
                        "Nếu bạn không định mua thì đừng có lãng phí thời gian!",
                        8f,
                        2f,
                        // Sells Rares, wants a specific Common.
                        new Item[] { Item.COMPASS, Item.SPICE, Item.CANDLE },
                        new HashSet<>(Arrays.asList(Item.CLOTH)),
                        "(It is soft. You use it to make clothes.)"
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(20, 0, 6), // Top-Right Booth
                    new MerchantData(
                        "Japanese Merchant",
                        "jp-merchant.png",
                        Language.JAPANESE,
                        "道に迷いました。",
                        "おお！これはいいね！",
                        "毎度ありがとうございます。",
                        "お前さあ…こんなゴミで商売してんの？",
                        "商売するつもりがないならさっさと消えろ！",
                        9f,
                        2f,
                        // Sells Epics, wants a specific Rare
                        new Item[] { Item.CHALICE, Item.GEMSTONE, Item.LENS },
                        new HashSet<>(Arrays.asList(Item.COMPASS)),
                        "(I lost my way...)"
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(20, 0, 18), // Bottom-Right Booth
                    new MerchantData(
                        "Chinese Merchant",
                        "vi-merchant.png",
                        Language.CHINESE,
                        "它是由金子做的。你可以用它来喝水！",
                        "哦！太好了！谢谢你！这个能多吃六月！",
                        "谢谢你的购买！",
                        "我才没有那么笨！给我滚！",
                        "如果你不打算买的话就别浪费我的时间了！",
                        10f,
                        2f,
                        // Sells a Legendary, wants a specific Epic
                        new Item[] { Item.DRAGON_SCALE, Item.IDOL, Item.CLOTH },
                        new HashSet<>(Arrays.asList(Item.CHALICE)),
                        "(It is made of gold. You can use it to drink water.)"
                    )
                ),
            },
            new Vector3[] { new Vector3(14, 1, 21) }
        );
        levelDatabase.put(level2.getLevelId(), level2);
    }

    public Level getLevel(LevelId levelId) {
        return levelDatabase.get(levelId);
    }

    public void setCurrentLevelId(LevelId levelId) {
        this.currentLevelId = levelId;
    }

    public Level getCurrentLevel() {
        return levelDatabase.get(currentLevelId);
    }
}
