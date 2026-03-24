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
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.scene.SoundManager;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.Config;
import com.simpulator.game.Scenes;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UiHelper;

public class MainMenu implements Scene {

    private static final Color BACKGROUND_COLOR = new Color(
        0.08f,
        0.11f,
        0.16f,
        1f
    );
    private static final Color TITLE_COLOR = new Color(0.83f, 0.68f, 0.21f, 1f);
    private static final Color TEXT_COLOR = new Color(0.96f, 0.87f, 0.70f, 1f);
    private static final Color HINT_COLOR = new Color(0.82f, 0.78f, 0.70f, 1f);
    private static final Color BUTTON_COLOR = new Color(
        0.10f,
        0.15f,
        0.23f,
        0.94f
    );
    private static final Color BUTTON_HOVER_COLOR = new Color(
        0.16f,
        0.22f,
        0.32f,
        1f
    );

    private final SoundManager sounds = new SoundManager();
    private final KeyboardManager km = new KeyboardManager();
    private final MouseManager mm = new MouseManager();

    private final Viewport viewport = new FitViewport(640, 480);
    private final UIRoot uiRoot = new UIRoot();
    private final BitmapFont font;

    public MainMenu(SceneManager sceneManager) {
        sounds.setVolume(Config.volume * 0.01f);

        km.bind(ButtonBindType.DOWN, Keys.ENTER, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        km.bind(ButtonBindType.DOWN, Keys.G, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        km.bind(ButtonBindType.DOWN, Keys.S, e ->
            sceneManager.setScene(Scenes.SoundMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, e -> Gdx.app.exit());

        font = new BitmapFont();
        buildUI(sceneManager);
    }

    private void buildUI(SceneManager sceneManager) {
        UiHelper.setupUiMouseHandlers(mm, viewport, uiRoot);

        uiRoot.addChild(
            new Text(
                "HARBOUR FRANCA",
                font,
                Text.Alignment.CENTER,
                TITLE_COLOR,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(90)
                    .height(21)
                    .getLayout()
            )
        );
        uiRoot.addChild(
            new Text(
                "Learn languages through smart trading",
                font,
                Text.Alignment.CENTER,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(118)
                    .height(10.5f)
                    .getLayout()
            )
        );

        addMenuButton("Start Game", 225, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        addMenuButton("Sound Options", 150, e ->
            sceneManager.setScene(Scenes.SoundMenu)
        );
        addMenuButton("Exit", 75, e -> Gdx.app.exit());

        uiRoot.addChild(
            new Text(
                "ENTER = Start   |   S = Sound   |   ESC = Exit",
                font,
                Text.Alignment.CENTER,
                HINT_COLOR,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.END)
                    .padBottom(24)
                    .height(10.5f)
                    .getLayout()
            )
        );
    }

    private void addMenuButton(
        String text,
        float y,
        Action<MouseManager.MouseButtonEvent> onClick
    ) {
        final float WIDTH = 320f;
        final float HEIGHT = 58f;
        final int BORDER_WIDTH = 2;
        final float FONT_SIZE = 13f;

        Box button = UiHelper.createButton(
            new Box(
                BORDER_WIDTH,
                TITLE_COLOR,
                BUTTON_COLOR,
                new UIRelativeLayout.Builder()
                    .xAlignment(Alignment.CENTER)
                    .yAlignment(Alignment.END)
                    .padBottom(y)
                    .width(WIDTH)
                    .height(HEIGHT)
                    .getLayout()
            ),
            BUTTON_HOVER_COLOR,
            new Text(
                text,
                font,
                Text.Alignment.CENTER,
                TEXT_COLOR,
                new UIRelativeLayout()
            ),
            FONT_SIZE,
            e -> {
                onClick.act(e);
                return true;
            }
        );
        uiRoot.addChild(button);
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
        viewport.setScreenPosition(x, y);
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }
}
