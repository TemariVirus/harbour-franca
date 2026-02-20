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

        // Camera direction is sometimes not properly normalised
        Vector3 camDirection = camera.direction.cpy().nor();
        if (
            (event.deltaY > 0 && camDirection.y > -1f) ||
            (event.deltaY < 0 && camDirection.y < 1f)
        ) {
            Vector3 axis = camDirection.cpy().crs(Vector3.Y);
            if (axis.isZero()) {
                // If axis is zero, it means we are looking straight up or down
                // Use a default axis to prevent gimbal lock
                axis.set(Vector3.X);
            }

            float currentAngle = (float) Math.acos(
                MathUtils.clamp(camDirection.dot(Vector3.Y), -1, 1)
            );
            float rotateAmount = -event.deltaY * sensitivity;

            // Prevent camera from going too close to the poles to avoid gimbal lock
            float maxAngle = (float) Math.toDegrees(currentAngle) - 0.01f;
            float minAngle = (float) Math.toDegrees(currentAngle) - 179.99f;
            rotateAmount = MathUtils.clamp(rotateAmount, minAngle, maxAngle);
            camera.rotate(axis, rotateAmount);
            cameraMoved = true;
        }

        if (cameraMoved) {
            camera.update();
        }
    }
}
