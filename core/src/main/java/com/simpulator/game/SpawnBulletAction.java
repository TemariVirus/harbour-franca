package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.simpulator.engine.Action;
import com.simpulator.engine.MouseManager;

public class SpawnBulletAction
    implements Action<MouseManager.MouseButtonEvent>
{

    private EntityRemover entityRemover;
    private Texture bulletTexture;
    private SoundPlayer hitSound;
    private Camera player;
    private float cooldown;
    private float speed;
    private float lastSpawnTime;

    public SpawnBulletAction(
        EntityRemover entityRemover,
        Texture bulletTexture,
        SoundPlayer hitSound,
        Camera player,
        float cooldown,
        float speed
    ) {
        this.entityRemover = entityRemover;
        this.bulletTexture = bulletTexture;
        this.hitSound = hitSound;
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

        entityRemover
            .getEntityManager()
            .add(
                new BulletEntity(
                    entityRemover,
                    player.position.cpy(),
                    new Vector2(
                        bulletTexture.getWidth(),
                        bulletTexture.getHeight()
                    ),
                    5f,
                    player.view.getRotation(new Quaternion()).conjugate(),
                    bulletTexture,
                    hitSound,
                    5000f / speed,
                    speed
                )
            );
        lastSpawnTime = event.timestamp;
    }
}
