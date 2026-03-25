package com.simpulator.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.graphics.Skybox;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneCompositor;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.scene.SoundManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.game.Clock;
import com.simpulator.game.Config;
import com.simpulator.game.EntityTargeter;
import com.simpulator.game.RotateCameraAction;
import com.simpulator.game.Scenes;
import com.simpulator.game.SkyboxLoader;
import com.simpulator.game.TranslateCameraAction;
import com.simpulator.game.entities.CameraEntity;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.levels.Level;
import com.simpulator.game.levels.LevelManager;
import com.simpulator.game.trading.Inventory;
import java.util.List;

public class ExploreScene implements Scene {

    protected static final float PLAYER_SPEED = 4f;

    protected final SceneManager sceneManager;
    protected final LevelManager levelManager;
    protected final TextureCache textures = new TextureCache();
    protected final SoundManager sounds = new SoundManager();
    protected final EntityManager entityManager = new EntityManager();
    protected final KeyboardManager keyboard = new KeyboardManager();
    protected final MouseManager mouse = new MouseManager();

    protected final Viewport viewport = new ExtendViewport(640, 480);
    protected final CameraEntity playerCamera;

    protected final Skybox skybox;
    protected final SceneCompositor overlays = new SceneCompositor();
    protected final GameHUD hud;
    protected TradingUI tradingUI = null;

    protected final Clock clock = new Clock(0);
    protected final List<MerchantEntity> merchants;
    protected final EntityTargeter<MerchantEntity> merchantTargeter;

    private final Level level;
    protected final Inventory playerInventory;
    private final int valueGoal;

