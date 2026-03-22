package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;

public class MainMenu extends Scene implements InputProcessor, Renderable {

    private final SceneManager sceneManager;
    private final KeyboardManager km;

    private final BitmapFont titleFont;
    private final BitmapFont subtitleFont;
    private final BitmapFont buttonFont;
    private final BitmapFont hintFont;
    private final GlyphLayout layout;

    private final Rectangle titlePanel;
    private final Rectangle startButton;
    private final Rectangle soundButton;
    private final Rectangle exitButton;

    private final TextureRegion white;

    public MainMenu(SceneManager sceneManager) {
        super(new FitViewport(640, 480));
        this.sceneManager = sceneManager;

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
        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, e ->
            Gdx.app.exit()
        );

        Skin skin = SimpleSkin.getSkin();
        white = new TextureRegion(skin.get("white", Texture.class));

        titleFont = new BitmapFont();
        titleFont.setColor(new Color(0.95f, 0.81f, 0.24f, 1f));
        titleFont.getData().setScale(1.9f);

        subtitleFont = new BitmapFont();
        subtitleFont.setColor(new Color(0.96f, 0.87f, 0.70f, 1f));
        subtitleFont.getData().setScale(0.95f);

        buttonFont = new BitmapFont();
        buttonFont.setColor(new Color(0.96f, 0.87f, 0.70f, 1f));
        buttonFont.getData().setScale(1.15f);

        hintFont = new BitmapFont();
        hintFont.setColor(new Color(0.82f, 0.78f, 0.70f, 1f));
        hintFont.getData().setScale(0.9f);

        layout = new GlyphLayout();

        titlePanel = new Rectangle();
        startButton = new Rectangle();
        soundButton = new Rectangle();
        exitButton = new Rectangle();
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(km);
        return multiplexer;
    }

    @Override
    public void onLoad() {
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void dispose() {
        super.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
        buttonFont.dispose();
        hintFont.dispose();
    }

    @Override
    public void update(float deltaTime) {
        km.update(deltaTime, Float.NaN);
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        ScreenUtils.clear(0.08f, 0.11f, 0.16f, 1f);
        viewport.update(width, height, true);

        float panelWidth = 420f;
        float panelX = (640f - panelWidth) / 2f;

        titlePanel.set(panelX, 330f, panelWidth, 90f);

        float buttonWidth = 320f;
        float buttonHeight = 58f;
        float buttonX = (640f - buttonWidth) / 2f;

        startButton.set(buttonX, 225f, buttonWidth, buttonHeight);
        soundButton.set(buttonX, 150f, buttonWidth, buttonHeight);
        exitButton.set(buttonX, 75f, buttonWidth, buttonHeight);

        graphics.beginRender(viewport);
        graphics.render(this, null);
        graphics.endRender();
    }

    @Override
    public boolean isVisible(Camera camera) {
        return true;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        Color goldTrim = new Color(0.83f, 0.68f, 0.21f, 1f);
        Color navyPanel = new Color(0.10f, 0.15f, 0.23f, 0.94f);
        Color hoverPanel = new Color(0.16f, 0.22f, 0.32f, 1f);
        Color parchmentText = new Color(0.96f, 0.87f, 0.70f, 1f);

        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

        boolean overStart = startButton.contains(mouse.x, mouse.y);
        boolean overSound = soundButton.contains(mouse.x, mouse.y);
        boolean overExit = exitButton.contains(mouse.x, mouse.y);

        // Buttons
        drawBox(batch, startButton.x, startButton.y, startButton.width, startButton.height,
                goldTrim, overStart ? hoverPanel : navyPanel);
        drawBox(batch, soundButton.x, soundButton.y, soundButton.width, soundButton.height,
                goldTrim, overSound ? hoverPanel : navyPanel);
        drawBox(batch, exitButton.x, exitButton.y, exitButton.width, exitButton.height,
                goldTrim, overExit ? hoverPanel : navyPanel);

        // Title
        titleFont.setColor(goldTrim);
        drawCentered(batch, titleFont, "HARBOUR FRANCA", titlePanel.x + titlePanel.width / 2f, titlePanel.y + 60f);

        subtitleFont.setColor(parchmentText);
        drawCentered(batch, subtitleFont, "Learn languages through smart trading",
                titlePanel.x + titlePanel.width / 2f, titlePanel.y + 28f);

        // Button labels
        buttonFont.setColor(parchmentText);
        drawCentered(batch, buttonFont, "Start Game", startButton.x + startButton.width / 2f, startButton.y + 37f);
        drawCentered(batch, buttonFont, "Sound Options", soundButton.x + soundButton.width / 2f, soundButton.y + 37f);
        drawCentered(batch, buttonFont, "Exit", exitButton.x + exitButton.width / 2f, exitButton.y + 37f);

        // Hint text
        hintFont.setColor(new Color(0.82f, 0.78f, 0.70f, 1f));
        drawCentered(batch, hintFont, "ENTER = Start   |   S = Sound   |   ESC = Exit", 320f, 30f);
    }

    private void drawCentered(TextureBatch batch, BitmapFont font, String text, float centerX, float y) {
        layout.setText(font, text);
        font.draw(batch, text, centerX - layout.width / 2f, y);
    }

    private void drawBox(TextureBatch batch, float x, float y, float width, float height, Color border, Color fill) {
        batch.setColor(border);
        batch.draw(white, x - 2, y - 2, width + 4, height + 4);
        batch.setColor(fill);
        batch.draw(white, x, y, width, height);
        batch.setColor(Color.WHITE);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 world = viewport.unproject(new Vector2(screenX, screenY));

        if (startButton.contains(world.x, world.y)) {
            sceneManager.setScene(Scenes.Explore);
            return true;
        }

        if (soundButton.contains(world.x, world.y)) {
            sceneManager.setScene(Scenes.SoundMenu);
            return true;
        }

        if (exitButton.contains(world.x, world.y)) {
            Gdx.app.exit();
            return true;
        }

        return false;
    }

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}