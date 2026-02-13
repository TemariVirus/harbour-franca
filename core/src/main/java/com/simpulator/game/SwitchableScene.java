package com.simpulator.game;

import com.simpulator.engine.Action;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public abstract class SwitchableScene extends Scene {

    protected SceneManager sceneManager;

    public SwitchableScene(SceneManager sceneManager) {
        super();
        this.sceneManager = sceneManager;
    }

    protected <T> Action<T> switchSceneAction(String sceneName) {
        return new Action<T>() {
            public void act(float deltaTime, T extraData) {
                sceneManager.switchScene(sceneName);
            }
        };
    }
}
