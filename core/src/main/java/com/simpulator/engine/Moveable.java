package com.simpulator.engine;

import com.badlogic.gdx.math.Vector3;

public interface Moveable {
    public void translate(Vector3 delta);
    public void rotate(Vector3 axis, float radians);
}
