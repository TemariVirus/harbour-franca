package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;

/** An object that can be moved in 3D space. */
public interface Movable {
    /** Translates the object by the given delta vector. */
    public void translate(Vector3 delta);

    /** Rotates the object around the given axis by the given angle in radians. */
    public void rotate(Vector3 axis, float radians);
}
