package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.ExploreScene.ExploreScene;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;
    private MusicManager musics;
    private int windowWidth;
    private int windowHeight;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        musics = new MusicManager();
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        LevelManager levelManager = new LevelManager();
        levelManager.setCurrentLevelId("level_01");

        sceneManager.addScene(Scenes.MainMenu, () ->
            new MainMenu(sceneManager)
        );
        sceneManager.addScene(Scenes.SoundMenu, () ->
            new SoundMenu(sceneManager, musics)
        );
        sceneManager.addScene(Scenes.Explore, () ->
            new ExploreScene(sceneManager, levelManager.getCurrentLevel())
        );
        sceneManager.addScene(Scenes.Victory, () ->
            new VictoryScene(sceneManager)
        );

        sceneManager.setScene(Scenes.MainMenu);
        musics.startMusic("GameAudio.mp3");
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        sceneManager.update(deltaTime);
        sceneManager.render(windowWidth, windowHeight);
    }

    @Override
    public void resize(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        musics.dispose();
    }
}
