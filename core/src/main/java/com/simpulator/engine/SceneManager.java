package com.simpulator.engine;

import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

public class SceneManager implements Disposable {

    private Scene currentScene;
    private HashMap<String, Scene> scenes;
    private GraphicsManager graphics;

    public SceneManager() {
        this.scenes = new HashMap<>();
        this.graphics = new GraphicsManager();
    }

    public void addScene(String name, Scene scene)
        throws IllegalArgumentException {
        if (scenes.containsKey(name)) {
            throw new IllegalArgumentException(
                "Scene with name " + name + " already exists."
            );
        }
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null.");
        }
        scenes.put(name, scene);
    }

    public void switchScene(String name) {
        if (currentScene != null) {
            currentScene.unload();
        }
        currentScene = scenes.get(name);
        currentScene.load();
    }

    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }

    public long playSound(String path) {
        if (currentScene != null) {
            return currentScene.playSound(path);
        }
        return -1;
    }

    public void render() {
        if (currentScene != null) {
            currentScene.render(graphics);
        }
    }

    public void dispose() {
        if (currentScene != null) {
            currentScene.unload();
        }
        graphics.dispose();
    }
}
