package com.simpulator.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.RectangleRenderer;
import com.simpulator.engine.scene.TextureCache;

public class GatekeeperEntity extends CuboidEntity {

    private Entity player;

    public GatekeeperEntity(
        Vector3 position,
        TextureCache textures,
        Entity player
    ) {
        super(
            position,
            new Vector3(2f, 2.5f, 0.5f),
            new Quaternion().setFromAxis(Vector3.Y, 0),
            new RectangleRenderer(new TextureRegion(textures.get("Door.png"))),
            false
        );
        this.player = player;
    }

    @Override
    public void update(float deltaTime) {
        // Always face the player
        Vector3 toPlayer = player.getPosition().sub(position);
        float targetAngle = (float) Math.atan2(toPlayer.x, toPlayer.z);
        setRotation(Vector3.Y, targetAngle);
    }
}
