package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public class SoundMenu extends Scene {

    private SceneManager sceneManager;
    private SpriteBatch batch;
    private BitmapFont font;

    public SoundMenu(SceneManager sceneManager) {
        super();
        this.sceneManager = sceneManager;
    }

    @Override
    public void load() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        sounds.setBgm("GameAudio.mp3");
        sounds.startBgm();
    }

    @Override
    public void unload() {
        super.unload();
        batch.dispose();
        font.dispose();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // TODO: use KeyboardManager
        if (
            Gdx.input.isKeyJustPressed(Keys.ESCAPE) ||
            Gdx.input.isKeyJustPressed(Keys.BACKSPACE)
        ) {
            sceneManager.switchScene("MainMenu");
        }

        if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
            changeVolume(-0.1f);
        }
        if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
            changeVolume(0.1f);
        }
    }

    private void changeVolume(float amount) {
        float current = sounds.getVolume();
        float target = current + amount;

        // TODO: store volume as an int
        // fix float pt error
        target = Math.round(target * 10) / 10.0f;

        sounds.setVolume(target);
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.2f, 0.1f, 0.1f, 1f); // Dark Red background

        batch.begin();
        font.draw(batch, "SOUND SETTINGS", 100, 450);

        // fix it from going to 79
        int percent = MathUtils.round(sounds.getVolume() * 100);

        font.draw(batch, "Volume: " + percent + "%", 100, 350);
        font.draw(batch, "[LEFT] / [RIGHT] to adjust", 100, 300);

        font.draw(batch, "Press [ESCAPE] to Return", 100, 200);
        batch.end();
    }
}
