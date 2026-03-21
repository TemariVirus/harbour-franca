package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;

public class SoundMenu extends Scene {

    private MusicManager musics;
    private KeyboardManager km;
    private BitmapFont font;
    private Stage stage;
    private Slider volumeSlider;
    private float renderDeltaTime = 0;

    public SoundMenu(SceneManager sceneManager, MusicManager musicManager) {
        super(new FitViewport(640, 480));
        this.musics = musicManager;

        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        stage = new Stage(viewport);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        this.volumeSlider = new Slider(0, 100, 10, false, SimpleSkin.getSkin());
        volumeSlider.setValue(Config.volume);
        volumeSlider.addListener(
            new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Config.volume = Math.round(volumeSlider.getValue());
                    sounds.setVolume(Config.volume * 0.01f);
                    musics.setVolume(Config.volume * 0.01f);
                }
            }
        );
        table.add(volumeSlider).width(300).height(50).padTop(5);

        this.km = new KeyboardManager();
        km.bind(
            ButtonBindType.DOWN,
            Keys.ESCAPE,
            ActionHelper.switchSceneAction(sceneManager, Scenes.MainMenu)
        );
        km.bind(
            ButtonBindType.DOWN,
            Keys.BACKSPACE,
            ActionHelper.switchSceneAction(sceneManager, Scenes.MainMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.LEFT, changeVolumeAction(-10));
        km.bind(ButtonBindType.DOWN, Keys.RIGHT, changeVolumeAction(10));
    }

    private <T> Action<T> changeVolumeAction(int amount) {
        return (T data) -> {
            Config.volume = MathUtils.clamp(Config.volume + amount, 0, 100);

            float volume = Config.volume * 0.01f;
            sounds.setVolume(volume);
            musics.setVolume(volume);
            if (volumeSlider != null) {
                volumeSlider.setValue(Config.volume);
            }
        };
    }

    @Override
    public InputProcessor getInputProcessor() {
        Gdx.input.setCursorCatched(false);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(km);
        return multiplexer;
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        stage.dispose();
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
        renderDeltaTime += deltaTime;
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        ScreenUtils.clear(0.2f, 0.1f, 0.1f, 1f); // Dark Red background
        viewport.update(width, height, true);

        graphics.beginRender(viewport);
        graphics.render(new UIText(font, "SOUND SETTINGS", 100, 450), null);
        graphics.render(
            new UIText(font, "Volume: " + Config.volume + "%", 100, 350),
            null
        );
        graphics.render(
            new UIText(font, "[LEFT] / [RIGHT] or [MOUSE] to adjust", 100, 300),
            null
        );
        graphics.render(
            new UIText(font, "Press [ESCAPE] to Return", 100, 200),
            null
        );
        graphics.endRender();

        stage.act(renderDeltaTime);
        stage.draw();

        renderDeltaTime = 0;
    }
}
