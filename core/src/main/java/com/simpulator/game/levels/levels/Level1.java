package com.simpulator.game.levels.levels;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.Level;
import com.simpulator.game.levels.LevelId;
import com.simpulator.game.levels.MerchantData;
import com.simpulator.game.levels.maps.Level1Map;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.HashSet;

public class Level1 extends Level {

    public Level1() {
        super(
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
                        "Có dây thừng không? Có người thích kiểu chơi *đó* đấy.",
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
    }
}
