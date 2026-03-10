package com.simpulator.engine.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.Entity;

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

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
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
    public void render(TextureBatch batch, Camera camera, Entity entity) {
        batch.draw3D(textureRegion, entity.getTransform());
    }
}
