package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.ExploreScene.ExploreScene;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;
    private MusicManager musics;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        musics = new MusicManager();

        MainMenu mainMenu = new MainMenu(sceneManager);
        SoundMenu soundMenu = new SoundMenu(sceneManager, musics);
        ExploreScene mainGame = new ExploreScene(sceneManager);

        sceneManager.addScene(Scenes.MainMenu, mainMenu);
        sceneManager.addScene(Scenes.SoundMenu, soundMenu);
        sceneManager.addScene(Scenes.Explore, mainGame);

        sceneManager.switchScene(Scenes.MainMenu);
        musics.startMusic("GameAudio.mp3");
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
        musics.dispose();
    }
}
