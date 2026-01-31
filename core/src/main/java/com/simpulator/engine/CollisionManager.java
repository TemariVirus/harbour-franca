package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;

public class CollisionManager {

    public static boolean isSeperatingAxis(
        Vector3[] verts1,
        Vector3[] verts2,
        Vector3 axis
    ) {
        float min1 = Float.MAX_VALUE,
            max1 = Float.MIN_VALUE;
        float min2 = Float.MAX_VALUE,
            max2 = Float.MIN_VALUE;

        for (Vector3 vert : verts1) {
            float distance = vert.dot(axis);
            min1 = Math.min(min1, distance);
            max1 = Math.max(max1, distance);
        }

        for (Vector3 vert : verts2) {
            float distance = vert.dot(axis);
            min2 = Math.min(min2, distance);
            max2 = Math.max(max2, distance);
        }

        return max1 < min2 || max2 < min1;
    }

    /**
     * Checks if 2 polygons are intersecting using the separating axis theorem.
     * en.wikipedia.org/wiki/Hyperplane_separation_theorem#Use_in_collision_detection
     *
     * @param verts1 The vertices of the first polygon.
     * @param edges1 The non-parallel edges of the first polygon.
     * @param verts2 The vertices of the second polygon.
     * @param edges2 The non-parallel edges of the second polygon.
     * @return Whether the polygons are intersecting.
     */
    public static boolean polygonIntersects(
        Vector3[] verts1,
        Vector3[] edges1,
        Vector3[] verts2,
        Vector3[] edges2
    ) {
        // Face normals
        if (isSeperatingAxis(verts1, verts2, edges1[0].cpy().crs(edges1[1]))) {
            return false;
        }
        if (isSeperatingAxis(verts1, verts2, edges2[0].cpy().crs(edges2[1]))) {
            return false;
        }
        // Edge cross products
        for (Vector3 edge1 : edges1) {
            for (Vector3 edge2 : edges2) {
                Vector3 axis = edge1.cpy().crs(edge2);
                if (isSeperatingAxis(verts1, verts2, axis)) {
                    return false;
                }
            }
        }
        return true;
    }
}
