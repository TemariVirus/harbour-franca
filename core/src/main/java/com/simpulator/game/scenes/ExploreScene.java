package com.simpulator.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneCompositor;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.engine.scene.SoundManager;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.Clock;
import com.simpulator.game.Config;
import com.simpulator.game.EntityTargeter;
import com.simpulator.game.RotateCameraAction;
import com.simpulator.game.Scenes;
import com.simpulator.game.Skybox;
import com.simpulator.game.SkyboxLoader;
import com.simpulator.game.TranslateCameraAction;
import com.simpulator.game.entities.CameraEntity;
import com.simpulator.game.entities.GatekeeperEntity;
import com.simpulator.game.entities.MerchantEntity;
import com.simpulator.game.levels.Level;
import com.simpulator.game.levels.LevelManager;
import com.simpulator.game.trading.Inventory;
import com.simpulator.game.ui.UIRelativeLayout;
import java.util.List;

public class ExploreScene implements Scene {

    protected static final float PLAYER_SPEED = 4f;

    private final SceneManager sceneManager;
    private final GraphicsManager graphics;
    private final LevelManager levelManager;
    protected final TextureCache textures = new TextureCache();
    protected final SoundManager sounds = new SoundManager();
    protected final EntityManager entityManager = new EntityManager();
    protected final KeyboardManager keyboard = new KeyboardManager();
    protected final MouseManager mouse = new MouseManager();

    private final Viewport viewport = new ExtendViewport(640, 480);
    protected final CameraEntity playerCamera;

    private final Skybox skybox;
    protected final SceneCompositor overlays = new SceneCompositor();
    protected final GameHUD hud;
    private TradingUI tradingUI = null;

    protected final Clock clock = new Clock(0);
    protected final List<MerchantEntity> merchants;
    protected final EntityTargeter<MerchantEntity> merchantTargeter;

    protected final List<GatekeeperEntity> gatekeepers;
    protected final EntityTargeter<GatekeeperEntity> gatekeeperTargeter;

    protected final Level level;
    protected final Inventory playerInventory;
    protected final int valueGoal;
    private int hintsRemaining = 2;

    public ExploreScene(
        SceneManager sceneManager,
        GraphicsManager graphics,
        LevelManager levelManager,
        MusicManager musics,
        Level level
    ) {
        this.sceneManager = sceneManager;
        this.graphics = graphics;
        this.levelManager = levelManager;
        musics.stopAllMusic();
        musics.startMusic(level.getBgmPath());
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
            level.getPlayerStartPosition(),
            new Vector3(1, 1, 1),
            level.getPlayerStartRotation(),
            camera
        );
        entityManager.add(playerCamera);

        skybox = SkyboxLoader.load(textures, level.getSkyboxPath(), camera.far);
        level.loadMap(entityManager, textures);

        playerInventory = level.createInventory();
        hud = new GameHUD(graphics, level.getValueGoal(), playerInventory);
        hud.setHintCount(hintsRemaining);
        overlays.push(hud, new UIRelativeLayout());

        merchants = level.createMerchants(textures, playerCamera);
        entityManager.addAll(merchants);
        merchantTargeter = new EntityTargeter<>(merchants, 2);

        gatekeepers = level.createGatekeepers(textures, playerCamera);
        entityManager.addAll(gatekeepers);
        // Create a targeter so the player can look at the gatekeeper (distance of 3 units)
        gatekeeperTargeter = new EntityTargeter<>(gatekeepers, 3);

        setupKeybinds(sceneManager);
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));
        mouse.bindButton(ButtonBindType.DOWN, Input.Buttons.RIGHT, event ->
            openTradingUIWithLookingAt()
        );

        this.level = level;
        valueGoal = level.getValueGoal();
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
        keyboard.bind(ButtonBindType.DOWN, Keys.H, event -> showHint());
        keyboard.bind(ButtonBindType.DOWN, Keys.E, event -> handleInteract());
    }

    private void showHint() {
        // Find the merchant the player is currently looking at / talking to
        MerchantEntity activeMerchant = merchantTargeter.getClosest(
            playerCamera.getCamera()
        );
        if (activeMerchant == null) {
            return;
        }

        String npcHint = activeMerchant.getData().hint;
        // Check if they have a hint and if we have any hints remaining
        if (hintsRemaining > 0 && npcHint != null && !npcHint.isEmpty()) {
            hintsRemaining--;
            hud.setHintCount(hintsRemaining);
            hud.showHint("Hint: " + npcHint);
            sounds.play("sfx/trade-good.ogg");
        }
    }

    private void handleInteract() {
        // First, see if we are trying to interact with the gatekeeper
        GatekeeperEntity gatekeeper = gatekeeperTargeter.getClosest(
            playerCamera.getCamera()
        );
        if (gatekeeper != null) {
            tryGoToNextLevel();
            return;
        }

        // If not looking at the door, try trading normally
        openTradingUIWithLookingAt();
    }

    protected void tryGoToNextLevel() {
        if (playerInventory.getTotalValue() < valueGoal) {
            return;
        }

        if (level.getNextLevelId() == null) {
            sceneManager.setScene(Scenes.Win);
        } else {
            levelManager.setCurrentLevelId(level.getNextLevelId());
            sceneManager.setScene(Scenes.Explore);
        }
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

        tradingUI = new TradingUI(
            playerInventory,
            target,
            graphics,
            textures,
            sounds
        );
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
        for (MerchantEntity merchant : merchants) {
            if (merchant.canTrade()) {
                return; // There are still merchants left to trade with
            }
        }

        if (playerInventory.getTotalValue() < valueGoal) {
            sceneManager.setScene(Scenes.Lose);
        } else if (gatekeepers.size() == 0) {
            // If there are no gatekeepers, just win immediately
            tryGoToNextLevel();
        }
    }

    protected boolean isTradingUIOpen() {
        return tradingUI != null;
    }

    protected void updateTargeted() {
        if (isTradingUIOpen()) return;

        GatekeeperEntity gatekeeper = gatekeeperTargeter.getClosest(
            playerCamera.getCamera()
        );
        if (gatekeeper != null) {
            hud.setPromptVisible(true);
            if (playerInventory.getTotalValue() >= valueGoal) {
                hud.setPrompt("[E] Open the door (You have enough gold!)");
            } else {
                hud.setPrompt(
                    "Thou shall not pass! (Until you get enough gold)"
                );
            }
            return;
        }

        MerchantEntity targeted = merchantTargeter.getClosest(
            playerCamera.getCamera()
        );
        if (targeted != null) {
            hud.setPromptVisible(true);
            if (targeted.canTrade()) {
                hud.setPrompt("[E] Trade with " + targeted.getData().getName());
            } else {
                hud.setPrompt(
                    targeted.getData().getName() + " does not want to trade."
                );
            }
            return;
        }

        hud.setPromptVisible(false);
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
    public void render(int x, int y, int width, int height) {
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
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
    }
}
