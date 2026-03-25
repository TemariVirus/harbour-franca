package com.simpulator.game.scenes;

import com.badlogic.gdx.Input.Keys;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.levels.Level;
import com.simpulator.game.levels.LevelManager;

public class TutorialScene extends ExploreScene {

    private enum TutorialState {
        MOVEMENT,
        TRADING,
    }

    private TutorialState tutorialState = TutorialState.MOVEMENT;

    public TutorialScene(
        SceneManager sceneManager,
        LevelManager levelManager,
        MusicManager musics,
        Level level
    ) {
        super(sceneManager, levelManager, musics, level);
        keyboard.bind(ButtonBindType.DOWN, Keys.W, onMoveAction());
        keyboard.bind(ButtonBindType.DOWN, Keys.A, onMoveAction());
        keyboard.bind(ButtonBindType.DOWN, Keys.S, onMoveAction());
        keyboard.bind(ButtonBindType.DOWN, Keys.D, onMoveAction());
    }

    private <T> Action<T> onMoveAction() {
        return e -> {
            if (tutorialState == TutorialState.MOVEMENT) {
                tutorialState = TutorialState.TRADING;
            }
        };
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!isTradingUIOpen()) {
            handleTutorialHints();
        }
    }

    private void handleTutorialHints() {
        if (merchantTargeter.getLastTarget() != null) {
            return;
        }

        hud.setPromptVisible(true);
        switch (tutorialState) {
            case MOVEMENT:
                hud.setPrompt(
                    "Welcome to the Harbour Franca tutorial!\nUse [W, A, S, D] and mouse to move around the harbour."
                );
                break;
            case TRADING:
                hud.setPrompt("Reach the level goal by trading!");
                break;
        }
    }
}
