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
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.Scenes;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UiHelper;

public class ResultScene implements Scene {

    public enum ResultType {
        WIN,
        LOSE,
    }

    private final SceneManager sceneManager;
    private final ResultType resultType;

    private final KeyboardManager km = new KeyboardManager();
    private final MouseManager mm = new MouseManager();

    private final Viewport viewport = new FitViewport(720, 480);
    private final UIRoot uiRoot = new UIRoot();
    private final BitmapFont font;

    private Color backgroundColor;
    private Color titleColor;

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

    public ResultScene(SceneManager sceneManager, ResultType resultType) {
        this.sceneManager = sceneManager;
        this.resultType = resultType;

        km.bind(ButtonBindType.DOWN, Keys.ENTER, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        km.bind(ButtonBindType.DOWN, Keys.M, e ->
            sceneManager.setScene(Scenes.MainMenu)
        );
        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, e -> Gdx.app.exit());

        font = new BitmapFont(Gdx.files.internal("fonts/en.fnt"));

        setupColors();
        buildUI();
    }

    private void setupColors() {
        if (resultType == ResultType.WIN) {
            backgroundColor = new Color(0.08f, 0.11f, 0.16f, 1f);
            titleColor = new Color(0.83f, 0.68f, 0.21f, 1f);
        } else {
            backgroundColor = new Color(0.16f, 0.08f, 0.08f, 1f);
            titleColor = new Color(0.85f, 0.25f, 0.25f, 1f);
        }
    }

    private void buildUI() {
        UiHelper.setupUiMouseHandlers(mm, viewport, uiRoot);

        String title = (resultType == ResultType.WIN)
            ? "YOU WIN!"
            : "YOU LOST!";
        String subtitle = (resultType == ResultType.WIN)
            ? "Great job completing the level"
            : "Better luck next time";

        uiRoot.addChild(
            new Text(
                title,
                font,
                Text.Alignment.CENTER,
                titleColor,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(90)
                    .height(24)
                    .getLayout()
            )
        );

        uiRoot.addChild(
            new Text(
                subtitle,
                font,
                Text.Alignment.CENTER,
                TEXT_COLOR,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(122)
                    .height(11)
                    .getLayout()
            )
        );

        addMenuButton("Play Again", 225, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        addMenuButton("Main Menu", 150, e ->
            sceneManager.setScene(Scenes.MainMenu)
        );
        addMenuButton("Quit", 75, e -> Gdx.app.exit());

        uiRoot.addChild(
            new Text(
                "ENTER = Replay   |   M = Main Menu   |   ESC = QUIT",
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

        Box button = UiHelper.createButton(
            new Box(
                2,
                titleColor,
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
            13f,
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
        ScreenUtils.clear(backgroundColor);
        viewport.update(width, height, true);
        viewport.setScreenPosition(x, y);
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }
}
