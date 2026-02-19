package com.simpulator.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.ColliderMesh;
import com.simpulator.engine.Entity;

public class BulletEntity extends CollidableEntity {

    private EntityRemover entityRemover;
    private Sound hitSound;
    /** The remaining lifetime of this bullet, in seconds. */
    private float lifetime;
    private Vector3 velocity;

    public BulletEntity(
        EntityRemover entityRemover,
        Vector3 position,
        Vector2 size,
        float thickness,
        Quaternion rotation,
        Texture texture,
        Sound hitSound,
        float lifetime,
        float speed
    ) {
        super(
            entityRemover.getEntityManager(),
            position,
            size,
            thickness,
            rotation,
            texture,
            false
        );
        this.entityRemover = entityRemover;
        this.hitSound = hitSound;
        this.lifetime = lifetime;
        this.velocity = new Vector3(0, 0, -speed).mul(rotation);
    }

    @Override
    public void update(float deltaTime) {
        lifetime -= deltaTime;
        if (lifetime < 0) {
            entityRemover.markForRemoval(this);
            return;
        }
        translate(velocity.cpy().scl(deltaTime));

        for (Entity entity : entityRemover.getEntityManager().getEntities()) {
            if (entity == this) continue;
            if (entity instanceof ColliderMesh) {
                if (intersects((ColliderMesh) entity)) {
                    entityRemover.markForRemoval(this);
                    entityRemover.markForRemoval(entity);
                    hitSound.play();
                    break;
                }
            }
        }
    }
}
