package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Movable;

public class MovableCamera implements Movable {

    private Camera camera;

    public MovableCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void translate(Vector3 delta) {
        camera.translate(delta);
        camera.update();
    }

    @Override
    public void rotate(Vector3 axis, float radians) {
        camera.rotate(axis, radians * (float) (180.0 / Math.PI));
        camera.update();
    }
}
