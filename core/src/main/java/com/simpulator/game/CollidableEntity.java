package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.ColliderMesh;
import com.simpulator.engine.Cuboid;
import com.simpulator.engine.Entity;
import com.simpulator.engine.GJKShape;
import java.util.Arrays;

public class CollidableEntity extends Entity implements ColliderMesh {

    private float thickness;

    public CollidableEntity(
        Vector3 position,
        Vector2 size,
        float thickness,
        Quaternion rotation,
        Texture texture
    ) {
        super(position, size, rotation, new TextureRegion(texture));
        this.thickness = Math.abs(thickness);
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = Math.abs(thickness);
    }

    public OrientedBoundingBox getOrientedBoundingBox() {
        Vector3 minCorner = getLocalVertex(0);
        minCorner.z = -thickness;
        Vector3 maxCorner = getLocalVertex(2);
        maxCorner.z = thickness;

        BoundingBox box = new BoundingBox(minCorner, maxCorner);
        return new OrientedBoundingBox(box, transform);
    }

    @Override
    public Iterable<GJKShape> getShapes() {
        Cuboid cuboid = new Cuboid(getOrientedBoundingBox());
        return Arrays.asList(new GJKShape[] { cuboid });
    }

    @Override
    public BoundingBox getBounds() {
        return getOrientedBoundingBox().getBounds();
    }
}
