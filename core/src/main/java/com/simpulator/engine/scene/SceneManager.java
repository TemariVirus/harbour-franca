package com.simpulator.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

/** Manages loading and unloading of scenes. */
public class SceneManager implements Disposable {

    private Scene currentScene = null;
    private HashMap<String, SceneFactory> scenes;

    /** Create an empty SceneManager. */
    public SceneManager() {
        this.scenes = new HashMap<>();
    }

    /** Add a new scene that can be reference by its name. */
    public void addScene(String name, SceneFactory factory)
        throws IllegalArgumentException {
        if (scenes.containsKey(name)) {
            throw new IllegalArgumentException(
                "Factory with name " + name + " already exists."
            );
        }
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null.");
        }
        scenes.put(name, factory);
    }

    /** Unload the current scene, if any, and load the given scene. */
    public void setScene(String name) {
        if (!scenes.containsKey(name)) {
            throw new IllegalArgumentException(
                "Scene with name " + name + " does not exist."
            );
        }

        if (currentScene != null) {
            currentScene.dispose();
        }
        currentScene = scenes.get(name).create();
        currentScene.onFocus();
        Gdx.input.setInputProcessor(currentScene.getInputProcessor());
    }

    /** Update the current scene by the given delta time in seconds. */
    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }

    /** Render the current scene to the screen. */
    public void render(int width, int height) {
        if (currentScene != null) {
            currentScene.render(0, 0, width, height);
        }
    }

    @Override
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
    }
}
