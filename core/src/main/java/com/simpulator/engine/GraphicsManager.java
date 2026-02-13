package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import java.util.Arrays;
import java.util.Comparator;

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
        if (camera == null) throw new IllegalArgumentException(
            "camera cannot be null"
        );

        beginRender();
        entity.render(batch, camera);
    }

    public void renderEntities(Entity[] entities, Camera camera) {
        if (entities == null) return;
        if (camera == null) throw new IllegalArgumentException(
            "camera cannot be null"
        );

        Arrays.sort(
            entities,
            new Comparator<Entity>() {
                // Needed to not allocate a new vector for every calculation
                Vector3 tmp = new Vector3();

                float getZ(Entity entity) {
                    tmp.set(entity.getPosition());
                    camera.project(tmp);
                    return tmp.z;
                }

                @Override
                public int compare(Entity a, Entity b) {
                    if (a == null && b == null) return 0;
                    if (a == null) return 1;
                    if (b == null) return -1;

                    return Float.compare(getZ(b), getZ(a));
                }
            }
        );

        beginRender();
        for (Entity e : entities) {
            if (e != null) {
                e.render(batch, camera);
            }
        }
    }

    public void renderText(
        BitmapFont font,
        CharSequence str,
        float x,
        float y
    ) {
        beginRender();
        font.draw(batch, str, x, y);
    }

    public void renderText(
        BitmapFont font,
        CharSequence str,
        float x,
        float y,
        float targetWidth,
        int halign,
        boolean wrap
    ) {
        beginRender();
        font.draw(batch, str, x, y, targetWidth, halign, wrap);
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
