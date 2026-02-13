package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;

/** Enables collision detection for a convex shape via the GJK algorithm. */
public interface GJKTarget {
    /**
     * Support function for the GJK algorithm.
     * Returns the point on this convex shape that is furthest from the origin
     * in the given direction, or in the opposite direction if reverse is true.
     *
     * @see CollisionManager#intersects(GJKTarget, GJKTarget, Vector3)
     */
    Vector3 furthestPoint(Vector3 direction, boolean reverse);
}
