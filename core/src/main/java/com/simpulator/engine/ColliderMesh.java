package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/** A mesh used for detecting collisions, made out of GJKShapes. */
public interface ColliderMesh {
    /** Returns all convex shapes that make up the mesh. */
    public Iterable<GJKShape> getShapes();

    /** Returns the bounding box of the entire mesh. */
    public BoundingBox getBounds();

    /** Returns whether this mesh intersects with another mesh. */
    public default boolean intersects(ColliderMesh other) {
        if (!this.getBounds().intersects(other.getBounds())) {
            return false;
        }

        for (GJKShape shape1 : this.getShapes()) {
            for (GJKShape shape2 : other.getShapes()) {
                if (CollisionManager.intersects(shape1, shape2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether this mesh intersects with another mesh.
     * If they intersect, a best-effort translation vector (moving this ColliderMesh)
     * to separate them is stored in outPush.
     */
    public default boolean intersects(ColliderMesh immovable, Vector3 outPush) {
        if (!this.getBounds().intersects(immovable.getBounds())) {
            return false;
        }

        float intersectCount = 0;
        outPush.setZero();
        Vector3 tempMtv = new Vector3();
        for (GJKShape movableShape : this.getShapes()) {
            for (GJKShape immovableShape : immovable.getShapes()) {
                if (
                    CollisionManager.intersects(
                        movableShape,
                        immovableShape,
                        tempMtv
                    )
                ) {
                    intersectCount++;
                    outPush.add(tempMtv);
                }
            }
        }
        if (intersectCount > 0) {
            outPush.scl(1f / intersectCount);
        }
        return intersectCount > 0;
    }
}
