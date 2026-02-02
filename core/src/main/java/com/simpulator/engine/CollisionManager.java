package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/** GJK algorithm and its helper functions. */
class GJK {

    public static <T> void swap(ArrayList<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    public static boolean sameDirection(Vector3 a, Vector3 b) {
        return a.dot(b) > 0;
    }

    private static boolean handleLine(
        ArrayList<Vector3> simplex,
        Vector3 direction
    ) {
        // Line segment AB
        Vector3 ab = simplex.get(1).cpy().sub(simplex.get(0));
        Vector3 ao = simplex.get(0).cpy().scl(-1);

        if (sameDirection(ab, ao)) {
            // Set direction to be perpendicular to AB towards the origin
            direction.set(ab).crs(ao).crs(ab);
        } else {
            // Remove point B
            simplex.remove(1);
            direction.set(ao);
        }
        // Line segment cannot "contain" a point
        return false;
    }

    private static boolean handleTriangle(
        ArrayList<Vector3> simplex,
        Vector3 direction
    ) {
        // Triangle ABC
        Vector3 ab = simplex.get(1).cpy().sub(simplex.get(0));
        Vector3 ac = simplex.get(2).cpy().sub(simplex.get(0));
        Vector3 ao = simplex.get(0).cpy().scl(-1);
        Vector3 abcNormal = ab.cpy().crs(ac);

        if (sameDirection(abcNormal.cpy().crs(ac), ao)) {
            if (sameDirection(ac, ao)) {
                // Remove point B
                simplex.remove(1);
                direction.set(ac).crs(ao).crs(ac);
            } else {
                // Remove point C
                simplex.remove(2);
                return handleLine(simplex, direction);
            }
        } else {
            if (sameDirection(ab.cpy().crs(abcNormal), ao)) {
                // Remove point C
                simplex.remove(2);
                return handleLine(simplex, direction);
            } else {
                if (sameDirection(abcNormal, ao)) {
                    direction.set(abcNormal);
                } else {
                    // Swap B and C
                    swap(simplex, 1, 2);
                    direction.set(abcNormal).scl(-1);
                }
            }
        }
        // Triangle is too thin to "contain" a point in 3D space
        return false;
    }

    private static boolean handleTetrahedron(
        ArrayList<Vector3> simplex,
        Vector3 direction
    ) {
        // Tetrahedron ABCD
        Vector3 ab = simplex.get(1).cpy().sub(simplex.get(0));
        Vector3 ac = simplex.get(2).cpy().sub(simplex.get(0));
        Vector3 ad = simplex.get(3).cpy().sub(simplex.get(0));
        Vector3 ao = simplex.get(0).cpy().scl(-1);

        Vector3 abcNormal = ab.cpy().crs(ac);
        Vector3 acdNormal = ac.cpy().crs(ad);
        Vector3 adbNormal = ad.cpy().crs(ab);

        if (sameDirection(abcNormal, ao)) {
            // Remove point D
            simplex.remove(3);
            return handleTriangle(simplex, direction);
        }
        if (sameDirection(acdNormal, ao)) {
            // Remove point B
            simplex.remove(1);
            return handleTriangle(simplex, direction);
        }
        if (sameDirection(adbNormal, ao)) {
            // Remove point C
            simplex.remove(2);
            // Swap B and D
            swap(simplex, 1, 2);
            return handleTriangle(simplex, direction);
        }

        // Origin is inside tetrahedron
        return true;
    }

    /**
     * Returns true if the simplex contains the origin, false otherwise.
     * Updates the simplex and the next direction to check if the origin is not contained.
     */
    private static boolean simplexContainsOrigin(
        ArrayList<Vector3> simplex,
        Vector3 direction
    ) {
        switch (simplex.size()) {
            case 0:
            case 1:
                throw new IllegalArgumentException(
                    "Simplex must have at least 2 points."
                );
            case 2:
                return handleLine(simplex, direction);
            case 3:
                return handleTriangle(simplex, direction);
            case 4:
                return handleTetrahedron(simplex, direction);
            default:
                throw new IllegalArgumentException(
                    "Simplex has too many points."
                );
        }
    }

    /**
     * Returns whether 2 convex shapes are intersecting.
     * The simplex containing the origin is stored in simplex.
     */
    public static boolean isIntersecting(
        GJKTarget shape1,
        GJKTarget shape2,
        ArrayList<Vector3> simplex
    ) {
        Vector3 direction = new Vector3(1, 1, 1).nor();

        Vector3 simplexNextPoint = shape1
            .furthestPoint(direction, false)
            .sub(shape2.furthestPoint(direction, true));
        simplex.clear();
        simplex.add(0, simplexNextPoint);
        direction = simplexNextPoint.cpy().scl(-1);
        while (true) {
            simplexNextPoint = shape1
                .furthestPoint(direction, false)
                .sub(shape2.furthestPoint(direction, true));
            if (
                simplexNextPoint.dot(direction) <= 0 ||
                simplex.contains(simplexNextPoint)
            ) {
                // Simplex cannot possibly contain origin; no collision
                return false;
            }

            simplex.add(0, simplexNextPoint);
            if (GJK.simplexContainsOrigin(simplex, direction)) {
                // Simplex contains origin; collision detected
                return true;
            }
        }
    }
}

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
     * https://en.wikipedia.org/wiki/Hyperplane_separation_theorem#Use_in_collision_detection
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

    /**
     * Checks if 2 convex shapes are intersecting using the GJK algorithm.
     * If they intersect, the minimum translation vector (moving movable) to separate them is stored in outMtv.
     * The previous frame's minimum translation vector may be provided to speed up the calculation.
     * https://en.wikipedia.org/wiki/Gilbert%E2%80%93Johnson%E2%80%93Keerthi_distance_algorithm
     */
    public static boolean intersects(
        GJKTarget movable,
        GJKTarget immovable,
        Vector3 outMtv
    ) {
        ArrayList<Vector3> polytope = new ArrayList<>();
        if (!GJK.isIntersecting(movable, immovable, polytope)) {
            return false;
        }

        // TODO: EPA
        return true;
    }
}
