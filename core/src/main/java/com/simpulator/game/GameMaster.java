package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.levels.LevelId;
import com.simpulator.game.levels.LevelManager;
import com.simpulator.game.scenes.MainMenu;
import com.simpulator.game.scenes.ResultScene;
import com.simpulator.game.scenes.ResultScene.ResultType;
import com.simpulator.game.scenes.SoundMenu;

public class GameMaster extends ApplicationAdapter {

    private SceneManager sceneManager;
    private GraphicsManager graphics;
    private MusicManager musics;
    private int windowWidth;
    private int windowHeight;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        graphics = new GraphicsManager();
        musics = new MusicManager();
        windowWidth = Gdx.graphics.getWidth();
        windowHeight = Gdx.graphics.getHeight();

        LevelManager levelManager = new LevelManager();
        levelManager.setCurrentLevelId(LevelId.TUTORIAL);

        sceneManager.addScene(Scenes.MainMenu, () ->
            new MainMenu(sceneManager, graphics, levelManager)
        );
        sceneManager.addScene(Scenes.SoundMenu, () ->
            new SoundMenu(sceneManager, graphics, musics)
        );
        sceneManager.addScene(Scenes.Explore, () ->
            levelManager
                .getCurrentLevel()
                .createScene(sceneManager, graphics, levelManager, musics)
        );
        sceneManager.addScene(Scenes.Win, () ->
            new ResultScene(sceneManager, graphics, ResultType.WIN)
        );

        sceneManager.addScene(Scenes.Lose, () ->
            new ResultScene(sceneManager, graphics, ResultType.LOSE)
        );

        sceneManager.setScene(Scenes.MainMenu);
        musics.startMusic("GameAudio.ogg");
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
