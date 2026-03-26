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
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import com.simpulator.game.trading.ItemRarity;

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
        level2.nextLevelId = "level_03"; // Ends the game for now
        level2.displayName = "The Grand Market";
        
        level2.skyboxPath = "Skyboxes/miramar";
        level2.bgmPath = "GameAudio.ogg";
        
        level2.valueGoal = 100; 
        level2.map = new Level2Map();
        // Turn around player
        level2.playerStartYaw = 180f;
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
                        "Vietnamese Merchant", 
                        "zh-merchant.png", 
                        Language.VIETNAMESE,
                        "Nó mềm mại. Bạn dùng nó để may áo quần.", //TODO add fnt
                        "Ồ, tuyệt quá! Cảm ơn nhé.",
                        "Cảm ơn bạn đã mua hàng!",
                        "Hơi bị hớ đấy nhỉ? Đồ của tôi đáng giá hơn thế.",
                        "Nếu bạn không định mua thì đừng có lãng phí thời gian!",
                        8f, 2f,
                        // Sells Rares, wants a specific Common.
                        new Item[] { Item.COMPASS, Item.SPICE, Item.CANDLE },
                        new HashSet<>(Arrays.asList(Item.CLOTH)), "(It is soft. You use it to make clothes.)" 
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(20, 0, 6), // Top-Right Booth
                    new MerchantData(
                        "Japanese Merchant", 
                        "jp-merchant.png", 
                        Language.JAPANESE,
                        "道に迷いました。", //TODO add fnt
                        "おお！これはいいね！",
                        "毎度ありがとうございます。",
                        "お前さあ…こんなゴミで商売してんの？",
                        "商売するつもりがないならさっさと消えろ！",
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
                        "Chinese Merchant",
                        "vi-merchant.png",
                        Language.CHINESE,
                        "它是由金子做的。你可以用它来喝水！", //TODO add fnt
                        "哦！太好了！谢谢你！这个能多吃六月！",
                        "谢谢你的购买！",
                        "我才没有那么笨！给我滚！",
                        "如果你不打算买的话就别浪费我的时间了！",
                        10f, 2f,
                        // Sells a Legendary, wants a specific Epic
                        new Item[] { Item.DRAGON_SCALE, Item.IDOL, Item.CLOTH },
                        new HashSet<>(Arrays.asList(Item.CHALICE)), "(It is made of gold. You can use it to drink water.)" 
                    )
                )
            };
        
        levelDatabase.put(level2.levelId, level2);
        // Level 3 Will use the level 1 mao
        Level level3 = new Level();
        level3.levelId = "level_03";
        level3.nextLevelId = null; // This is the final level
        level3.displayName = "The Endless Bazaar";
        
        level3.skyboxPath = "Skyboxes/miramar";
        level3.bgmPath = "GameAudio.ogg";
        
        level3.valueGoal = 200; 
        
        // using level 1 map
        level3.map = new Level1Map();
        
        level3.playerStart = new Vector3(14, 1, 8);

        level3.startingItems = generateRandomInventory(
            new ItemRarity[] { ItemRarity.COMMON, ItemRarity.COMMON, ItemRarity.COMMON }, 
            null
        );

        Item vietWants = getRandomItem(ItemRarity.COMMON, null);
        Item japnWants = getRandomItem(ItemRarity.RARE, null);
        Item chinWants = getRandomItem(ItemRarity.EPIC, null);

        level3.merchants = new MerchantConfig[] {
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(6, 0, 2), 
                    new MerchantData(
                        "Vietnamese Merchant", "zh-merchant.png", Language.VIETNAMESE,
                        "Có dây thừng không? Có người thích kiểu\nchơi *đó* đấy.",
                        "Ồ, tuyệt quá! Cảm ơn nhé.",
                        "Cảm ơn bạn đã mua hàng!",
                        "Hơi bị hớ đấy nhỉ? Đồ của tôi đáng giá hơn thế.",
                        "Nếu bạn không định mua thì đừng có lãng phí thời gian!",
                        8f, 2f,
                        generateRandomInventory(new ItemRarity[] { ItemRarity.RARE, ItemRarity.COMMON, ItemRarity.COMMON }, vietWants),
                        new HashSet<>(Arrays.asList(vietWants)), 
                        "(I'm looking for a " + vietWants.toString() + ")" 
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(14, 0, 2), 
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
                        generateRandomInventory(new ItemRarity[] { ItemRarity.EPIC, ItemRarity.RARE, ItemRarity.RARE }, japnWants),
                        new HashSet<>(Arrays.asList(japnWants)), 
                        "(I need a " + japnWants.toString() + ")" 
                    )
                ),
                new MerchantConfig(
                    MerchantEntity.class,
                    new Vector3(22, 0, 2), 
                    new MerchantData(
                        "Chinese Merchant",
                        "vi-merchant.png",
                        Language.CHINESE,
                        "我很饿。有食物的话我什么都愿意给！",
                        "哦！太好了！谢谢你！这个能多吃六月！",
                        "谢谢你的购买！",
                        "我才没有那么笨！给我滚！",
                        "如果你不打算买的话就别浪费我的时间了！",
                        10f, 2f,
                        generateRandomInventory(new ItemRarity[] { ItemRarity.LEGENDARY, ItemRarity.EPIC, ItemRarity.EPIC }, chinWants),
                        new HashSet<>(Arrays.asList(chinWants)), 
                        "(Bring me a " + chinWants.toString() + "!)" 
                    )
                )
            };
        
        levelDatabase.put(level3.levelId, level3);
        
    }
    
    // For Level 3 Randomiser
    private final Random random = new Random();

    // Pulls a random item of a specific rarity, ignoring any items in the exclude list
    private Item getRandomItem(ItemRarity targetRarity, List<Item> excludeList) {
        List<Item> possibleItems = new ArrayList<>();
        for (Item item : Item.values()) {
            if (item.rarity() == targetRarity && (excludeList == null || !excludeList.contains(item))) {
                possibleItems.add(item);
            }
        }
        return possibleItems.get(random.nextInt(possibleItems.size()));
    }

    // Generates a full 3-item inventory based on a requested spread of rarities
    private Item[] generateRandomInventory(ItemRarity[] rarities, Item wantedItem) {
        List<Item> excludes = new ArrayList<>();
        if (wantedItem != null) {
            excludes.add(wantedItem); // Prevent the merchant from selling the item they want!
        }

        Item[] inventory = new Item[rarities.length];
        for (int i = 0; i < rarities.length; i++) {
            Item randomItem = getRandomItem(rarities[i], excludes);
            inventory[i] = randomItem;
            excludes.add(randomItem); // Prevent duplicate items in the same inventory
        }
        return inventory;
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
