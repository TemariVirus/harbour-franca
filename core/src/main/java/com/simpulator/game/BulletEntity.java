package com.simpulator.game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;

public class BulletEntity extends Entity {

    /** The remaining lifetime of this bullet, in seconds. */
    private float lifetime;
    private Vector3 velocity;

    public BulletEntity(
        Vector3 position,
        Vector2 size,
        Quaternion rotation,
        TextureRegion textureRegion,
        float lifetime,
        float speed
    ) {
        super(position, size, rotation, textureRegion);
        this.lifetime = lifetime;
        this.velocity = new Vector3(0, 0, -speed).mul(rotation);
    }

    @Override
    public void update(float deltaTime) {
        lifetime -= deltaTime;
        if (lifetime < 0) {
            // TODO: delete dead bullets
            return;
        }
        translate(velocity.cpy().scl(deltaTime));
        // TODO: delete entities and make sound on contact
    }
}
