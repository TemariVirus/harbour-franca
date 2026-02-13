package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.simpulator.engine.SceneManager;
import com.simpulator.engine.SoundManager;

public class GameMaster extends ApplicationAdapter {
    
    private SceneManager sceneManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();

        // Load the music file 
        SoundManager.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("GameAudio.mp3"));
        
        SoundManager.backgroundMusic.setLooping(true);
        SoundManager.backgroundMusic.setVolume(SoundManager.getGlobalVolume());
        SoundManager.backgroundMusic.play();



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
        
        // Dispose of the music when the game closes
        if (SoundManager.backgroundMusic != null) {
            SoundManager.backgroundMusic.dispose();
        }
    }
}