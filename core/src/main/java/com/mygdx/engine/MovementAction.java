package com.mygdx.engine;

public class MovementAction implements IAction {

    /** The entity this action acts on. */
    protected Entity target;
    /** Horizontal speed in pixels/s */
    private float speedX;
    /** Vertical speed in pixels/s */
    private float speedY;
    /** Rotation speed in degrees/s */
    private float rotationSpeed;

    public MovementAction(
        Entity target,
        float speedX,
        float speedY,
        float rotationSpeed
    ) {
        this.target = target;
        this.speedX = speedX;
        this.speedY = speedY;
        this.rotationSpeed = rotationSpeed;
    }

    public float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void act(float deltaTime) {
        target.translate(speedX * deltaTime, speedY * deltaTime);
        target.rotate(rotationSpeed * deltaTime);
    }
}
