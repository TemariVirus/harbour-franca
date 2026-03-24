package com.simpulator.game.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.EntityRenderer;
import com.simpulator.engine.graphics.TextureBatch;

public class TiledRenderer implements EntityRenderer {

    private TextureRegion textureRegion;
    private Vector2 tileSize;
    private Color tint = Color.WHITE.cpy();

    public TiledRenderer(TextureRegion textureRegion, Vector2 tileSize) {
        this.textureRegion = textureRegion;
        this.tileSize = tileSize;
    }

    public TiledRenderer(
        TextureRegion textureRegion,
        Vector2 tileSize,
        Color tint
    ) {
        this.textureRegion = textureRegion;
        this.tileSize = tileSize;
        this.tint.set(tint);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public Vector2 getTileSize() {
        return tileSize.cpy();
    }

    public void setTileSize(Vector2 tileSize) {
        this.tileSize.set(tileSize);
    }

    public Color getTint() {
        return tint.cpy();
    }

    public void setTint(Color tint) {
        this.tint.set(tint);
    }

    protected boolean isPortionVisible(
        Camera camera,
        Matrix4 modelTransform,
        float x,
        float y,
        float width,
        float height
    ) {
        OrientedBoundingBox obb = new OrientedBoundingBox(
            new BoundingBox(
                new Vector3(x, y, 0),
                new Vector3(x + width, y + height, 0)
            ),
            modelTransform
        );
        return camera.frustum.boundsInFrustum(obb);
    }

    @Override
    public boolean isVisible(Camera camera, Entity entity) {
        return isPortionVisible(
            camera,
            entity.getTransform(),
            -0.5f,
            -0.5f,
            1,
            1
        );
    }

    @Override
    public void render(TextureBatch batch, Camera camera, Entity entity) {
        Vector3 size = entity.getSize();
        float xStep = tileSize.x / size.x;
        float yStep = tileSize.y / size.y;

        for (float x = -0.5f; x + xStep <= 0.5f; x += xStep) {
            for (float y = -0.5f; y + yStep <= 0.5f; y += yStep) {
                if (
                    !isPortionVisible(
                        camera,
                        entity.getTransform(),
                        x,
                        y,
                        xStep,
                        yStep
                    )
                ) {
                    continue;
                }
                batch.draw3D(
                    textureRegion,
                    entity.getTransform(),
                    x,
                    y,
                    x + xStep,
                    y + yStep
                );
            }
        }
    }
}
