package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class RoamingEntity extends CollidableEntity {
    
    private float speed = 100f; 
    private Vector3 targetPosition;
    private Vector3 moveDirection;

    // Adjust values to size of playable game area
    private final float minX = -500f;
    private final float maxX = 500f;
    private final float minZ = -500f;
    private final float maxZ = 100f;

    public RoamingEntity(Vector3 position, Vector2 size, float thickness, Texture texture) {
        super(position, size, thickness, new Quaternion().idt(), texture);
        
        this.targetPosition = new Vector3();
        this.moveDirection = new Vector3();
        
        pickNewWaypoint(); 
    }

    private void pickNewWaypoint() {
        float randomX = MathUtils.random(minX, maxX);
        float randomZ = MathUtils.random(minZ, maxZ);
        
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
            
            Vector3 velocity = moveDirection.scl(speed * deltaTime);
            this.translate(velocity);
        }
    }
}