package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import java.util.List;

/**
 * Determines which entity (if any) a camera is currently looking at
 * and is close enough to interact with.
 */
public class EntityTargeter<T extends Entity> {

    private final List<T> targets;
    private final float range;

    public EntityTargeter(List<T> targets, float range) {
        this.targets = targets;
        this.range = range;
    }

    /**
     * Returns the closest target that the camera is looking at, or null if none.
     * Only entities within `range` units of the camera are considered.
     */
    public T getClosest(Camera camera) {
        T closestEntity = null;
        float closestDistance = range;
        for (T entity : targets) {
            Vector3 toEntity = entity.getPosition().sub(camera.position);
            float flatDistance = new Vector3(toEntity.x, 0, toEntity.z).len();
            if (flatDistance >= closestDistance) {
                continue;
            }

            // Are we looking at the entity?
            if (flatDistance > 1e-6f) {
                // Horizontal check
                float rayAngle = (float) Math.atan2(
                    camera.direction.z,
                    camera.direction.x
                );
                float entityAngle = (float) Math.atan2(toEntity.z, toEntity.x);
                float angleRange = (float) Math.atan(
                    (entity.getSize().x * 0.5f) / flatDistance
                );
                float angleDiff = rayAngle - entityAngle;
                if (angleDiff < -Math.PI) angleDiff += Math.PI * 2;
                if (angleDiff > Math.PI) angleDiff -= Math.PI * 2;
                if (Math.abs(angleDiff) > Math.abs(angleRange)) {
                    continue;
                }
            }
            {
                // Vertical check
                float flatRayLen = new Vector3(
                    camera.direction.x,
                    0,
                    camera.direction.z
                ).len();
                if (flatRayLen < 1e-6f) {
                    continue;
                }
                float raySlopeY = camera.direction.y / flatRayLen;
                float projectedY =
                    camera.position.y + (raySlopeY * flatDistance);
                float lowerY = entity.getPosition().y - entity.getSize().y / 2f;
                float upperY = entity.getPosition().y + entity.getSize().y / 2f;
                if (projectedY < lowerY || projectedY > upperY) {
                    continue;
                }
            }

            if (flatDistance < closestDistance) {
                closestEntity = entity;
                closestDistance = flatDistance;
            }
        }

        return closestEntity;
    }
}
