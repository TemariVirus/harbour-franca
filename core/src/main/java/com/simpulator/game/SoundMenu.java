package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.ui.Slider;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UiHelper;

// TODO: beautify this scene
public class SoundMenu extends Scene {

    private static final Color BACKGROUND_COLOR = new Color(
        0.2f,
        0.1f,
        0.1f,
        1f
    );
    private static final Color TEXT_COLOR = Color.YELLOW;

    private MusicManager musics;
    private KeyboardManager km;
    private MouseManager mm;

    private BitmapFont font;
    private UIRoot uiRoot;
    private Text volumeText;
    private Slider volumeSlider;

    public SoundMenu(SceneManager sceneManager, MusicManager musicManager) {
        super(new FitViewport(640, 480));
        this.musics = musicManager;

        this.km = new KeyboardManager();
        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, e ->
            sceneManager.setScene(Scenes.MainMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.BACKSPACE, e ->
            sceneManager.setScene(Scenes.MainMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.LEFT, e ->
            updateVolume(Config.volume - 10)
        );
        km.bind(ButtonBindType.DOWN, Keys.RIGHT, e ->
            updateVolume(Config.volume + 10)
        );

        this.mm = new MouseManager();

        buildUI(sceneManager);
    }

    private void buildUI(SceneManager sceneManager) {
        final float FONT_SIZE = 22;

        font = new BitmapFont();
        uiRoot = new UIRoot();
        UiHelper.setupUiMouseHandlers(mm, viewport, uiRoot);

        uiRoot.addChild(
            new Text(
                "SOUND SETTINGS",
                font,
                Text.Alignment.START,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .padTop(30)
                    .padLeft(100)
                    .height(FONT_SIZE)
                    .getLayout()
            )
        );

        volumeText = new Text(
            formatVolumeText(Config.volume),
            font,
            Text.Alignment.START,
            TEXT_COLOR,
            new UIRelativeLayout.Builder()
                .padTop(130)
                .padLeft(100)
                .height(FONT_SIZE)
                .getLayout()
        );
        uiRoot.addChild(volumeText);
        uiRoot.addChild(
            new Text(
                "[LEFT] / [RIGHT] or [MOUSE] to adjust",
                font,
                Text.Alignment.START,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .padTop(180)
                    .padLeft(100)
                    .height(FONT_SIZE)
                    .getLayout()
            )
        );

        volumeSlider = new Slider(
            0,
            100,
            10,
            Color.DARK_GRAY,
            10,
            Color.RED,
            20,
            new UIRelativeLayout.Builder()
                .xAlignment(Alignment.CENTER)
                .padTop(225)
                .width(300)
                .height(40)
                .getLayout()
        );
        volumeSlider.addValueListener(value -> updateVolume(value));
        volumeSlider.setValue(Config.volume, true);
        uiRoot.addChild(volumeSlider);

        uiRoot.addChild(
            new Text(
                "Press [ESCAPE] to Return",
                font,
                Text.Alignment.START,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .padTop(280)
                    .padLeft(100)
                    .height(FONT_SIZE)
                    .getLayout()
            )
        );
    }

    private static String formatVolumeText(int volume) {
        return "Volume: " + volume + "%";
    }

    private void updateVolume(float newVolume) {
        volumeSlider.setValue(newVolume, false);
        Config.volume = Math.round(volumeSlider.getValue());
        volumeText.setText(formatVolumeText(Config.volume));
        float volume = Config.volume * 0.01f;
        sounds.setVolume(volume);
        musics.setVolume(volume);
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(km);
        multiplexer.addProcessor(mm);
        return multiplexer;
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
        mm.update(deltaTime, Float.NaN);
        uiRoot.update(deltaTime);
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        ScreenUtils.clear(BACKGROUND_COLOR);
        viewport.update(width, height, true);
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }
}
