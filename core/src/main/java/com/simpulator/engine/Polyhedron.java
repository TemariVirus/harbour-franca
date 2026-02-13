package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/** Represents a convex polyhedron. */
public abstract class Polyhedron implements GJKTarget {

    /** Returns all unique vertices of the polyhedron. */
    public abstract Iterable<Vector3> getAllVertices();

    /** Returns a bounding box containing the polyhedron. */
    public abstract BoundingBox getBounds();

    @Override
    public Vector3 furthestPoint(Vector3 direction, boolean reverse) {
        float distance = reverse
            ? Float.POSITIVE_INFINITY
            : Float.NEGATIVE_INFINITY;
        Vector3 furthest = null;
        // The furthest point will be one of the vertices, so just check all of them
        for (Vector3 vert : getAllVertices()) {
            float dot = vert.dot(direction);
            if (dot < distance == reverse) {
                distance = dot;
                furthest = vert;
            }
        }
        return furthest.cpy();
    }

    /** Returns whether this polyhedron intersects with another polyhedron. */
    public boolean intersects(Polyhedron other) {
        if (!this.getBounds().intersects(other.getBounds())) {
            return false;
        }
        return CollisionManager.intersects(this, other);
    }

    /**
     * Returns whether this polyhedron intersects with another polyhedron.
     * If they intersect, the minimum translation vector (moving this polyhedron)
     * to separate them is stored in outMtv.
     */
    public boolean intersects(Polyhedron immovable, Vector3 outMtv) {
        if (!this.getBounds().intersects(immovable.getBounds())) {
            return false;
        }
        return CollisionManager.intersects(this, immovable, outMtv);
    }
}
