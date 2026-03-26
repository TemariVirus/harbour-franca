package com.simpulator.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.Config;
import com.simpulator.game.Scenes;
import com.simpulator.game.ui.Slider;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UIRelativeLayout;
import com.simpulator.game.ui.UIRelativeLayout.Alignment;
import com.simpulator.game.ui.UiHelper;

public class SoundMenu implements Scene {

    private static final Color BACKGROUND_COLOR = new Color(
        0.2f,
        0.1f,
        0.1f,
        1f
    );
    private static final Color TEXT_COLOR = Color.YELLOW;

    final float TOP_MARGIN = 30;
    final float LEFT_MARGIN = 70;
    final float TITLE_SIZE = 24;
    final float FONT_SIZE = 18;

    private MusicManager musics;
    private final KeyboardManager km = new KeyboardManager();
    private final MouseManager mm = new MouseManager();

    private final Viewport viewport = new FitViewport(640, 480);
    private final UIRoot uiRoot = new UIRoot();
    private final BitmapFont font;

    private Text volumeText;
    private Slider volumeSlider;

    public SoundMenu(SceneManager sceneManager, MusicManager musicManager) {
        this.musics = musicManager;

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

        font = new BitmapFont(Gdx.files.internal("fonts/en.fnt"));
        buildUI(sceneManager);
    }

    private void buildUI(SceneManager sceneManager) {
        UiHelper.setupUiMouseHandlers(mm, viewport, uiRoot);

        uiRoot.addChild(
            new Text(
                "SOUND SETTINGS",
                font,
                Text.Alignment.START,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .padTop(TOP_MARGIN)
                    .padLeft(LEFT_MARGIN)
                    .height(TITLE_SIZE)
                    .getLayout()
            )
        );

        volumeText = new Text(
            formatVolumeText(Config.volume),
            font,
            Text.Alignment.START,
            TEXT_COLOR,
            new UIRelativeLayout.Builder()
                .padTop(TOP_MARGIN + 100)
                .padLeft(LEFT_MARGIN)
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
                    .padTop(TOP_MARGIN + 150)
                    .padLeft(LEFT_MARGIN)
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
                .padTop(TOP_MARGIN + 195)
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
                    .padTop(TOP_MARGIN + 250)
                    .padLeft(LEFT_MARGIN)
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
    public boolean onFocus() {
        Gdx.input.setCursorCatched(false);
        return true;
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
        mm.update(deltaTime, Float.NaN);
        uiRoot.update(deltaTime);
    }

    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        ScreenUtils.clear(BACKGROUND_COLOR);
        viewport.update(width, height, true);
        viewport.setScreenPosition(
            viewport.getScreenX() + x,
            viewport.getScreenY() + y
        );
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }
}
