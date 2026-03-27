package com.simpulator.game.levels.maps;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.entities.CuboidEntity;
import com.simpulator.game.entities.TiledRenderer;
import com.simpulator.game.levels.LevelMap;

public class Level1Map implements LevelMap {

    // W = Outer Brick Wall, B = Inner Booth Wall, C = Counter, D = Door, M = Merchant
    public static final String[] LEVEL_1_LAYOUT = {
        "WWWWWWWWWWWWWWW",
        "W.BMB.BMB.BMB.W",
        "W.BCB.BCB.BCB.W",
        "W.............W",
        "W.............W",
        "W.............W",
        "WWWWWWWDWWWWWWW",
    };

    private static final float TILE_SIZE = 2.0f;

    @Override
    public void load(EntityManager entityManager, TextureCache textures) {
        TiledRenderer outerWallTex = new TiledRenderer(
            new TextureRegion(textures.get("brick.png")),
            new com.badlogic.gdx.math.Vector2(1, 2)
        );
        TiledRenderer boothWallTex = new TiledRenderer(
            new TextureRegion(textures.get("booth_wall.png")),
            new com.badlogic.gdx.math.Vector2(1, 2)
        );
        TiledRenderer counterTex = new TiledRenderer(
            new TextureRegion(textures.get("booth_counter.png")),
            new com.badlogic.gdx.math.Vector2(1, 1)
        );
        TiledRenderer floorTex = new TiledRenderer(
            new TextureRegion(textures.get("floor.png")),
            new com.badlogic.gdx.math.Vector2(1, 1)
        );

        for (int row = 0; row < LEVEL_1_LAYOUT.length; row++) {
            String line = LEVEL_1_LAYOUT[row];
            for (int col = 0; col < line.length(); col++) {
                char tile = line.charAt(col);

                float x = col * TILE_SIZE;
                float z = row * TILE_SIZE;

                // Always generate floor
                entityManager.add(
                    new CuboidEntity(
                        new Vector3(x, -1.0f, z),
                        new Vector3(TILE_SIZE, TILE_SIZE, 0.1f),
                        new Quaternion().setFromAxis(Vector3.X, -90),
                        floorTex,
                        false
                    )
                );

                if (tile == 'W') {
                    float rotation = 0f;
                    float zOffset = 0f;
                    float xOffset = 0f;

                    if (row == 0) {
                        rotation = 0f;
                        zOffset = 1.0f;
                    } else if (row == LEVEL_1_LAYOUT.length - 1) {
                        rotation = 180f;
                        zOffset = -1.0f;
                    } else if (col == 0) {
                        rotation = 90f;
                        xOffset = 1.0f;
                    } else if (col == line.length() - 1) {
                        rotation = -90f;
                        xOffset = -1.0f;
                    }

                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x + xOffset, 1.0f, z + zOffset),
                            new Vector3(TILE_SIZE, 4.0f, TILE_SIZE),
                            new Quaternion().setFromAxis(Vector3.Y, rotation),
                            outerWallTex,
                            false
                        )
                    );
                } else if (tile == 'B') {
                    // Inner Wooden Booth Wall (Faces Right and Left so it's solid)
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, 1.0f, z),
                            new Vector3(TILE_SIZE, 4.0f, TILE_SIZE),
                            new Quaternion().setFromAxis(Vector3.Y, 90f),
                            boothWallTex,
                            false
                        )
                    );
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, 1.0f, z),
                            new Vector3(TILE_SIZE, 4.0f, TILE_SIZE),
                            new Quaternion().setFromAxis(Vector3.Y, -90f),
                            boothWallTex,
                            false
                        )
                    );
                } else if (tile == 'C') {
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, 0.0f, z),
                            new Vector3(TILE_SIZE * 2.0f, 1.0f, TILE_SIZE),
                            new Quaternion().setFromAxis(Vector3.Y, 0f),
                            counterTex,
                            false
                        )
                    );
                }
            }
        }
    }
}
