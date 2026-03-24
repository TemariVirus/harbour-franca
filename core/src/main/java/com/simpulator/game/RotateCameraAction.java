package com.simpulator.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.game.entities.CameraEntity;

public class RotateCameraAction implements Action<MouseManager.MouseMoveEvent> {

    private CameraEntity entity;
    private float sensitivity;

    public RotateCameraAction(CameraEntity entity, float sensitivity) {
        this.entity = entity;
        this.sensitivity = sensitivity;
    }

    @Override
    public void act(MouseManager.MouseMoveEvent event) {
        if (event.deltaX != 0) {
            entity.rotate(
                Vector3.Y,
                (float) Math.toRadians(-event.deltaX * sensitivity)
            );
        }

        // Camera direction is sometimes not properly normalised
        Vector3 camDirection = entity.getCamera().direction.cpy().nor();
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
            float rotateAmount = (float) Math.toRadians(
                -event.deltaY * sensitivity
            );

            // Prevent going too close to the poles to avoid gimbal lock
            float maxAngle = currentAngle - 0.001f;
            float minAngle = currentAngle - 3.141f;
            rotateAmount = MathUtils.clamp(rotateAmount, minAngle, maxAngle);
            entity.rotate(axis, rotateAmount);
        }
    }
}
