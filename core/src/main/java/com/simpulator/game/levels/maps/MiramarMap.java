package com.simpulator.game.levels.maps;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.CuboidEntity;
import com.simpulator.game.TiledRenderer;
import com.simpulator.game.levels.LevelMap;

public class MiramarMap implements LevelMap {

    private static final String BRICK_IMG = "Oran.jpeg";
    private static final float WIDTH = 5;
    private static final float HEIGHT = 3;
    private static final float WALL_THICKNESS = 0.2f;

    @Override
    public void load(EntityManager entityManager, TextureCache textures) {
        TiledRenderer renderer = new TiledRenderer(
            new TextureRegion(textures.get(BRICK_IMG)),
            new Vector2(1, 1)
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(0, 0, WIDTH),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 0),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(0, 0, -WIDTH),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 180),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(WIDTH, 0, 0),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 90),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(-WIDTH, 0, 0),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, -90),
                renderer,
                false
            )
        );
    }
}
