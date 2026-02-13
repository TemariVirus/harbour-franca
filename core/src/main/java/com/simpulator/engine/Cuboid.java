package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

/** Converts an oriented bounding box to the Polyhedron class. */
public class Cuboid extends Polyhedron {

    private OrientedBoundingBox obb;

    public Cuboid(OrientedBoundingBox obb) {
        this.obb = obb;
    }

    @Override
    public Iterable<Vector3> getAllVertices() {
        return java.util.Arrays.asList(obb.getVertices());
    }

    @Override
    public BoundingBox getBounds() {
        return obb.getBounds();
    }
}
