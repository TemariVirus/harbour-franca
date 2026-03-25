package com.simpulator.game.levels;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.language.Language;
import com.simpulator.game.levels.maps.MiramarMap;
import com.simpulator.game.scenes.ExploreScene;
import com.simpulator.game.scenes.TutorialScene;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.HashSet;

public class TutorialLevel extends Level {

    public TutorialLevel() {
        this.levelId = "level_00";
        this.nextLevelId = "level_01";
        this.displayName = "Tutorial";

        this.skyboxPath = "Skyboxes/miramar";
        this.bgmPath = "GameAudio.mp3";
        this.valueGoal = 13;
        this.startingItems = new Item[] { Item.TRINKET, Item.FISH };

        this.playerStart = new Vector3(0, 1, 0);

        this.map = new MiramarMap();

        this.merchants = new MerchantConfig[] {
            new MerchantConfig(
                MerchantEntity.class,
                new Vector3(0, 0, 2),
                new MerchantData(
                    "Friendly Guide",
                    "guide.png",
                    Language.ENGLISH,
                    "Welcome to Harbour Franca! Let's make a simple trade. I would pay\nanything for a trinket.",
                    "Thank you!",
                    "I'll only pay slightly more for that.",
                    "No, the value difference is too great! Our deal is off.",
                    "Too slow! It seems you do not have that iron in you!",
                    1000f,
                    1.5f,
                    new Item[] { Item.FISH, Item.GEMSTONE, Item.CANDLE },
                    new HashSet<>(Arrays.asList(Item.TRINKET))
                )
            ),
        };
    }

    @Override
    public ExploreScene createScene(
        SceneManager sceneManager,
        LevelManager levelManager,
        MusicManager musicManager
    ) {
        return new TutorialScene(
            sceneManager,
            levelManager,
            musicManager,
            this
        );
    }
}
