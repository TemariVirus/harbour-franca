package com.simpulator.engine;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/** An entity that can collide with other entities. */
public abstract class CollidableEntity extends Entity implements ColliderMesh {

    protected CollidableEntity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        EntityRenderer renderer
    ) {
        super(position, size, rotation, renderer);
    }

    /** Called when another entity in the same EntityManager is colliding with this entity. */
    public abstract void onCollision(CollidableEntity other);
}
