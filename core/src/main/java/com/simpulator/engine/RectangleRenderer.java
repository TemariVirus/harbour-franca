package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

/** A renderer for drawing flat rectangular entities. */
public class RectangleRenderer implements EntityRenderer {

    /** The texture region used to render the entity. */
    private TextureRegion textureRegion;
    /** The tint color applied when rendering the entity. */
    private Color tint = Color.WHITE.cpy();

    public RectangleRenderer(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public RectangleRenderer(TextureRegion textureRegion, Color tint) {
        this.textureRegion = textureRegion;
        this.tint.set(tint);
    }

    public Color getTint() {
        return tint.cpy();
    }

    public void setTint(Color tint) {
        this.tint = tint.cpy();
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    /** Returns the vertex in local space, indexed in clockwise order. */
    private Vector3 getVertex(int index) {
        switch (index) {
            case 0:
                return new Vector3(-0.5f, -0.5f, 0); // Bottom left
            case 1:
                return new Vector3(-0.5f, 0.5f, 0); // Top left
            case 2:
                return new Vector3(0.5f, 0.5f, 0); // Top right
            case 3:
                return new Vector3(0.5f, -0.5f, 0); // Bottom right
            default:
                throw new IllegalArgumentException(
                    "Index must be in range [0, 3]"
                );
        }
    }

    @Override
    public boolean isVisible(Camera camera, Entity entity) {
        OrientedBoundingBox obb = new OrientedBoundingBox(
            new BoundingBox(getVertex(0), getVertex(2)),
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
    public void render(SpriteBatch batch, Camera camera, Entity entity) {
        float[] vertexData = new float[20];
        for (int i = 0; i < 4; i++) {
            boolean isLeft = i < 2;
            boolean isBottom = (i == 0) || (i == 3);

            float u = isLeft ? textureRegion.getU() : textureRegion.getU2();
            float v = isBottom ? textureRegion.getV2() : textureRegion.getV();
            Vector3 vertPos = getVertex(i).mul(entity.getTransform()); // World space
            camera.project(vertPos); // Screen space

            vertexData[i * 5 + 0] = vertPos.x;
            vertexData[i * 5 + 1] = vertPos.y;
            vertexData[i * 5 + 2] = tint.toFloatBits();
            vertexData[i * 5 + 3] = u;
            vertexData[i * 5 + 4] = v;
        }
        batch.draw(textureRegion.getTexture(), vertexData, 0, 20);
    }
}
