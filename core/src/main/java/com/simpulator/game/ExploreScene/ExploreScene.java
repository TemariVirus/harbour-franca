package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
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

public class ExploreScene extends Scene {

    private static final String BRICK_IMG = "Oran.jpeg";
    private static final float PLAYER_SPEED = 4f;

    private final Clock clock = new Clock(0);
    private CameraEntity playerCamera;

    private SceneManager sceneManager;
    private EntityManager entityManager;
    private InputMultiplexer inputMux;
    private KeyboardManager keyboard;
    private MouseManager mouse;

    private Level currentLevel;
    private Skybox skybox;

    public ExploreScene(SceneManager sceneManager, Level level) {
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

        keyboard = new KeyboardManager();
        createLevelLayout();
        
        // @formatter:off
        keyboard.bind(ButtonBindType.HOLD, Keys.W, new TranslateCameraAction(playerCamera, new Vector3(0, 0, -PLAYER_SPEED)));
        keyboard.bind(ButtonBindType.HOLD, Keys.A, new TranslateCameraAction(playerCamera, new Vector3(-PLAYER_SPEED, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.S, new TranslateCameraAction(playerCamera, new Vector3(0, 0, PLAYER_SPEED)));
        keyboard.bind(ButtonBindType.HOLD, Keys.D, new TranslateCameraAction(playerCamera, new Vector3(PLAYER_SPEED, 0, 0)));

        keyboard.bind(ButtonBindType.DOWN, Keys.ESCAPE, ActionHelper.switchSceneAction(sceneManager, Scenes.MainMenu));
        // @formatter:on

        mouse = new MouseManager();
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));

        inputMux = new InputMultiplexer();
        inputMux.addProcessor(keyboard);
        inputMux.addProcessor(mouse);
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
        keyboard.update(deltaTime, clock.getSeconds());
        mouse.update(deltaTime, clock.getSeconds());
        entityManager.updateCollisions();
        entityManager.update(deltaTime);
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Render Sky box first
        if (skybox != null) {
            graphics.render3D(skybox, playerCamera.getCamera());
            graphics.endRender();
        }

        // Render world entities
        graphics.render3D(
            entityManager.getEntities(),
            playerCamera.getCamera()
        );
        graphics.endRender();
    }
}