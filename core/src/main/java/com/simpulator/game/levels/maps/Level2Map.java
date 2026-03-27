package com.simpulator.game.levels.maps;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.entities.CuboidEntity;
import com.simpulator.game.entities.TiledRenderer;
import com.simpulator.game.levels.LevelMap;

public class Level2Map implements LevelMap {

    private static final String[] LEVEL_2_LAYOUT = {
        // W = Outer Brick Wall, B = Inner Booth Wall, C = Counter, D = Door, M = Merchant P = Pavement
        "WWWWWWWWWWWWWWW", // 0
        "W......P......W", // 1
        "W......P.BBB..W", // 2 (Top-Right Booth)
        "W......P.CMB..W", // 3
        "W......P.BBB..W", // 4
        "W..BBB.P......W", // 5 (Middle-Left Booth)
        "W..BMC.P......W", // 6
        "W..BBB.P......W", // 7
        "W......P.BBB..W", // 8 (Bottom-Right Booth)
        "W......P.CMB..W", // 9
        "W......P.BBB..W", // 10
        "WWWWWWWDWWWWWWW", // 11 (Gatekeeper)
    };

    private static final float TILE_SIZE = 2.0f;

    private boolean isBooth(String[] layout, int r, int c) {
        if (
            r < 0 || r >= layout.length || c < 0 || c >= layout[0].length()
        ) return false;
        char ch = layout[r].charAt(c);
        return ch == 'B' || ch == 'M' || ch == 'C';
    }

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
        TiledRenderer pavementTex = new TiledRenderer(
            new TextureRegion(textures.get("pavement.png")),
            new com.badlogic.gdx.math.Vector2(1, 1)
        );

        for (int row = 0; row < LEVEL_2_LAYOUT.length; row++) {
            String line = LEVEL_2_LAYOUT[row];
            for (int col = 0; col < line.length(); col++) {
                char tile = line.charAt(col);

                float x = col * TILE_SIZE;
                float z = row * TILE_SIZE;

                if (tile == 'P') {
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, -1.0f, z),
                            new Vector3(TILE_SIZE, TILE_SIZE, 0.1f),
                            new Quaternion().setFromAxis(Vector3.X, -90),
                            pavementTex,
                            false
                        )
                    );
                } else if (
                    tile == '.' || tile == 'C' || tile == 'M' || tile == 'B'
                ) {
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, -1.0f, z),
                            new Vector3(TILE_SIZE, TILE_SIZE, 0.1f),
                            new Quaternion().setFromAxis(Vector3.X, -90),
                            floorTex,
                            false
                        )
                    );
                }

                if (tile == 'W') {
                    float rotation = 0f,
                        zOffset = 0f,
                        xOffset = 0f;
                    if (row == 0) {
                        rotation = 0f;
                        zOffset = 1.0f;
                    } else if (row == LEVEL_2_LAYOUT.length - 1) {
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
                            new Vector3(TILE_SIZE, 4.0f, 0.1f),
                            new Quaternion().setFromAxis(Vector3.Y, rotation),
                            outerWallTex,
                            false
                        )
                    );
                }
                // Edge Snapping
                else if (tile == 'B') {
                    boolean n = isBooth(LEVEL_2_LAYOUT, row - 1, col);
                    boolean s = isBooth(LEVEL_2_LAYOUT, row + 1, col);
                    boolean w = isBooth(LEVEL_2_LAYOUT, row, col - 1);
                    boolean e = isBooth(LEVEL_2_LAYOUT, row, col + 1);

                    // Offsets the plane to sit flush on the boundary lines of the grid cell
                    float offset = TILE_SIZE / 2.0f;

                    if (!n) {
                        // North Wall
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x, 1.0f, z - offset),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 180f),
                                boothWallTex,
                                false
                            )
                        ); // Outward Face
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x, 1.0f, z - offset),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 0f),
                                boothWallTex,
                                false
                            )
                        ); // Inward Face
                    }
                    if (!s) {
                        // South Wall
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x, 1.0f, z + offset),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 0f),
                                boothWallTex,
                                false
                            )
                        );
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x, 1.0f, z + offset),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 180f),
                                boothWallTex,
                                false
                            )
                        );
                    }
                    if (!w) {
                        // West Wall
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x - offset, 1.0f, z),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, -90f),
                                boothWallTex,
                                false
                            )
                        );
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x - offset, 1.0f, z),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 90f),
                                boothWallTex,
                                false
                            )
                        );
                    }
                    if (!e) {
                        // East Wall
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x + offset, 1.0f, z),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, 90f),
                                boothWallTex,
                                false
                            )
                        );
                        entityManager.add(
                            new CuboidEntity(
                                new Vector3(x + offset, 1.0f, z),
                                new Vector3(TILE_SIZE, 4.0f, 0.1f),
                                new Quaternion().setFromAxis(Vector3.Y, -90f),
                                boothWallTex,
                                false
                            )
                        );
                    }
                } else if (tile == 'C') {
                    float counterRot = (col < 7) ? 90f : -90f;
                    entityManager.add(
                        new CuboidEntity(
                            new Vector3(x, 0.0f, z),
                            new Vector3(TILE_SIZE, 1.0f, 0.1f),
                            new Quaternion().setFromAxis(Vector3.Y, counterRot),
                            counterTex,
                            false
                        )
                    );
                }
            }
        }
    }
}
