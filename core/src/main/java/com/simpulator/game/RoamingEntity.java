package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RoamingEntity extends CuboidEntity {

    private float speed = 100f;
    private Vector3 targetPosition;
    private Vector3 moveDirection;

    // Adjust values to size of playable game area
    private static final float MIN_X = -500f;
    private static final float MAX_X = 500f;
    private static final float MIN_Z = -500f;
    private static final float MAX_Z = 100f;

    public RoamingEntity(
        Vector3 position,
        Vector2 size,
        float thickness,
        Texture texture
    ) {
        super(
            position,
            size,
            thickness,
            new Quaternion().idt(),
            texture,
            false
        );
        this.targetPosition = new Vector3();
        this.moveDirection = new Vector3();

        pickNewWaypoint();
    }

    private void pickNewWaypoint() {
        float randomX = MathUtils.random(MIN_X, MAX_X);
        float randomZ = MathUtils.random(MIN_Z, MAX_Z);

        targetPosition.set(randomX, getPosition().y, randomZ);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector3 currentPos = getPosition();
        float distanceToTarget = currentPos.dst(targetPosition);

        if (distanceToTarget < 10f) {
            pickNewWaypoint();
        } else {
            moveDirection.set(targetPosition).sub(currentPos).nor();

            Vector3 step = moveDirection.scl(speed * deltaTime);
            this.translate(step);
        }
    }
}
