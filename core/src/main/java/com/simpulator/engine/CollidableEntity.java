package com.simpulator.engine;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public abstract class CollidableEntity extends Entity implements ColliderMesh {

    public CollidableEntity(
        Vector3 position,
        Vector2 size,
        Quaternion rotation,
        TextureRegion textureRegion
    ) {
        super(position, size, rotation, textureRegion);
    }

    /** Called when another entity in the same EntityManager is colliding with this entity. */
    public abstract void onCollision(CollidableEntity other);
}
