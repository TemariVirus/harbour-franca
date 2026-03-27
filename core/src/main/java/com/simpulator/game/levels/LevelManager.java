package com.simpulator.game.levels;

import com.simpulator.game.levels.levels.Level1;
import com.simpulator.game.levels.levels.Level2;
import com.simpulator.game.levels.levels.TutorialLevel;
import java.util.HashMap;
import java.util.Map;

public class LevelManager {

    private Map<LevelId, Level> levelDatabase = new HashMap<>();
    private LevelId currentLevelId;

    public LevelManager() {
        // Hardcoded Level
        initLevels();
    }

    private void initLevels() {
        Level tutorialLevel = new TutorialLevel();
        levelDatabase.put(tutorialLevel.getLevelId(), tutorialLevel);

        Level level1 = new Level1();
        levelDatabase.put(level1.getLevelId(), level1);

        Level level2 = new Level2();
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
