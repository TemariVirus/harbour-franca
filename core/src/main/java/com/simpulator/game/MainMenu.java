package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.input.MouseManager.MouseButton;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;

public class MainMenu extends Scene {

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

    private final KeyboardManager km;
    private final MouseManager mm;
    private UIRoot uiRoot;
    private BitmapFont font;

    public MainMenu(SceneManager sceneManager) {
        super(new FitViewport(640, 480));
        sounds.setVolume(Config.volume * 0.01f);

        km = new KeyboardManager();
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

        mm = new MouseManager();

        buildUI(sceneManager);
    }

    private void buildUI(SceneManager sceneManager) {
        uiRoot = new UIRoot();
        font = new BitmapFont();
        mm.bindMove(e -> {
            Vector2 world = viewport.unproject(new Vector2(e.x, e.y));
            uiRoot.handleEvent(
                new MouseManager.MouseMoveEvent(
                    (int) world.x,
                    (int) world.y,
                    e.deltaX,
                    e.deltaY,
                    e.deltaTime,
                    e.timestamp
                )
            );
        });
        mm.bindButton(ButtonBindType.DOWN, MouseButton.LEFT, e -> {
            Vector2 world = viewport.unproject(new Vector2(e.x, e.y));
            uiRoot.handleEvent(
                new MouseManager.MouseButtonEvent(
                    (int) world.x,
                    (int) world.y,
                    e.button,
                    e.type,
                    e.deltaTime,
                    e.timestamp
                )
            );
        });

        uiRoot.addChild(
            new Text.Builder(
                "HARBOUR FRANCA",
                font,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(90)
                    .height(21)
                    .getLayout()
            )
                .xAnchor(Text.Alignment.CENTER)
                .color(TITLE_COLOR)
                .getText()
        );
        uiRoot.addChild(
            new Text.Builder(
                "Learn languages through smart trading",
                font,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.START)
                    .padTop(118)
                    .height(10.5f)
                    .getLayout()
            )
                .xAnchor(Text.Alignment.CENTER)
                .color(TEXT_COLOR)
                .getText()
        );

        addMenuButton("Start Game", 225, e ->
            sceneManager.setScene(Scenes.Explore)
        );
        addMenuButton("Sound Options", 150, e ->
            sceneManager.setScene(Scenes.SoundMenu)
        );
        addMenuButton("Exit", 75, e -> Gdx.app.exit());

        uiRoot.addChild(
            new Text.Builder(
                "ENTER = Start   |   S = Sound   |   ESC = Exit",
                font,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.END)
                    .padBottom(24)
                    .height(10.5f)
                    .getLayout()
            )
                .xAnchor(Text.Alignment.CENTER)
                .color(HINT_COLOR)
                .getText()
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

        Box button = new Box(
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
        );
        button.addHoverColor(BUTTON_COLOR, BUTTON_HOVER_COLOR);
        button.addListener(MouseManager.MouseButtonEvent.class, e -> {
            if (e.button == MouseButton.LEFT && e.type == ButtonBindType.DOWN) {
                onClick.act(e);
            }
            return true;
        });
        button.addChild(
            new Text.Builder(
                text,
                font,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.CENTER)
                    .height(FONT_SIZE)
                    .getLayout()
            )
                .xAnchor(Text.Alignment.CENTER)
                .color(TEXT_COLOR)
                .getText()
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
