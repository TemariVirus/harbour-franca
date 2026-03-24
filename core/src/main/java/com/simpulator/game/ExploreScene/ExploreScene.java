package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.graphics.Skybox;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.MusicManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.Clock;
import com.simpulator.game.Config;
import com.simpulator.game.Scenes;
import com.simpulator.game.SkyboxLoader;
import com.simpulator.game.TradingUI;
import com.simpulator.game.levels.Level;
import com.simpulator.game.trading.Inventory;
import java.util.List;

public class ExploreScene extends Scene {

    private static final float PLAYER_SPEED = 4f;

    private int selectedDialogueIndex = -1;
    private int selectedPlayerItemIndex = 0;

    private final Clock clock = new Clock(0);
    private final CameraEntity playerCamera;
    private final Skybox skybox;

    private final EntityManager entityManager;
    private final KeyboardManager keyboard;
    private final MouseManager mouse;

    private List<MerchantEntity> npcs;
    private NpcTargetingSystem npcTargetingSystem;

    private final Inventory playerInventory;
    private final GameHUD hud;
    private TradingUI tradingUI;
    private int valueGoal;
    private boolean victoryQueued = false;

    public ExploreScene(
        SceneManager sceneManager,
        Level level,
        MusicManager musics
    ) {
        super(new ExtendViewport(640, 480));
        musics.stopAllMusic();
        musics.startMusic(level.bgmPath);
        sounds.setVolume(Config.volume * 0.01f);
        this.entityManager = new EntityManager();

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

        keyboard = new KeyboardManager();
        setupKeybinds(sceneManager);

        mouse = new MouseManager();
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));
        mouse.bindButton(ButtonBindType.DOWN, Input.Buttons.RIGHT, event -> {
            MerchantEntity target = npcTargetingSystem.getTargetedNpc();
            if (target != null && !tradingUI.isVisible()) {
                openTradingUI(target);
            }
        });

        npcs = level.createMerchants(textures, playerCamera);
        entityManager.addAll(npcs);
        npcTargetingSystem = new NpcTargetingSystem(
            playerCamera.getCamera(),
            npcs
        );

        playerInventory = level.createInventory();

        hud = new GameHUD(level.valueGoal, playerInventory);
        tradingUI = new TradingUI();
        // tradingUI.setListener(
        //     new TradingUI.TradingUIListener() {
        //         @Override
        //         public void onDialogueSelected(int optionIndex) {
        //             selectedDialogueIndex = optionIndex;
        //             updateInnerThought();
        //         }

        //         @Override
        //         public void onNpcItemChanged(int newIndex) {
        //             selectedPlayerItemIndex = newIndex;
        //             updateInnerThought();
        //         }

        //         @Override
        //         public void onTradeConfirmed(int playerItemIndex) {
        //             if (
        //                 currentOffer == null || selectedDialogueIndex < 0
        //             ) return;

        //             Item playerItem = playerInventory
        //                 .getItems()
        //                 .get(playerItemIndex);
        //             Item npcItem = currentOffer
        //                 .getNpcChoices()
        //                 .get(selectedDialogueIndex);
        //             TradeResult result = tradeManager.attemptTrade(
        //                 playerItem,
        //                 npcItem
        //             );

        //             MerchantEntity target = npcTargetingSystem.getTargetedNpc();
        //             if (target != null) {
        //                 switch (result) {
        //                     case SUCCESS:
        //                     case NPC_HAPPY:
        //                         target.setTradeState(
        //                             NpcEntity.MerchantEntity.TRADED
        //                         );
        //                         break;
        //                     case FAILED:
        //                         target.setTradeState(
        //                             NpcEntity.MerchantEntity.ANGRY
        //                         );
        //                         break;
        //                 }
        //             }

        //             String resultText;
        //             if (result == TradeResult.NPC_HAPPY) resultText =
        //                 "NPC is happy with the deal!";
        //             else if (result == TradeResult.SUCCESS) resultText =
        //                 "Trade successful!";
        //             else resultText = "NPC rejected the trade!";
        //             tradingUI.showTradeResult(resultText);

        //             syncHUD();
        //             currentOffer = null;
        //             selectedDialogueIndex = -1;
        //             selectedPlayerItemIndex = 0;

        //             if (tradeManager.isLevelComplete() && !victoryQueued) {
        //                 victoryQueued = true;
        //                 tradingUI.showTradeResult(
        //                     "Level Complete! Goal reached!"
        //                 );

        //                 Gdx.input.setCursorCatched(false);

        //                 Timer.schedule(
        //                     new Timer.Task() {
        //                         @Override
        //                         public void run() {
        //                             sceneManager.setScene(Scenes.Victory);
        //                         }
        //                     },
        //                     2f
        //                 );
        //             } else {
        //                 Timer.schedule(
        //                     new Timer.Task() {
        //                         @Override
        //                         public void run() {
        //                             closeTradingUI();
        //                         }
        //                     },
        //                     2f
        //                 );
        //             }
        //         }

        //         @Override
        //         public void onTradeCancelled() {
        //             currentOffer = null;
        //             selectedDialogueIndex = -1;
        //             selectedPlayerItemIndex = 0;
        //             closeTradingUI();
        //         }

        //         @Override
        //         public void onTimeUp() {
        //             currentOffer = null;
        //             selectedDialogueIndex = -1;
        //             selectedPlayerItemIndex = 0;
        //             tradingUI.showTradeResult("Too slow!");
        //             Timer.schedule(
        //                 new Timer.Task() {
        //                     @Override
        //                     public void run() {
        //                         closeTradingUI();
        //                     }
        //                 },
        //                 2f
        //             );
        //         }
        //     }
        // );

        this.valueGoal = level.valueGoal;
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
        keyboard.bind(ButtonBindType.DOWN, Keys.E, event -> {
            MerchantEntity target = npcTargetingSystem.getTargetedNpc();
            if (target != null && !tradingUI.isVisible()) {
                openTradingUI(target);
            }
        });
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(keyboard);
        inputMux.addProcessor(mouse);
        return inputMux;
    }

    @Override
    public void onFocus() {
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        hud.dispose();
        if (tradingUI != null) {
            tradingUI.dispose();
        }
    }

    private void openTradingUI(MerchantEntity target) {
        // if (!target.canTrade() || tradingUI.isVisible()) return;
        //
        // if (target.getCachedOffer() == null) {
        //     try {
        //         target.setCachedOffer(
        //             tradeOfferFactory.createOffer(
        //                 playerInventory,
        //                 target.getDialogueOptions()
        //             )
        //         );
        //     } catch (IllegalStateException e) {
        //         Gdx.app.log("Trade", "createOffer failed: " + e.getMessage());
        //         return;
        //     }
        // }
        // currentOffer = target.getCachedOffer();
        //
        // selectedDialogueIndex = -1;
        // selectedPlayerItemIndex = 0;
        //
        // List<Item> allItems = playerInventory.getItems();
        // String[] playerNames = allItems
        //     .stream()
        //     .map(Item::getName)
        //     .toArray(String[]::new);
        // String[] playerRarities = allItems
        //     .stream()
        //     .map(i -> i.getRarity().name())
        //     .toArray(String[]::new);
        //
        // String[] npcDialogue = currentOffer
        //     .getNpcDialogueLabels()
        //     .toArray(new String[0]);
        //
        // tradingUI.show(
        //     target.getName(),
        //     npcDialogue,
        //     playerNames,
        //     playerRarities
        // );
        // InputMultiplexer inputMux = new InputMultiplexer();
        // inputMux.addProcessor(tradingUI.getInputProcessor());
        // inputMux.addProcessor(keyboard);
        // inputMux.addProcessor(mouse);
        // Gdx.input.setCursorCatched(false);
        // Gdx.input.setInputProcessor(inputMux);
    }

    private void closeTradingUI() {
        tradingUI.hide();
        if (!victoryQueued) {
            Gdx.input.setCursorCatched(true);
            Gdx.input.setInputProcessor(getInputProcessor());
            mouse.resetMousePosition();
        }
    }

    @Override
    public void update(float deltaTime) {
        clock.forward(deltaTime);
        if (!tradingUI.isVisible()) {
            keyboard.update(deltaTime, clock.getSeconds());
            mouse.update(deltaTime, clock.getSeconds());

            npcTargetingSystem.update();
            MerchantEntity targeted = npcTargetingSystem.getTargetedNpc();
            if (targeted == null) {
                hud.hideInteractionPrompt();
            } else if (targeted.canTrade()) {
                hud.showInteractionPrompt(
                    "[E] Trade with " + targeted.getData().getName()
                );
            } else {
                hud.showInteractionPrompt(
                    targeted.getData().getName() + " does not want to trade."
                );
            }
        } else {
            hud.hideInteractionPrompt();
        }

        entityManager.updateCollisions();
        entityManager.update(deltaTime);
        hud.update(deltaTime);
        tradingUI.update(deltaTime);
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        viewport.update(width, height);

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
        hud.render(
            graphics,
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
        tradingUI.render(
            graphics,
            viewport.getScreenX(),
            viewport.getScreenY(),
            viewport.getScreenWidth(),
            viewport.getScreenHeight()
        );
    }
}
