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
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Slider;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UIRelativeLayout;
import com.simpulator.game.ui.UIRelativeLayout.Alignment;
import com.simpulator.game.ui.UiHelper;

public class SoundMenu implements Scene {

    private static final Color BACKGROUND_COLOR = new Color(0.08f, 0.11f, 0.16f, 1f);
    private static final Color TITLE_COLOR = new Color(0.83f, 0.68f, 0.21f, 1f);
    private static final Color TEXT_COLOR = new Color(0.96f, 0.87f, 0.70f, 1f);
    private static final Color HINT_COLOR = new Color(0.82f, 0.78f, 0.70f, 1f);

    private static final Color PANEL_BORDER_COLOR = TITLE_COLOR;
    private static final Color PANEL_FILL_COLOR = new Color(0.10f, 0.15f, 0.23f, 0.94f);

    private static final Color SLIDER_TRACK_COLOR = new Color(0.22f, 0.27f, 0.34f, 1f);
    private static final Color SLIDER_KNOB_COLOR = new Color(0.83f, 0.68f, 0.21f, 1f);

    private final MusicManager musics;
    private final KeyboardManager km = new KeyboardManager();
    private final MouseManager mm = new MouseManager();

    private final Viewport viewport = new FitViewport(720, 480);
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
        buildUI();
    }

    private void buildUI() {
        UiHelper.setupUiMouseHandlers(mm, viewport, uiRoot);

        uiRoot.addChild(
            new Text(
                "SOUND OPTIONS",
                font,
                Text.Alignment.CENTER,
                TITLE_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.START)
                    .padTop(72)
                    .height(24)
                    .getLayout()
            )
        );

        uiRoot.addChild(
            new Text(
                "Adjust the game volume",
                font,
                Text.Alignment.CENTER,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.START)
                    .padTop(108)
                    .height(11)
                    .getLayout()
            )
        );

        uiRoot.addChild(
            new Box(
                2,
                PANEL_BORDER_COLOR,
                PANEL_FILL_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.CENTER)
                    .width(420)
                    .height(170)
                    .padTop(10)
                    .getLayout()
            )
        );

        volumeText = new Text(
            formatVolumeText(Config.volume),
            font,
            Text.Alignment.CENTER,
            TEXT_COLOR,
            new UIRelativeLayout.Builder()
                .xAlignment(Alignment.CENTER)
                .yAlignment(Alignment.CENTER)
                .padTop(-80)
                .height(16)
                .getLayout()
        );
        uiRoot.addChild(volumeText);

        uiRoot.addChild(
            new Text(
                "[LEFT] / [RIGHT] or [MOUSE] to adjust",
                font,
                Text.Alignment.CENTER,
                HINT_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.CENTER)
                    .padTop(20)
                    .height(12)
                    .getLayout()
            )
        );

        volumeSlider = new Slider(
            0,
            100,
            10,
            SLIDER_TRACK_COLOR,
            10,
            SLIDER_KNOB_COLOR,
            20,
            new UIRelativeLayout.Builder()
                .xAlignment(Alignment.CENTER)
                .yAlignment(Alignment.CENTER)
                .padTop(120)
                .width(300)
                .height(40)
                .getLayout()
        );
        volumeSlider.addValueListener(value -> updateVolume(value));
        volumeSlider.setValue(Config.volume, true);
        uiRoot.addChild(volumeSlider);

        uiRoot.addChild(
            new Text(
                "ESC = Return to Main Menu",
                font,
                Text.Alignment.CENTER,
                HINT_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.END)
                    .padBottom(28)
                    .height(10.5f)
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