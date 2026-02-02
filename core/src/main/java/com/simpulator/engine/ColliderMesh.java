package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import java.util.Iterator;

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
        for (Polyhedron polyhedron1 : new PolyhedraIterator(this)) {
            for (Polyhedron polyhedron2 : new PolyhedraIterator(immovable)) {
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

class PolyhedraIterator implements Iterable<Polyhedron>, Iterator<Polyhedron> {

    private Polyhedron[] polyhedra;
    private OrientedBoundingBox[] obbs;
    private int index;

    public PolyhedraIterator(ColliderMesh mesh) {
        this.polyhedra = mesh.getPolyhedra();
        this.obbs = mesh.getBoundingBoxes();
        this.index = 0;
    }

    @Override
    public Iterator<Polyhedron> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return index < polyhedra.length + obbs.length;
    }

    @Override
    public Polyhedron next() {
        if (index < polyhedra.length) {
            return polyhedra[index++];
        } else {
            return new Cuboid(obbs[index++ - polyhedra.length]);
        }
    }
}
