package com.simpulator.engine;

import java.util.HashMap;


public class SceneManager {
    private Scene currentScene;
    private HashMap<String, Scene> scenes;
    private GraphicsManager graphics;

    public SceneManager() {
        this.scenes = new HashMap<>();
        this.graphics = new GraphicsManager();
    }

    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    public void switchScene(String name) {
        if (currentScene != null) {
            currentScene.unload();
        }
        currentScene = scenes.get(name);
        if (currentScene != null) {
            currentScene.load();
        }
    }

    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
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