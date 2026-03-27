package com.simpulator.engine.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Arrays;
import java.util.Comparator;

/** Manages rendering of entities and text. */
public class GraphicsManager implements Disposable {

    protected TextureBatch batch = new TextureBatch();
    private Viewport viewport = null;

    private void setCamera(Camera camera) {
        if (camera == null) {
            batch.setProjectionMatrix(this.viewport.getCamera().combined);
        } else {
            batch.setProjectionMatrix(camera.combined);
        }
    }

    /** Begin rendering in the given viewport */
    public void beginRender(Viewport viewport) {
        if (isRendering()) {
            endRender();
        }

        batch.begin();
        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.viewport = viewport;
        this.viewport.apply();
    }

    /** Render a single object from the camera's perspective, on top of everything so far. */
    public void render(Renderable renderable, Camera camera) {
        if (!isRendering()) {
            throw new IllegalStateException(
                "Cannot call render() while not rendering. Call beginRender() first."
            );
        }
        if (renderable == null) return;

        setCamera(camera);
        if (renderable.isVisible(camera)) {
            renderable.render(batch, camera);
        }
    }

    /** Render multiple entities from the camera's perspective. The entities are rendered back to front. */
    public void render(Renderable[] renderables, Camera camera) {
        if (!isRendering()) {
            throw new IllegalStateException(
                "Cannot call render() while not rendering. Call beginRender() first."
            );
        }
        if (renderables == null) return;

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
     * Renders a 3D entity, ignoring its z order.
     * A depth buffer is used to render it correctly.
     * It should use the TextureBatch.draw3D methods to render itself.
     */
    public void render3D(Renderable renderable, Camera camera) {
        if (!isRendering()) {
            throw new IllegalStateException(
                "Cannot call render3D() while not rendering. Call beginRender() first."
            );
        }
        if (renderable == null) return;

        setCamera(camera);
        if (renderable.isVisible(camera)) {
            renderable.render(batch, camera);
        }
    }

    /**
     * Renders multiple 3D entities, ignoring their z order.
     * A depth buffer is used to render them correctly.
     * They should use the TextureBatch.draw3D methods to render themselves.
     */
    public void render3D(Renderable[] renderables, Camera camera) {
        if (!isRendering()) {
            throw new IllegalStateException(
                "Cannot call render3D() while not rendering. Call beginRender() first."
            );
        }
        if (renderables == null) return;

        setCamera(camera);
        for (Renderable r : renderables) {
            if (r != null && r.isVisible(camera)) {
                r.render(batch, camera);
            }
        }
    }

    /** Finish rendering and draw the result to the screen. */
    public void endRender() {
        if (!isRendering()) return;

        batch.end();
        viewport = null;
    }

    /** Returns whether a render pass is currently occuring. */
    public boolean isRendering() {
        return this.viewport != null;
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
