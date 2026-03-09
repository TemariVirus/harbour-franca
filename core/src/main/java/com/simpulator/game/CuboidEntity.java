package com.simpulator.game;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.simpulator.engine.CollidableEntity;
import com.simpulator.engine.Cuboid;
import com.simpulator.engine.EntityRenderer;
import com.simpulator.engine.GJKShape;
import java.util.Arrays;

public class CuboidEntity extends CollidableEntity {

    private boolean isMovable;

    public CuboidEntity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        EntityRenderer renderer,
        boolean isMovable
    ) {
        super(position, size, rotation, renderer);
        this.isMovable = isMovable;
    }

    public boolean getMovable() {
        return isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public OrientedBoundingBox getOrientedBoundingBox() {
        Vector3 minCorner = new Vector3(-0.5f, -0.5f, -0.5f);
        Vector3 maxCorner = new Vector3(0.5f, 0.5f, 0.5f);

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
