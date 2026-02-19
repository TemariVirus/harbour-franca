package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
    private Stage stage;
    private Slider volumeSlider;

    public SoundMenu(SceneManager sceneManager, MusicManager musicManager) {
        super(sceneManager);
        musics = musicManager;
    }

    // TODO: add separate volume for sounds and maintain it across scenes
    private <T> Action<T> changeVolumeAction(float amount) {
        return new Action<T>() {
            public void act(T data) {
                float current = sounds.getVolume();
                float target = current + amount;

                // TODO: store volume as an int
                // fix float pt error
                target = Math.round(target * 10) / 10.0f;

                sounds.setVolume(target);
                musics.setVolume(target);

                if (volumeSlider != null) {
                    volumeSlider.setValue(target); 
                }
            }
        };
    }

    @Override
    public void load() {
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(2);

        km = new KeyboardManager();
        km.bind(
            ButtonBindType.DOWN,
            Keys.ESCAPE,
            switchSceneAction(Scenes.MainMenu)
        );
        km.bind(
            ButtonBindType.DOWN,
            Keys.BACKSPACE,
            switchSceneAction(Scenes.MainMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.LEFT, changeVolumeAction(-0.1f));
        km.bind(ButtonBindType.DOWN, Keys.RIGHT, changeVolumeAction(0.1f));

        stage = new Stage();
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        volumeSlider = new Slider(0f, 1f, 0.1f, false, SimpleSkin.getSkin());
        volumeSlider.setValue(sounds.getVolume());
        
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float target = volumeSlider.getValue();
                target = Math.round(target * 10) / 10.0f;
                sounds.setVolume(target);
                musics.setVolume(target);
            }
        });

        table.add(volumeSlider).width(300).height(50).padTop(50);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); 
        multiplexer.addProcessor(km);   
        
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void unload() {
        super.unload();
        font.dispose();
        
        if (stage != null) {
            stage.dispose(); 
        }
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.2f, 0.1f, 0.1f, 1f); // Dark Red background

        graphics.render(new UIText(font, "SOUND SETTINGS", 100, 450), null);

        // fix it from going to 79
        int percent = MathUtils.round(sounds.getVolume() * 100);

        graphics.render(
            new UIText(font, "Volume: " + percent + "%", 100, 350),
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

        if (stage != null) {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
    }
}
