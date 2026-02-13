package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.Action;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.MusicManager;
import com.simpulator.engine.SceneManager;

public class SoundMenu extends SwitchableScene {

    private MusicManager musics;
    private KeyboardManager km;
    private BitmapFont font;

    public SoundMenu(SceneManager sceneManager, MusicManager musicManager) {
        super(sceneManager);
        musics = musicManager;
    }

    private <T> Action<T> changeVolumeAction(float amount) {
        return new Action<T>() {
            public void act(float deltaTime, T extraData) {
                float current = sounds.getVolume();
                float target = current + amount;

                // TODO: store volume as an int
                // fix float pt error
                target = Math.round(target * 10) / 10.0f;

                sounds.setVolume(target);
                musics.setVolume(target);
            }
        };
    }

    @Override
    public void load() {
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        km = new KeyboardManager();
        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, switchSceneAction(Scenes.MainMenu));
        km.bind(ButtonBindType.DOWN, Keys.BACKSPACE, switchSceneAction(Scenes.MainMenu));
        km.bind(ButtonBindType.DOWN, Keys.LEFT, changeVolumeAction(-0.1f));
        km.bind(ButtonBindType.DOWN, Keys.RIGHT, changeVolumeAction(0.1f));

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
        super.update(deltaTime);
        km.update(deltaTime, Float.NaN);
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.2f, 0.1f, 0.1f, 1f); // Dark Red background

        graphics.renderText(font, "SOUND SETTINGS", 100, 450);

        // fix it from going to 79
        int percent = MathUtils.round(sounds.getVolume() * 100);

        graphics.renderText(font, "Volume: " + percent + "%", 100, 350);
        graphics.renderText(font, "[LEFT] / [RIGHT] to adjust", 100, 300);

        graphics.renderText(font, "Press [ESCAPE] to Return", 100, 200);
        graphics.endRender();
    }
}
