package com.simpulator.engine;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

/**
 * A mesh used for detecting collisions, made out of a mix of polyhedra and oriented bounding boxes.
 * Detection collision is quicker with oriented bounding boxes.
 */
public interface ColliderMesh extends Moveable {
    /** Returns all polyhedra in the mesh. */
    public Polyhedron[] getPolyhedra();
    /** Returns all oriented bounding boxes in the mesh. */
    public OrientedBoundingBox[] getBoundingBoxes();

    /** Returns the bounding box of the entire mesh. */
    public default BoundingBox getBounds() {
        BoundingBox bounds = new BoundingBox();
        for (Polyhedron polyhedron : getPolyhedra()) {
            bounds.ext(polyhedron.getBounds());
        }
        for (OrientedBoundingBox obb : getBoundingBoxes()) {
            bounds.ext(obb.getBounds());
        }
        return bounds;
    }

    /** Returns whether this mesh intersects with another mesh. */
    public default boolean intersects(ColliderMesh other) {
        if (!this.getBounds().intersects(other.getBounds())) {
            return false;
        }

        for (Polyhedron polyhedron1 : this.getPolyhedra()) {
            for (Polyhedron polyhedron2 : other.getPolyhedra()) {
                if (polyhedron1.intersects(polyhedron2)) {
                    return true;
                }
            }
            for (OrientedBoundingBox obb2 : other.getBoundingBoxes()) {
                if (polyhedron1.intersects(new Cuboid(obb2))) {
                    return true;
                }
            }
        }
        for (OrientedBoundingBox obb1 : this.getBoundingBoxes()) {
            for (Polyhedron polyhedron2 : other.getPolyhedra()) {
                if (new Cuboid(obb1).intersects(polyhedron2)) {
                    return true;
                }
            }
            // This is faster than a generic polyhedron intersection test
            for (OrientedBoundingBox obb2 : other.getBoundingBoxes()) {
                if (obb1.intersects(obb2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
