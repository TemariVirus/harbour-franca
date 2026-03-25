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
        this.valueGoal = 7;
        this.startingItems = new Item[] { Item.TRINKET };

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
                    "Welcome to Harbour Franca! Let's make a simple trade.",
                    1.5f,
                    1.0f,
                    new Item[] { Item.FISH, Item.CLOTH, Item.CANDLE },
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
