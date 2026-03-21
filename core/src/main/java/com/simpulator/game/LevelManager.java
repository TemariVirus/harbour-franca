package com.simpulator.game;

import java.util.HashMap;
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

        level1.skyboxTexturePrefix = "Skybox/miramar";

        level1.backgroundMusic = "GameAudio.mp3";
        level1.playerStartX = 0f;
        level1.playerStartY = 1f;
        level1.playerStartZ = 0f;

        level1.entitiesToSpawn.add(new Level.EntityConfig("Tree", 5f, 0f, 5f));

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