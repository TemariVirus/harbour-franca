package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;

public class RectangleCollider {

    public static boolean intersects(
        Vector3 pos1,
        Vector3 edge11,
        Vector3 edge12,
        Vector3 pos2,
        Vector3 edge21,
        Vector3 edge22
    ) {
        Vector3[] verts1 = new Vector3[] {
            pos1.cpy(),
            pos1.cpy().add(edge11),
            pos1.cpy().add(edge12),
            pos1.cpy().add(edge11).add(edge12),
        };
        Vector3[] verts2 = new Vector3[] {
            pos2.cpy(),
            pos2.cpy().add(edge21),
            pos2.cpy().add(edge22),
            pos2.cpy().add(edge21).add(edge22),
        };

        // TODO: optimize with bounding box check

        // Face normals
        if (isSeperatingAxis(verts1, verts2, edge11.cpy().crs(edge12))) {
            return false;
        }
        if (isSeperatingAxis(verts1, verts2, edge21.cpy().crs(edge22))) {
            return false;
        }
        // Edge cross products
        if (isSeperatingAxis(verts1, verts2, edge11.cpy().crs(edge21))) {
            return false;
        }
        if (isSeperatingAxis(verts1, verts2, edge11.cpy().crs(edge22))) {
            return false;
        }
        if (isSeperatingAxis(verts1, verts2, edge12.cpy().crs(edge21))) {
            return false;
        }
        if (isSeperatingAxis(verts1, verts2, edge12.cpy().crs(edge22))) {
            return false;
        }
        return true;
    }

    private static boolean isSeperatingAxis(
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
}
