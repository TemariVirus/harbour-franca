package com.simpulator.game;

import com.simpulator.engine.input.Action;
import com.simpulator.engine.scene.SceneManager;

public final class ActionHelper {

    public static <T> Action<T> switchSceneAction(
        SceneManager sceneManager,
        String sceneName
    ) {
        return new Action<T>() {
            public void act(T data) {
                sceneManager.switchScene(sceneName);
            }
        };
    }
}
