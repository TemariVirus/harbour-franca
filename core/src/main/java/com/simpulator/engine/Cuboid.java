package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

/** Converts an oriented bounding box to the Polyhedron class. */
public class Cuboid extends Polyhedron {

    private static final int[][] FACE_VERTEX_INDICES = new int[][] {
        { 0, 1, 3, 2 }, // Front face
        { 4, 5, 7, 6 }, // Back face
        { 0, 2, 6, 4 }, // Left face
        { 3, 1, 5, 7 }, // Right face
        { 2, 3, 7, 6 }, // Bottom face
        { 4, 5, 1, 0 }, // Top face
    };

    private OrientedBoundingBox obb;

    public Cuboid(OrientedBoundingBox obb) {
        this.obb = obb;
    }

    @Override
    public int getFaceCount() {
        return FACE_VERTEX_INDICES.length;
    }

    @Override
    public Vector3[] getFaceVertices(int faceIndex) {
        Vector3[] vertices = obb.getVertices();
        int[] indices = FACE_VERTEX_INDICES[faceIndex];

        Vector3[] faceVertices = new Vector3[indices.length];
        for (int i = 0; i < indices.length; i++) {
            faceVertices[i] = vertices[indices[i]];
        }
        return faceVertices;
    }

    @Override
    public Iterable<Vector3> getAllVertices() {
        return java.util.Arrays.asList(obb.getVertices());
    }

    @Override
    public BoundingBox getBounds() {
        return obb.getBounds();
    }

    @Override
    public Vector3[] getNonParallelEdges(int faceIndex) {
        Vector3[] verts = getFaceVertices(faceIndex);
        return new Vector3[] { verts[1].sub(verts[0]), verts[3].sub(verts[0]) };
    }
}
