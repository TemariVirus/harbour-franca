package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import java.util.Arrays;
import java.util.Comparator;

/** Manages rendering of entities and text. */
public class GraphicsManager implements Disposable {

    protected SpriteBatch batch = new SpriteBatch();
    private boolean isRendering = false;

    protected void beginRender() {
        if (isRendering) return;

        batch.begin();
        isRendering = true;
    }

    /** Render a single object from the camera's perspective, on top of everything so far. */
    public void render(Renderable renderable, Camera camera) {
        if (renderable == null) return;

        beginRender();
        if (renderable.isVisible(camera)) {
            renderable.render(batch, camera);
        }
    }

    /** Render multiple entities from the camera's perspective. The entities are rendered back to front. */
    public void render(Renderable[] renderables, Camera camera) {
        if (renderables == null) return;

        Arrays.sort(
            renderables,
            new Comparator<Renderable>() {
                @Override
                public int compare(Renderable a, Renderable b) {
                    if (a == null && b == null) return 0;
                    if (a == null) return 1;
                    if (b == null) return -1;

                    return Float.compare(
                        b.getZOrder(camera),
                        a.getZOrder(camera)
                    );
                }
            }
        );

        beginRender();
        for (Renderable r : renderables) {
            if (r != null && r.isVisible(camera)) {
                r.render(batch, camera);
            }
        }
    }

    /** Finish rendering and draw the result to the screen. */
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
