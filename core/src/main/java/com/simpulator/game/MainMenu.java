package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;

public class MainMenu extends Scene {

    private BitmapFont font;
    private KeyboardManager km;

    public MainMenu(SceneManager sceneManager) {
        super(new FitViewport(640, 480));
        sounds.setVolume(Config.volume * 0.01f);

        this.km = new KeyboardManager();
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

        this.font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return km;
    }

    @Override
    public void onLoad() {
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);
        viewport.update(width, height, true);

        graphics.beginRender(viewport);
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
