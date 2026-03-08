package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public class MainMenu extends Scene {

    private SceneManager sceneManager;
    private KeyboardManager km;
    private BitmapFont font;

    public MainMenu(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void load() {
        sounds.setVolume(Config.volume * 0.01f);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);

        km = new KeyboardManager();
        km.bind(
            ButtonBindType.DOWN,
            Keys.ENTER,
            ActionHelper.switchSceneAction(sceneManager, Scenes.Explore)
        );
        km.bind(
            ButtonBindType.DOWN,
            Keys.G,
            ActionHelper.switchSceneAction(sceneManager, Scenes.Explore)
        );
        km.bind(
            ButtonBindType.DOWN,
            Keys.S,
            ActionHelper.switchSceneAction(sceneManager, Scenes.SoundMenu)
        );

        Gdx.input.setInputProcessor(km);
    }

    @Override
    public void unload() {
        super.unload();
        font.dispose();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);

        graphics.render(new UIText(font, "MAIN MENU", 100, 400), null);
        graphics.render(
            new UIText(font, "Press [ENTER] to Start Game", 100, 300),
            null
        );
        graphics.render(
            new UIText(font, "Press [S] for Sound Options", 100, 250),
            null
        );
        graphics.endRender();
    }
}
