package com.simpulator.game.levels;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.entities.GatekeeperEntity;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.scenes.ExploreScene;
import com.simpulator.game.trading.Inventory;
import com.simpulator.game.trading.Item;
import java.util.ArrayList;

public class Level {

    public static class MerchantConfig {

        private final Class<? extends MerchantEntity> merchantClass;
        private final Vector3 position;
        private final MerchantData data;

        public MerchantConfig(
            Class<? extends MerchantEntity> merchantClass,
            Vector3 position,
            MerchantData data
        ) {
            this.merchantClass = merchantClass;
            this.position = position;
            this.data = data;
        }

        public Class<? extends MerchantEntity> getMerchantClass() {
            return merchantClass;
        }

        public Vector3 getPosition() {
            return position;
        }

        public MerchantData getData() {
            return data;
        }
    }

    private final LevelId levelId;
    private final LevelId nextLevelId;
    private final String displayName;

    private final String skyboxPath;
    private final String bgmPath;

    private final int valueGoal;
    private final Item[] startingItems;

    private final Vector3 playerStartPosition;
    private final Quaternion playerStartRotation;
    private final LevelMap map;
    private final MerchantConfig[] merchants;
    private final Vector3[] gatekeeperPositions;

    public Level(
        LevelId levelId,
        LevelId nextLevelId,
        String displayName,
        String skyboxPath,
        String bgmPath,
        int valueGoal,
        Item[] startingItems,
        Vector3 playerStartPosition,
        Quaternion playerStartRotation,
        LevelMap map,
        MerchantConfig[] merchants,
        Vector3[] gatekeeperPositions
    ) {
        this.levelId = levelId;
        this.nextLevelId = nextLevelId;
        this.displayName = displayName;
        this.skyboxPath = skyboxPath;
        this.bgmPath = bgmPath;
        this.valueGoal = valueGoal;
        this.startingItems = startingItems;
        this.playerStartPosition = playerStartPosition;
        this.playerStartRotation = playerStartRotation;
        this.map = map;
        this.merchants = merchants;
        this.gatekeeperPositions = gatekeeperPositions;
    }

    public LevelId getLevelId() {
        return levelId;
    }

    public LevelId getNextLevelId() {
        return nextLevelId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSkyboxPath() {
        return skyboxPath;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public int getValueGoal() {
        return valueGoal;
    }

    public Inventory createInventory() {
        Inventory inventory = new Inventory();
        for (Item item : startingItems) {
            inventory.add(item);
        }
        return inventory;
    }

    public Vector3 getPlayerStartPosition() {
        return playerStartPosition.cpy();
    }

    public Quaternion getPlayerStartRotation() {
        return playerStartRotation.cpy();
    }

    public void loadMap(EntityManager entityManager, TextureCache textures) {
        map.load(entityManager, textures);
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

    public ArrayList<GatekeeperEntity> createGatekeepers(
        TextureCache textures,
        Entity player
    ) {
        ArrayList<GatekeeperEntity> outEntities = new ArrayList<>();
        for (Vector3 pos : gatekeeperPositions) {
            try {
                GatekeeperEntity gatekeeper = new GatekeeperEntity(
                    pos,
                    textures,
                    player
                );
                outEntities.add(gatekeeper);
            } catch (Exception e) {
                throw new RuntimeException(
                    "Failed to spawn gatekeeper at position " + pos.toString(),
                    e
                );
            }
        }
        return outEntities;
    }

    public ExploreScene createScene(
        SceneManager sceneManager,
        GraphicsManager graphics,
        LevelManager levelManager,
        MusicManager musicManager
    ) {
        return new ExploreScene(
            sceneManager,
            graphics,
            levelManager,
            musicManager,
            this
        );
    }
}
