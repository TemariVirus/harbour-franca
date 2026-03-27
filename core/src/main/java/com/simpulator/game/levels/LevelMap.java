package com.simpulator.game.levels;

import com.simpulator.engine.EntityManager;
import com.simpulator.engine.scene.TextureCache;

public interface LevelMap {
    /** Creates the entities in this map and adds them to entityManager. */
    public void load(EntityManager entityManager, TextureCache textures);
}
