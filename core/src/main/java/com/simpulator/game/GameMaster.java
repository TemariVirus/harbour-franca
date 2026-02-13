package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.simpulator.engine.SceneManager;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();

        MainMenu mainMenu = new MainMenu(sceneManager);
        SoundMenu soundMenu = new SoundMenu(sceneManager);
        MainGame mainGame = new MainGame(sceneManager);

        sceneManager.addScene("MainMenu", mainMenu);
        sceneManager.addScene("SoundMenu", soundMenu);
        sceneManager.addScene("MainGame", mainGame);

        // start scene
        sceneManager.switchScene("MainMenu");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        sceneManager.update(deltaTime);
        sceneManager.render();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
    }
}
