package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.graphics.Skybox;
import com.simpulator.engine.input.ButtonManager.ButtonBindType;
import com.simpulator.engine.input.KeyboardManager;
import com.simpulator.engine.input.MouseManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.scene.SceneManager;
import com.simpulator.game.ActionHelper;
import com.simpulator.game.Clock;
import com.simpulator.game.Config;
import com.simpulator.game.CuboidEntity;
import com.simpulator.game.Level;
import com.simpulator.game.Scenes;
import com.simpulator.game.TiledRenderer;
import java.util.ArrayList;
import java.util.List;
import com.simpulator.game.ExploreScene.NpcEntity;
import com.simpulator.game.ExploreScene.GameHUD;
import com.simpulator.game.ExploreScene.NpcTargetingSystem;
import com.simpulator.game.SimpleSkin;
import com.simpulator.engine.graphics.RectangleRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.KeyboardManager.KeyEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.simpulator.game.TradingUI;

public class ExploreScene extends Scene {

    private static final String BRICK_IMG = "Oran.jpeg";
    private static final float PLAYER_SPEED = 4f;

    private final Clock clock = new Clock(0);
    private CameraEntity playerCamera;

    private Viewport viewport;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private InputMultiplexer inputMux;
    private KeyboardManager keyboard;
    private MouseManager mouse;

    private Level currentLevel;
    private Skybox skybox;

    private List<NpcEntity> npcs = new ArrayList<>();
    private GameHUD hud;
    private TradingUI tradingUI;
    private NpcTargetingSystem npcTargetingSystem;

    public ExploreScene(SceneManager sceneManager, Level level) {
        this.viewport = new ExtendViewport(640, 480);
        this.sceneManager = sceneManager;
        this.currentLevel = level;
        this.entityManager = new EntityManager();
    }

