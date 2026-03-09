package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Action;
import com.simpulator.engine.KeyboardManager;

public class TrnaslateCameraAction implements Action<KeyboardManager.KeyEvent> {

    private CameraEntity entity;
    /** Velocity in world units/s */
    private Vector3 velocity;

    public TrnaslateCameraAction(CameraEntity entity, Vector3 velocity) {
        this.entity = entity;
        this.velocity = velocity;
    }

    @Override
    public void act(KeyboardManager.KeyEvent event) {
        Vector3 right = entity.getCamera().direction.cpy().crs(Vector3.Y).nor();
        Vector3 forward = right.cpy().crs(Vector3.Y); // No need to normalise

        entity.translate(right.scl(velocity.x * event.deltaTime)); // Relative x-axis
        entity.translate(new Vector3(0, velocity.y * event.deltaTime, 0)); // Absolute y-axis
        entity.translate(forward.scl(velocity.z * event.deltaTime)); // Relative z-axis
    }
}
