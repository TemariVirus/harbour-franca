package com.simpulator.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.CollidableEntity;
import com.simpulator.engine.SoundManager;

public class PlayerEntity extends CuboidEntity {

    private SoundManager sounds;
    private String hurtSfx;

    public PlayerEntity(
        SoundManager sounds,
        Vector3 position,
        Vector2 size,
        float thickness,
        Quaternion rotation,
        Texture texture,
        boolean isMovable,
        String hurtSfx
    ) {
        super(position, size, thickness, rotation, texture, isMovable);
        this.sounds = sounds;
        this.hurtSfx = hurtSfx;
    }

    @Override
    public void onCollision(CollidableEntity other) {
        if (other instanceof RoamingEntity) {
            sounds.play(hurtSfx);
        }
        super.onCollision(other);
    }
}
