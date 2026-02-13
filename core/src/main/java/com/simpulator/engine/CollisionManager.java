package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/** Collision detection and resolution. */
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
     * Returns whether 2 convex shapes are intersecting using the GJK algorithm.
     * If they intersect, the minimum translation vector (moving movable) to separate them is stored in outMtv.
     * https://en.wikipedia.org/wiki/Gilbert%E2%80%93Johnson%E2%80%93Keerthi_distance_algorithm
     */
    public static boolean intersects(
        GJKTarget movable,
        GJKTarget immovable,
        Vector3 outMtv
    ) {
        final int MAX_ITERATIONS = 500;
        // EPA is numerically sensitive
        // Prevent extra iterations due to rounding errors
        final float THRESHOLD = 1e-3f;

        ArrayList<Vector3> simplex = new ArrayList<>();
        if (!GJK.isIntersecting(movable, immovable, simplex)) {
            return false;
        }

        // Collision detected; Expand Polytope Algorithm to find MTV
        Polytope polytope = new Polytope(simplex);
        for (int iters = 0; iters < MAX_ITERATIONS; iters++) {
            Vector3 faceNormal = polytope.closestFaceNormalToOrigin();
            float faceDistance = faceNormal.len();
            faceNormal.nor();

            Vector3 support = movable
                .furthestPoint(faceNormal, false)
                .sub(immovable.furthestPoint(faceNormal, true));
            float distance = support.dot(faceNormal);
            if (
                Math.abs(distance - faceDistance) <
                Math.max(THRESHOLD, distance * THRESHOLD)
            ) {
                // No closer point found; face normal is the MTV
                outMtv.set(faceNormal).scl(-distance);
                return true;
            }

            polytope.expand(support);
        }

        // Max iterations reached; Return closest result to MTV so far
        outMtv.set(polytope.closestFaceNormalToOrigin()).scl(-1 - THRESHOLD);
        return true;
    }
}

/** GJK algorithm implementation. */
class GJK {

    /** Returns whether 2 vectors generally point in the same direction. */
    public static boolean sameDirection(Vector3 a, Vector3 b) {
        return a.dot(b) > 0;
    }

    /** Handles the case where the simplex is a line segment. */
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

    /** Handles the case where the simplex is a triangle. */
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
                    ListUtil.swap(simplex, 1, 2);
                    direction.set(abcNormal).scl(-1);
                }
            }
        }
        // Triangle is too thin to "contain" a point in 3D space
        return false;
    }

    /** Handles the case where the simplex is a tetrahedron. */
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
            ListUtil.swap(simplex, 1, 2);
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
        final int MAX_ITERATIONS = 100;
        Vector3 direction = new Vector3(1, 1, 1).nor();

        Vector3 simplexNextPoint = shape1
            .furthestPoint(direction, false)
            .sub(shape2.furthestPoint(direction, true));
        simplex.clear();
        simplex.add(0, simplexNextPoint);
        direction = simplexNextPoint.cpy().scl(-1);
        for (int iters = 0; iters < MAX_ITERATIONS; iters++) {
            simplexNextPoint = shape1
                .furthestPoint(direction, false)
                .sub(shape2.furthestPoint(direction, true));
            if (
                simplexNextPoint.dot(direction) < 1e-5 ||
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

        // Max iterations reached; assume no collision
        return false;
    }
}

/** 3D Polytope used in the Expanding Polytope Algorithm. */
class Polytope {

    private ArrayList<Vector3> vertices;
    private ArrayList<Face> faces;

    class Pair<T> {

        public T first;
        public T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pair<?> pair = (Pair<?>) obj;
            return first.equals(pair.first) && second.equals(pair.second);
        }
    }

    class Face {

        /** Indices into the vertices of the polytope. */
        public int p1Index;
        public int p2Index;
        public int p3Index;

        public Vector3 normal;
        public float distance;

        public Face(
            ArrayList<Vector3> vertices,
            int p1Index,
            int p2Index,
            int p3Index
        ) {
            this.p1Index = p1Index;
            this.p2Index = p2Index;
            this.p3Index = p3Index;
            updateNormal(vertices);
        }

        public void updateNormal(ArrayList<Vector3> vertices) {
            Vector3 a = vertices.get(p1Index);
            Vector3 b = vertices.get(p2Index);
            Vector3 c = vertices.get(p3Index);
            Vector3 ab = b.cpy().sub(a);
            Vector3 ac = c.cpy().sub(a);
            this.normal = ab.crs(ac).nor();
            this.distance = this.normal.dot(a);
            // Ensure the normal points away from the origin
            if (this.distance < 0) {
                this.normal.scl(-1);
                this.distance = -this.distance;
            }
        }
    }

    /** Create a new polytope with the same shape as the given tetrahedron. */
    public Polytope(ArrayList<Vector3> tetrahedron) {
        assert tetrahedron.size() == 4 : "Simplex must be a tetrahedron.";

        this.vertices = new ArrayList<>(tetrahedron);
        this.faces = new ArrayList<>();

        faces.add(new Face(this.vertices, 0, 1, 2));
        faces.add(new Face(this.vertices, 0, 3, 1));
        faces.add(new Face(this.vertices, 0, 2, 3));
        faces.add(new Face(this.vertices, 1, 3, 2));
    }

    /** Returns the polytope's face that is closest to the origin. */
    public Vector3 closestFaceNormalToOrigin() {
        float minDistance = Float.POSITIVE_INFINITY;
        Face closestFace = null;
        for (Face face : faces) {
            if (face.distance < minDistance) {
                minDistance = face.distance;
                closestFace = face;
            }
        }
        return closestFace.normal.cpy().scl(closestFace.distance);
    }

    /** Adds a new point to the polytope, assuming the point is outside the polytope. */
    public void expand(Vector3 support) {
        // Remove faces that can "see" the support point
        // and collect their edges
        ArrayList<Pair<Integer>> uniqueEdgeIndices = new ArrayList<>();
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            if (GJK.sameDirection(face.normal, support)) {
                addIfUniqueEdge(uniqueEdgeIndices, face.p1Index, face.p2Index);
                addIfUniqueEdge(uniqueEdgeIndices, face.p2Index, face.p3Index);
                addIfUniqueEdge(uniqueEdgeIndices, face.p3Index, face.p1Index);
                ListUtil.swapRemove(faces, i--);
            }
        }

        // Add new faces along the collected edges
        vertices.add(support);
        for (Pair<Integer> pair : uniqueEdgeIndices) {
            faces.add(
                new Face(vertices, pair.first, pair.second, vertices.size() - 1)
            );
        }
    }

    private void addIfUniqueEdge(
        ArrayList<Pair<Integer>> edges,
        int aIndex,
        int bIndex
    ) {
        int reverseIndex = edges.indexOf(new Pair<Integer>(bIndex, aIndex));
        if (reverseIndex < 0) {
            edges.add(new Pair<Integer>(aIndex, bIndex));
        } else {
            ListUtil.swapRemove(edges, reverseIndex);
        }
    }
}
