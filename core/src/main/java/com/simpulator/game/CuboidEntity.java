package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.CollidableEntity;
import com.simpulator.engine.Cuboid;
import com.simpulator.engine.GJKShape;
import java.util.Arrays;

public class CuboidEntity extends CollidableEntity {

    private float thickness;
    private boolean isMovable;

    public CuboidEntity(
        Vector3 position,
        Vector2 size,
        float thickness,
        Quaternion rotation,
        Texture texture,
        boolean isMovable
    ) {
        super(position, size, rotation, new TextureRegion(texture));
        this.thickness = Math.abs(thickness);
        this.isMovable = isMovable;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = Math.abs(thickness);
    }

    public boolean getMovable() {
        return isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
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

    @Override
    public void onCollision(CollidableEntity other) {
        if (!isMovable) {
            return;
        }

        Vector3 mtv = new Vector3();
        if (intersects(other, mtv)) {
            // Move ourselves outside of the colliding mesh
            translate(mtv);
        }
    }
}
