package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Action;
import com.simpulator.engine.MouseManager;

public class RotateCameraAction implements Action<MouseManager.MouseMoveEvent> {

    private Camera camera;
    private float sensitivity;

    public RotateCameraAction(Camera camera, float sensitivity) {
        this.camera = camera;
        this.sensitivity = sensitivity;
    }

    @Override
    public void act(MouseManager.MouseMoveEvent event) {
        boolean cameraMoved = false;

        if (event.deltaX != 0) {
            camera.rotate(Vector3.Y, -event.deltaX * sensitivity);
            cameraMoved = true;
        }

        // Prevent camera from going too close to the poles to avoid gimbal lock
        if (
            (event.deltaY > 0 && camera.direction.y > -0.99f) ||
            (event.deltaY < 0 && camera.direction.y < 0.99f)
        ) {
            Vector3 axis = camera.direction.cpy().crs(Vector3.Y);
            float currentAngle = (float) Math.acos(
                camera.direction.dot(Vector3.Y)
            );
            float rotateAmount =
                -event.deltaY *
                sensitivity *
                // Convert to radians
                ((float) Math.PI / 180f);
            float newAngle = MathUtils.clamp(
                currentAngle + rotateAmount,
                0.005f,
                (float) Math.PI - 0.005f
            );
            rotateAmount = newAngle - currentAngle;

            // Convert back to degrees
            camera.rotate(axis, rotateAmount * (180f / (float) Math.PI));
            cameraMoved = true;
        }

        if (cameraMoved) {
            camera.update();
        }
    }
}
