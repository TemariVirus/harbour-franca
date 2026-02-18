package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.simpulator.engine.Action;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.MouseManager;

public class SpawnBulletAction
    implements Action<MouseManager.MouseButtonEvent>
{

    private EntityManager entityManager;
    private TextureRegion bulletTexture;
    private Camera player;
    private float cooldown;
    private float speed;
    private float lastSpawnTime;

    public SpawnBulletAction(
        EntityManager entityManager,
        TextureRegion bulletTexture,
        Camera player,
        float cooldown,
        float speed
    ) {
        this.entityManager = entityManager;
        this.bulletTexture = bulletTexture;
        this.player = player;
        this.cooldown = cooldown;
        this.speed = speed;
        this.lastSpawnTime = Float.MIN_VALUE;
    }

    @Override
    public void act(MouseManager.MouseButtonEvent event) {
        if (lastSpawnTime + cooldown > event.timestamp) {
            // Cooldown is not over yet
            return;
        }

        entityManager.add(
            new BulletEntity(
                player.position.cpy(),
                new Vector2(
                    bulletTexture.getRegionWidth(),
                    bulletTexture.getRegionHeight()
                ),
                player.view.getRotation(new Quaternion()).conjugate(),
                bulletTexture,
                5000f / speed,
                speed
            )
        );
        lastSpawnTime = event.timestamp;
    }
}
