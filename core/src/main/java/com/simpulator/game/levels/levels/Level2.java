package com.simpulator.game.levels.levels;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.Level;
import com.simpulator.game.levels.LevelId;
import com.simpulator.game.levels.MerchantData;
import com.simpulator.game.levels.maps.Level2Map;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.HashSet;

public class Level2 extends Level {

    public Level2() {
        super(
            LevelId.LEVEL_2,
            null,
            "The Grand Market",
            "Skyboxes/miramar",
            "GameAudio.ogg",
            100,
            new Item[] { Item.ROPE, Item.FISH, Item.CLOTH },
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
                        "哦！太好了！谢谢你！这个能多喝六月！",
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
    }
}
