package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Action;
import com.simpulator.engine.KeyboardManager.KeyEvent;

public class MoveCameraAction implements Action<KeyEvent> {

    /** The camera this action acts on. */
    protected Camera target;
    /** Velocity in world units/s */
    private Vector3 velocity;
    /** Axis of rotation. */
    private Vector3 rotationAxis;
    /** Rotation speed in radians/s. */
    private float rotationSpeed;

    public MoveCameraAction(
        Camera target,
        Vector3 velocity,
        Vector3 rotationAxis,
        float rotationSpeed
    ) {
        this.target = target;
        setVelocity(velocity);
        setRotationAxis(rotationAxis);
        this.rotationSpeed = rotationSpeed;
    }

    public MoveCameraAction(Camera target, Vector3 velocity) {
        this.target = target;
        setVelocity(velocity);
        this.rotationAxis = new Vector3();
        this.rotationSpeed = 0;
    }

    public MoveCameraAction(
        Camera target,
        Vector3 rotationAxis,
        float rotationSpeed
    ) {
        this.target = target;
        this.velocity = new Vector3();
        setRotationAxis(rotationAxis);
        this.rotationSpeed = rotationSpeed;
    }

    public Vector3 getVelocity() {
        return velocity.cpy();
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity.cpy();
    }

    public Vector3 getRotationAxis() {
        return rotationAxis.cpy();
    }

    public void setRotationAxis(Vector3 rotationAxis) {
        this.rotationAxis = rotationAxis.cpy().nor();
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void act(float deltaTime, KeyEvent event) {
        if (deltaTime <= 0) {
            return;
        }

        target.translate(velocity.cpy().scl(deltaTime));
        target.rotate(rotationAxis, rotationSpeed * deltaTime);
        target.update();
    }
}
