package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;
import java.util.Arrays;
import java.util.Comparator;

/** Manages rendering of entities and text. */
public class GraphicsManager implements Disposable {

    protected TextureBatch batch = new TextureBatch();
    private boolean isRendering = false;

    protected void setCamera(Camera camera) {
        if (camera == null) {
            batch.setProjectionMatrix(
                new Matrix4().setToOrtho2D(
                    0,
                    0,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight()
                )
            );
        } else {
            batch.setProjectionMatrix(camera.combined);
        }
    }

    protected void beginRender() {
        if (isRendering) return;

        batch.begin();
        isRendering = true;
    }

    /** Render a single object from the camera's perspective, on top of everything so far. */
    public void render(Renderable renderable, Camera camera) {
        if (renderable == null) return;
        beginRender();

        setCamera(camera);
        if (renderable.isVisible(camera)) {
            renderable.render(batch, camera);
        }
    }

    /** Render multiple entities from the camera's perspective. The entities are rendered back to front. */
    public void render(Renderable[] renderables, Camera camera) {
        if (renderables == null) return;
        beginRender();

        setCamera(camera);

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

        for (Renderable r : renderables) {
            if (r != null && r.isVisible(camera)) {
                r.render(batch, camera);
            }
        }
    }

    /**
     * Renders multiple 3D entities, ignoring their z order.
     * A depth buffer is used to render them correctly.
     * They should use the TextureBatch.draw3D methods to render themselves.
     */
    public void render3D(Renderable[] renderables, Camera camera) {
        if (renderables == null) return;
        beginRender();

        setCamera(camera);
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

    public boolean isRendering() {
        return isRendering;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
