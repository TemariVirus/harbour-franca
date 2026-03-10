package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityRenderer;
import com.simpulator.engine.TextureBatch;

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

    @Override
    public boolean isVisible(Camera camera, Entity entity) {
        OrientedBoundingBox obb = new OrientedBoundingBox(
            new BoundingBox(
                new Vector3(-0.5f, -0.5f, 0),
                new Vector3(0.5f, 0.5f, 0)
            ),
            entity.getTransform()
        );
        return camera.frustum.boundsInFrustum(obb);
    }

    @Override
    public float getZOrder(Camera camera, Entity entity) {
        Vector3 position = entity.getPosition();
        camera.project(position);
        return position.z;
    }

    @Override
    public void render(TextureBatch batch, Camera camera, Entity entity) {
        Vector3 size = entity.getSize();
        float xStep = tileSize.x / size.x;
        float yStep = tileSize.y / size.y;

        float[] vertexData = new float[20];
        for (int i = 0; i < 4; i++) {
            boolean isLeft = i < 2;
            boolean isBottom = (i == 0) || (i == 3);
            float u = isLeft ? textureRegion.getU() : textureRegion.getU2();
            float v = isBottom ? textureRegion.getV2() : textureRegion.getV();

            vertexData[i * 5 + 2] = tint.toFloatBits();
            vertexData[i * 5 + 3] = u;
            vertexData[i * 5 + 4] = v;
        }
        Vector3[] verts = new Vector3[4];
        for (int i = 0; i < 4; i++) {
            verts[i] = new Vector3();
        }

        // TODO: tile it again
        batch.draw(textureRegion, entity.getTransform());
        for (float x = -0.5f; x + xStep <= 0.5f; x += xStep) {
            for (float y = -0.5f; y + yStep <= 0.5f; y += yStep) {
                // batch.draw(
                //     textureRegion,
                //     entity.getTransform(),
                //     x,
                //     y,
                //     x + xStep,
                //     y + yStep
                // );
            }
        }
    }
}
