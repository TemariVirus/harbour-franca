package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class GraphicsManager {

    protected SpriteBatch batch = new SpriteBatch();
    protected boolean isRendering = false;

    private final Matrix4 screenProj = new Matrix4();

    private void beginRender() {
        if (isRendering) return;

        screenProj.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(screenProj);

        batch.begin();
        isRendering = true;
    }

    public void renderEntity(Entity entity, Camera camera) {
        if (entity == null) return;
        if (camera == null) throw new IllegalArgumentException("camera cannot be null");

        beginRender();
        entity.render(batch, camera);
    }

    public void renderEntities(Entity[] entities, Camera camera) {
        if (entities == null) return;
        if (camera == null) throw new IllegalArgumentException("camera cannot be null");

        beginRender();
        for (Entity e : entities) {
            if (e != null) e.render(batch, camera);
        }
    }

    public void endRender() {
        if (!isRendering) return;
        batch.end();
        isRendering = false;
    }

    public void dispose() {
        batch.dispose();
    }
}
