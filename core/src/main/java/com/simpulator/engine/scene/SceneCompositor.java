package com.simpulator.engine.scene;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UILayout;
import java.util.ArrayList;
import java.util.List;

public class SceneCompositor implements Scene {

    private static class StackNode {

        public final Scene scene;
        public final UILayout renderArea;

        public StackNode(Scene scene, UILayout renderArea) {
            this.scene = scene;
            this.renderArea = renderArea;
        }
    }

    private final List<StackNode> scenes = new ArrayList<>();

    public void push(Scene scene, UILayout renderArea) {
        scenes.add(new StackNode(scene, renderArea));
    }

    public Scene pop() {
        return scenes.removeLast().scene;
    }

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

    @Override
    public void update(float deltaTime) {
        for (StackNode node : scenes) {
            node.scene.update(deltaTime);
        }
    }

    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        Rect bounds = new Rect(x, y, x + width, y + height);
        for (StackNode node : scenes) {
            Rect area = node.renderArea.computeBounds(bounds);
            node.scene.render(
                graphics,
                area.left,
                area.top,
                area.right - area.left,
                area.bottom - area.top
            );
        }
    }

    @Override
    public void dispose() {
        for (StackNode node : scenes) {
            node.scene.dispose();
        }
    }
}
