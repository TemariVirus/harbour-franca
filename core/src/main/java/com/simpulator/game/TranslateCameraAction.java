package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Action;
import com.simpulator.engine.KeyboardManager;

public class TranslateCameraAction implements Action<KeyboardManager.KeyEvent> {

    private Camera camera;
    /** Velocity in world units/s */
    private Vector3 velocity;

    public TranslateCameraAction(Camera camera, Vector3 velocity) {
        this.camera = camera;
        this.velocity = velocity;
    }

    @Override
    public void act(KeyboardManager.KeyEvent event) {
        Vector3 right = camera.direction.cpy().crs(Vector3.Y).nor();
        Vector3 forward = right.cpy().crs(Vector3.Y); // No need to normalise

        camera.position.add(right.scl(velocity.x * event.deltaTime)); // Relative x-axis
        camera.position.add(0, velocity.y * event.deltaTime, 0); // Absolute y-axis
        camera.position.add(forward.scl(velocity.z * event.deltaTime)); // Relative z-axis
        camera.update();
    }
}