    private void createLevelLayout() {
        final float WIDTH = 5;
        final float HEIGHT = 3;
        final float WALL_THICKNESS = 0.2f;

        TiledRenderer renderer = new TiledRenderer(
            new TextureRegion(textures.get(BRICK_IMG)),
            new Vector2(1, 1)
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(0, 0, WIDTH),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 0),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(0, 0, -WIDTH),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 180),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(WIDTH, 0, 0),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, 90),
                renderer,
                false
            )
        );
        entityManager.add(
            new CuboidEntity(
                new Vector3(-WIDTH, 0, 0),
                new Vector3(WIDTH * 2, HEIGHT, WALL_THICKNESS),
                new Quaternion().setFromAxis(Vector3.Y, -90),
                renderer,
                false
            )
        );
    }

    @Override
    public void load() {
        Gdx.input.setCursorCatched(true);

        PerspectiveCamera camera = new PerspectiveCamera(
            70,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        camera.near = 0.05f;
        camera.far = 100f;
        viewport.setCamera(camera);

        // @formatter:off
        playerCamera = new CameraEntity(
            new Vector3(currentLevel.playerStartX, currentLevel.playerStartY, currentLevel.playerStartZ),
            new Vector3(1, 1, 1),
            new Quaternion().setFromAxis(Vector3.Y, 0),
            camera
        );
        entityManager.add(playerCamera);

        // Load the Sky box
        if (currentLevel.skyboxTexturePrefix != null) {
            TextureRegion[] faces = new TextureRegion[6];
            faces[0] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_ft.png"));
            faces[1] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_bk.png"));
            faces[2] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_lf.png"));
            faces[3] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_rt.png"));
            faces[4] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_up.png"));
            faces[5] = new TextureRegion(textures.get(currentLevel.skyboxTexturePrefix + "_dn.png"));

            skybox = new Skybox(faces, camera.far);
        }

        sounds.setVolume(Config.volume * 0.01f);

        Skin hudSkin = SimpleSkin.getSkin();
        
        // GameHUD requires "default", "title", and "prompt" label styles.
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = hudSkin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        hudSkin.add("default", labelStyle);
        hudSkin.add("title", labelStyle);
        
        Label.LabelStyle promptStyle = new Label.LabelStyle();
        promptStyle.font = hudSkin.getFont("default");
        promptStyle.fontColor = Color.YELLOW;
        hudSkin.add("prompt", promptStyle);

        // --- Initialize Engine Input Managers first ---
        keyboard = new KeyboardManager();
        mouse = new MouseManager();
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));
        
        inputMux = new InputMultiplexer();
        inputMux.addProcessor(keyboard);
        inputMux.addProcessor(mouse);

        hud = new GameHUD(hudSkin, sceneManager.getGraphics().getBatch());
        hud.setInventory(new String[]{"Sword", "Shield", "Potion"});
        
        tradingUI = new TradingUI(hudSkin);
        tradingUI.setListener(new TradingUI.TradingUIListener() {
            @Override
            public void onDialogueSelected(int optionIndex) {
                 if (optionIndex == 0) tradingUI.setInnerThoughts("No way he's trading that!");
                 else if (optionIndex == 1) tradingUI.setInnerThoughts("Hopefully his not making this trade.");
                 else if (optionIndex == 2) tradingUI.setInnerThoughts("Seems fair.");
                 else tradingUI.setInnerThoughts("Hmm...");
            }
            @Override
            public void onTradeConfirmed(int itemIndex) {
                 tradingUI.showTradeResult("Traded successfully!");
                 String[] currentInv = new String[]{"Sword", "Shield", "Potion"};
                 if (itemIndex >= 0 && itemIndex < currentInv.length) {
                     currentInv[itemIndex] = "Traded " + currentInv[itemIndex];
                 }
                 hud.setInventory(currentInv);
                 
                 // Restore control
                 Gdx.input.setInputProcessor(inputMux);
                 mouse.ignoreNextDelta();
                 Gdx.input.setCursorCatched(true);
                 
                 Timer.schedule(new Timer.Task() {
                     @Override
                     public void run() {
                         tradingUI.hide();
                     }
                 }, 2f);
            }
            @Override
            public void onTradeCancelled() {
                 tradingUI.hide();
                 // Restore control
                 Gdx.input.setInputProcessor(inputMux);
                 mouse.ignoreNextDelta();
                 Gdx.input.setCursorCatched(true);
            }
            @Override
            public void onTimeUp() {
                 tradingUI.showTradeResult("Too slow!");
                 
                 // Restore control
                 Gdx.input.setInputProcessor(inputMux);
                 mouse.ignoreNextDelta();
                 Gdx.input.setCursorCatched(true);
                 
                 Timer.schedule(new Timer.Task() {
                     @Override
                     public void run() {
                         tradingUI.hide();
                     }
                 }, 2f);
            }
        });

        TextureRegion brickRegion = new TextureRegion(textures.get(BRICK_IMG));
        NpcEntity npc1 = new NpcEntity(
            new Vector3(2, 0, 2), new Vector3(1, 2, 1), new RectangleRenderer(brickRegion, Color.RED),
            "Chinese Merchant", "Chinese", new String[]{"更值錢", "公平", "不值錢"}, new String[]{"Yes", "Hmm", "No"}
        );
        NpcEntity npc2 = new NpcEntity(
            new Vector3(-2, 0, 2), new Vector3(1, 2, 1), new RectangleRenderer(brickRegion, Color.GREEN),
            "Vietnamese Merchant", "Vietnamese", new String[]{"Giá trị hơn", "Công bằng", "Giá trị thấp hơn"}, new String[]{"Yes", "Hmm", "No"}
        );
        NpcEntity npc3 = new NpcEntity(
            new Vector3(0, 0, -2), new Vector3(1, 2, 1), new RectangleRenderer(brickRegion, Color.BLUE),
            "Japanese Merchant", "Japanese", new String[]{"もっと価値がある", "妥当な", "価値が低い"}, new String[]{"Yes", "Hmm", "No"}
        );
        npcs.add(npc1);
        npcs.add(npc2);
        npcs.add(npc3);
        entityManager.add(npc1);
        entityManager.add(npc2);
        entityManager.add(npc3);

        npcTargetingSystem = new NpcTargetingSystem(playerCamera, npcs);

        createLevelLayout();

        keyboard.bind(ButtonBindType.HOLD, Keys.W, new TranslateCameraAction(playerCamera, new Vector3(0, 0, -PLAYER_SPEED)));
        keyboard.bind(ButtonBindType.HOLD, Keys.A, new TranslateCameraAction(playerCamera, new Vector3(-PLAYER_SPEED, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.S, new TranslateCameraAction(playerCamera, new Vector3(0, 0, PLAYER_SPEED)));
        keyboard.bind(ButtonBindType.HOLD, Keys.D, new TranslateCameraAction(playerCamera, new Vector3(PLAYER_SPEED, 0, 0)));

        keyboard.bind(ButtonBindType.DOWN, Keys.ESCAPE, ActionHelper.switchSceneAction(sceneManager, Scenes.MainMenu));
        
        keyboard.bind(ButtonBindType.DOWN, Keys.E, new Action<KeyEvent>() {
            @Override
            public void act(KeyEvent event) {
                NpcEntity target = npcTargetingSystem.getTargetedNpc();
                if (target != null && !tradingUI.isVisible()) {
                    tradingUI.show(target.getName(), target.getDialogueOptions(), new String[]{"Sword", "Shield", "Potion"}, new String[]{"Common", "Rare", "Epic"});
                    Gdx.input.setInputProcessor(tradingUI.getInputProcessor());
                    Gdx.input.setCursorCatched(false);
                }
            }
        });
        
        mouse.bindButton(ButtonBindType.DOWN, Input.Buttons.RIGHT, new Action<MouseManager.MouseButtonEvent>() {
            @Override
            public void act(MouseManager.MouseButtonEvent event) {
                NpcEntity target = npcTargetingSystem.getTargetedNpc();
                if (target != null && !tradingUI.isVisible()) {
                    tradingUI.show(target.getName(), target.getDialogueOptions(), new String[]{"Sword", "Shield", "Potion"}, new String[]{"Common", "Rare", "Epic"});
                    Gdx.input.setInputProcessor(tradingUI.getInputProcessor());
                    Gdx.input.setCursorCatched(false);
                }
            }
        });
        // @formatter:on

        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void unload() {
        super.unload();
        entityManager.removeAll();
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(false);
    }

    @Override
    public void update(float deltaTime) {
        clock.forward(deltaTime);
        if (!tradingUI.isVisible()) {
            keyboard.update(deltaTime, clock.getSeconds());
            mouse.update(deltaTime, clock.getSeconds());
            
            npcTargetingSystem.update();
            NpcEntity targeted = npcTargetingSystem.getTargetedNpc();
            if (targeted != null) {
                hud.showInteractionPrompt(targeted.getName());
            } else {
                hud.hideInteractionPrompt();
            }
        } else {
            hud.hideInteractionPrompt();
        }
        
        entityManager.updateCollisions();
        entityManager.update(deltaTime);
    }

    @Override
    public void render(GraphicsManager graphics, int width, int height) {
        viewport.update(width, height);

        // Render Sky box first
        if (skybox != null) {
            graphics.beginRender(viewport);
            graphics.render3D(skybox, playerCamera.getCamera());
            graphics.endRender();
        }
        
        // Render world entities
        graphics.beginRender(viewport);
        graphics.render3D(
            entityManager.getEntities(),
            playerCamera.getCamera()
        );
        graphics.endRender();

        // Render UI on top
        graphics.beginRender(viewport);
        graphics.render(hud, null);
        graphics.render(tradingUI, null);
        graphics.endRender();
    }
}
