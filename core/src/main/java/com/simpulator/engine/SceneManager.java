package com.simpulator.engine;

import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

/** Manages loading and unloading of scenes. */
public class SceneManager implements Disposable {

    private Scene currentScene;
    private HashMap<String, Scene> scenes;
    private GraphicsManager graphics;

    /** Create an empty SceneManager. */
    public SceneManager() {
        this.scenes = new HashMap<>();
        this.graphics = new GraphicsManager();
    }

    /** Add a new scene that can be reference by its name. */
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

    /** Unload the current scene, if any, and load and return the given scene. */
    public void switchScene(String name) {
        if (!scenes.containsKey(name)) {
            throw new IllegalArgumentException(
                "Scene with name " + name + " does not exist."
            );
        }

        if (currentScene != null) {
            currentScene.unload();
        }
        currentScene = scenes.get(name);
        currentScene.load();
    }

    /** Update the current scene by the given delta time in seconds. */
    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }

    /** Render the current scene to the screen. */
    public void render() {
        if (currentScene != null) {
            currentScene.render(graphics);
        }
    }

    @Override
    public void dispose() {
        if (currentScene != null) {
            currentScene.unload();
        }
        graphics.dispose();
    }
}