    public ExploreScene(
        SceneManager sceneManager,
        LevelManager levelManager,
        MusicManager musics,
        Level level
    ) {
        this.sceneManager = sceneManager;
        this.levelManager = levelManager;
        musics.stopAllMusic();
        musics.startMusic(level.bgmPath);
        sounds.setVolume(Config.volume * 0.01f);

        PerspectiveCamera camera = new PerspectiveCamera(
            70,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );
        camera.near = 0.05f;
        camera.far = 100f;
        viewport.setCamera(camera);

        playerCamera = new CameraEntity(
            level.playerStart,
            new Vector3(1, 1, 1),
            new Quaternion().setFromAxis(Vector3.Y, 0),
            camera
        );
        entityManager.add(playerCamera);

        skybox = SkyboxLoader.load(textures, level.skyboxPath, camera.far);
        level.map.load(entityManager, textures);

        playerInventory = level.createInventory();
        hud = new GameHUD(level.valueGoal, playerInventory);
        overlays.push(hud, new UIRelativeLayout());

        merchants = level.createMerchants(textures, playerCamera);
        entityManager.addAll(merchants);
        merchantTargeter = new EntityTargeter<>(merchants, 2);

        setupKeybinds(sceneManager);
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));
        mouse.bindButton(ButtonBindType.DOWN, Input.Buttons.RIGHT, event ->
            openTradingUIWithLookingAt()
        );

        this.level = level;
        valueGoal = level.valueGoal;
    }

    private void setupKeybinds(SceneManager sceneManager) {
        keyboard.bind(
            ButtonBindType.HOLD,
            Keys.W,
            new TranslateCameraAction(
                playerCamera,
                new Vector3(0, 0, -PLAYER_SPEED)
            )
        );
        keyboard.bind(
            ButtonBindType.HOLD,
            Keys.A,
            new TranslateCameraAction(
                playerCamera,
                new Vector3(-PLAYER_SPEED, 0, 0)
            )
        );
        keyboard.bind(
            ButtonBindType.HOLD,
            Keys.S,
            new TranslateCameraAction(
                playerCamera,
                new Vector3(0, 0, PLAYER_SPEED)
            )
        );
        keyboard.bind(
            ButtonBindType.HOLD,
            Keys.D,
            new TranslateCameraAction(
                playerCamera,
                new Vector3(PLAYER_SPEED, 0, 0)
            )
        );

        keyboard.bind(ButtonBindType.DOWN, Keys.ESCAPE, e ->
            sceneManager.setScene(Scenes.MainMenu)
        );
        keyboard.bind(ButtonBindType.DOWN, Keys.E, event ->
            openTradingUIWithLookingAt()
        );
    }

    protected int getValueGoal() {
        return valueGoal;
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(overlays.getInputProcessor());
        inputMux.addProcessor(keyboard);
        inputMux.addProcessor(mouse);
        return inputMux;
    }

    @Override
    public boolean onFocus() {
        if (overlays.onFocus()) {
            return true;
        }

        Gdx.input.setCursorCatched(true);
        return true;
    }

    @Override
    public void dispose() {
        overlays.dispose();
        sounds.dispose();
        textures.dispose();
    }

    protected void openTradingUIWithLookingAt() {
        MerchantEntity target = merchantTargeter.getClosest(
            playerCamera.getCamera()
        );

        if (target == null || !target.canTrade()) return;
        if (isTradingUIOpen()) {
            throw new IllegalStateException("Trading UI is already open");
        }

        tradingUI = new TradingUI(playerInventory, target, textures);
        overlays.push(tradingUI, new UIRelativeLayout());
        hud.setCrosshairVisible(false);
        hud.setPromptVisible(false);
        hud.setInventoryVisible(false);

        Gdx.input.setInputProcessor(getInputProcessor());
        onFocus();
    }

    protected void closeTradingUI() {
        if (!isTradingUIOpen()) {
            throw new IllegalStateException("Trading UI is not open");
        }

        Scene removed = overlays.pop();
        removed.dispose();
        tradingUI = null;
        hud.setCrosshairVisible(true);
        hud.setInventoryVisible(true);

        mouse.resetMousePosition();
        Gdx.input.setInputProcessor(getInputProcessor());
        onFocus();
    }

    protected void checkWinCondition() {
        // TODO: change this to some gatekeeper or smth that the player interacts with to check
        for (MerchantEntity merchant : merchants) {
            if (merchant.canTrade()) {
                return;
            }
        }

        if (playerInventory.getTotalValue() >= valueGoal) {
            if (level.nextLevelId == null) {
                // TODO ?
                // tradingUI.showTradeResult("Level Complete! Goal reached!");
                sceneManager.setScene(Scenes.Win);
            } else {
                levelManager.setCurrentLevelId(level.nextLevelId);
                sceneManager.setScene(Scenes.Explore);
            }
        } else {
            // lose scene
            sceneManager.setScene(Scenes.Lose);
        }
    }

    protected boolean isTradingUIOpen() {
        return tradingUI != null;
    }

    protected void updateTargeted() {
        if (isTradingUIOpen()) return;

        MerchantEntity targeted = merchantTargeter.getClosest(
            playerCamera.getCamera()
        );
        if (targeted == null) {
            hud.setPromptVisible(false);
        } else {
            hud.setPromptVisible(true);
            if (targeted.canTrade()) {
                hud.setPrompt("[E] Trade with " + targeted.getData().getName());
            } else {
                hud.setPrompt(
                    targeted.getData().getName() + " does not want to trade."
                );
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (tradingUI != null && tradingUI.shouldClose()) {
            closeTradingUI();
            checkWinCondition();
        }

        clock.forward(deltaTime);
        if (!isTradingUIOpen()) {
            keyboard.update(deltaTime, clock.getSeconds());
            mouse.update(deltaTime, clock.getSeconds());
            updateTargeted();
        }
        entityManager.updateCollisions();
        entityManager.update(deltaTime);
        overlays.update(deltaTime);
    }

    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        viewport.update(width, height);
        viewport.setScreenPosition(
            viewport.getScreenX() + y,
            viewport.getScreenY() + y
        );

        // Render Sky box first
        graphics.beginRender(viewport);
        graphics.render3D(skybox, playerCamera.getCamera());
        graphics.endRender(); // End render so that everything else is rendered on top of the skybox

        // Render world entities
        graphics.beginRender(viewport);
        graphics.render3D(
            entityManager.getEntities(),
            playerCamera.getCamera()
        );
        graphics.endRender();

        // Render UI on top
        overlays.render(
            graphics,
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
    }
}
