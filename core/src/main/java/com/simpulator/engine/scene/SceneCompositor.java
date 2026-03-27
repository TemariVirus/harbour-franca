package com.simpulator.engine.scene;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UILayout;
import java.util.ArrayList;
import java.util.List;

/** Stacks multiple scenes that handle input and render together. */
public class SceneCompositor implements Scene {

    private static final class StackNode {

        public final Scene scene;
        public final UILayout layout;

        public StackNode(Scene scene, UILayout layout) {
            this.scene = scene;
            this.layout = layout;
        }
    }

    private final List<StackNode> scenes = new ArrayList<>();

    /**
     * Adds a scene to be rendered on top of all previous scenes,
     * with the position and size determined by the given layout.
     *
     * The caller is responsible for setting the new input processor and calling onFocus() if needed.
     */
    public void push(Scene scene, UILayout layout) {
        scenes.add(new StackNode(scene, layout));
    }

    /**
     * Removes the top scene from the stack and returns it.
     * The scene is not disposed, so the caller can reuse it if desired.
     *
     * The caller is responsible for setting the new input processor and calling onFocus() if needed.
     */
    public Scene pop() {
        return scenes.removeLast().scene;
    }

    /**
     * Returns an InputProcessor that combines the input processors of all scenes in the stack.
     * Scenes higher in the stack handle input first.
     */
    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer mux = new InputMultiplexer();
        for (StackNode node : scenes.reversed()) {
            InputProcessor ip = node.scene.getInputProcessor();
            if (ip != null) {
                mux.addProcessor(ip);
            }
        }
        return mux;
    }

    /**
     * Calls onFocus() on each scene in the stack, starting from the top, until one of them accepts focus.
     * Returns true if a scene accepted focus, false otherwise.
     */
    @Override
    public boolean onFocus() {
        // Go down the stack until a scene gets focus
        for (int i = scenes.size() - 1; i >= 0; i--) {
            StackNode n = scenes.get(i);
            if (n.scene.onFocus()) {
                return true;
            }
        }
        return false;
    }

    /** Updates all scenes in the stack. */
    @Override
    public void update(float deltaTime) {
        for (StackNode node : scenes) {
            node.scene.update(deltaTime);
        }
    }

    /** Renders all scenes in the stack. */
    @Override
    public void render(int x, int y, int width, int height) {
        Rect bounds = new Rect(x, y, x + width, y + height);
        for (StackNode node : scenes) {
            Rect area = node.layout.computeBounds(bounds);
            node.scene.render(
                area.left,
                area.top,
                area.right - area.left,
                area.bottom - area.top
            );
        }
    }

    /** Disposes all scenes in the stack. */
    @Override
    public void dispose() {
        for (StackNode node : scenes) {
            node.scene.dispose();
        }
    }
}
