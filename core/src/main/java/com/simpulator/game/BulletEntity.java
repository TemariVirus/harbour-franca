package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.CollidableEntity;

public class BulletEntity extends CuboidEntity {

    private EntityRemover entityRemover;
    private SoundPlayer hitSound;
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
        SoundPlayer hitSound,
        float lifetime,
        float speed
    ) {
        super(position, size, thickness, rotation, texture, false);
        this.entityRemover = entityRemover;
        this.hitSound = hitSound;
        this.lifetime = lifetime;
        this.velocity = new Vector3(0, 0, -speed).mul(rotation);
    }

    @Override
    public void onCollision(CollidableEntity other) {
        entityRemover.markForRemoval(this);
        entityRemover.markForRemoval(other);
        hitSound.play();
    }

    @Override
    public void update(float deltaTime) {
        lifetime -= deltaTime;
        if (lifetime < 0) {
            entityRemover.markForRemoval(this);
            return;
        }
        translate(velocity.cpy().scl(deltaTime));
    }
}
