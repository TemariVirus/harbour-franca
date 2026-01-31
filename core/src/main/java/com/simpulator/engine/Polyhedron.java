package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public abstract class Polyhedron {

    /** Returns the total number of faces in the polyhedron. */
    public abstract int getFaceCount();

    /** Returns the face vertices in clockwise order. Must return at least 2 vertices. */
    public abstract Vector3[] getFaceVertices(int faceIndex);

    /** Returns a bounding box containing the polyhedron. */
    public abstract BoundingBox getBounds();

    /** Returns all non-pallel edges in clockwise order. Must return at least 2 edges. */
    public Vector3[] getNonParallelEdges(int faceIndex) {
        Vector3[] verts = getFaceVertices(faceIndex);
        assert verts.length >= 2;

        // Assume all edges are not parallel with each other
        Vector3[] edges = new Vector3[verts.length];
        edges[0] = verts[0].cpy().sub(verts[verts.length - 1]);
        for (int i = 1; i < verts.length; i++) {
            Vector3 current = verts[i - 1];
            Vector3 next = verts[i];
            edges[i] = next.cpy().sub(current);
        }
        return edges;
    }

    /** Returns whether this polyhedron intersects with another polyhedron. */
    public boolean intersects(Polyhedron other) {
        if (!this.getBounds().intersects(other.getBounds())) {
            return false;
        }

        for (int i = 0; i < this.getFaceCount(); i++) {
            Vector3[] verts1 = this.getFaceVertices(i);
            Vector3[] edges1 = this.getNonParallelEdges(i);
            for (int j = 0; j < other.getFaceCount(); j++) {
                if (
                    CollisionManager.polygonIntersects(
                        verts1,
                        edges1,
                        other.getFaceVertices(j),
                        other.getNonParallelEdges(j)
                    )
                ) {
                    return true;
                }
            }
        }
        return false;
    }
}
