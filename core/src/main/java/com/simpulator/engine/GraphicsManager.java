package com.simpulator.engine;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class GraphicsManager implements Disposable {

    protected SpriteBatch batch = new SpriteBatch();
    protected boolean isRendering = false;

    private void beginRender() {
        if (isRendering) return;

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

        Arrays.sort(entities, new Comparator<Entity>() {
            Vector3 tmpA = new Vector3();
            Vector3 tmpB = new Vector3();

            @Override
            public int compare(Entity a, Entity b) {
                if (a == null && b == null) return 0;
                if (a == null) return 1;
                if (b == null) return -1;

                tmpA.set(a.getPosition());
                camera.project(tmpA);
                float za = tmpA.z;

                tmpB.set(b.getPosition());
                camera.project(tmpB);
                float zb = tmpB.z;

                return Float.compare(zb, za);
            }
        });

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

    @Override
    public void dispose() {
        batch.dispose();
    }
}
