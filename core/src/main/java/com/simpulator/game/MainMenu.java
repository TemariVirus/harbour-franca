package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public class MainMenu extends Scene {

    private SceneManager sceneManager;
    private SpriteBatch batch;
    private BitmapFont font;

    public MainMenu(SceneManager sceneManager) {
        super();
        this.sceneManager = sceneManager;
    }

    @Override
    public void load() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
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

        // TODO: use KeyboaordManager
        if (
            Gdx.input.isKeyJustPressed(Keys.ENTER) ||
            Gdx.input.isKeyJustPressed(Keys.G)
        ) {
            sceneManager.switchScene("MainGame");
        }
        if (Gdx.input.isKeyJustPressed(Keys.S)) {
            sceneManager.switchScene("SoundMenu");
        }
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);

        // TODO: integrate with GraphicsManager?
        batch.begin();
        font.draw(batch, "MAIN MENU", 100, 400);
        font.draw(batch, "Press [ENTER] to Start Game", 100, 300);
        font.draw(batch, "Press [S] for Sound Options", 100, 250);
        batch.end();
    }
}
