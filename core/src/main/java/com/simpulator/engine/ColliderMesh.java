package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/** A mesh used for detecting collisions, made out of polyhedra. */
public interface ColliderMesh extends Moveable {
    /** Returns all polyhedra in the mesh. */
    public Iterable<Polyhedron> getPolyhedra();

    /** Returns the bounding box of the entire mesh. */
    public default BoundingBox getBounds() {
        BoundingBox bounds = new BoundingBox();
        for (Polyhedron polyhedron : getPolyhedra()) {
            bounds.ext(polyhedron.getBounds());
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
        }
        return false;
    }

    /**
     * Returns whether this mesh intersects with another mesh.
     * If they intersect, the minimum translation vector (moving this ColliderMesh)
     * to separate them is stored in outMtv.
     */
    public default boolean intersects(ColliderMesh immovable, Vector3 outMtv) {
        if (!this.getBounds().intersects(immovable.getBounds())) {
            return false;
        }

        float intersectCount = 0;
        Vector3 mtv = new Vector3().setZero();
        Vector3 tempMtv = new Vector3();
        for (Polyhedron polyhedron1 : this.getPolyhedra()) {
            for (Polyhedron polyhedron2 : immovable.getPolyhedra()) {
                if (polyhedron1.intersects(polyhedron2, tempMtv)) {
                    intersectCount++;
                    mtv.add(tempMtv);
                }
            }
        }
        outMtv.set(mtv).scl(1f / intersectCount);
        return intersectCount > 0;
    }
}
