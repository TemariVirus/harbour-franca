package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.MouseManager;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public class ExploreScene extends Scene {

    private static final String BRICK_IMG = "brick.png";
    private static final float PLAYER_SPEED = 3f;

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;

    private SceneManager sceneManager;
    private EntityManager entityManager;
    private InputMultiplexer inputMux;
    private KeyboardManager keyboard;
    private MouseManager mouse;

    public ExploreScene(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.entityManager = new EntityManager();
    }

    private void createLevelLayout() {
        final int WIDTH = 5;
        for (int y = 0; y < 3; y++) {
            for (int i = -WIDTH; i <= WIDTH; i++) {
                entityManager.add(
                    new CuboidEntity(
                        new Vector3(i, y, WIDTH + 0.5f),
                        new Vector2(1, 1),
                        1,
                        new Quaternion().setFromAxis(Vector3.Y, 0),
                        textures.get(BRICK_IMG),
                        false
                    )
                );
                entityManager.add(
                    new CuboidEntity(
                        new Vector3(-i, y, -WIDTH - 0.5f),
                        new Vector2(1, 1),
                        1,
                        new Quaternion().setFromAxis(Vector3.Y, 0),
                        textures.get(BRICK_IMG),
                        false
                    )
                );
                entityManager.add(
                    new CuboidEntity(
                        new Vector3(WIDTH + 0.5f, y, i),
                        new Vector2(1, 1),
                        1,
                        new Quaternion().setFromAxis(Vector3.Y, 90),
                        textures.get(BRICK_IMG),
                        false
                    )
                );
                entityManager.add(
                    new CuboidEntity(
                        new Vector3(-WIDTH - 0.5f, y, -i),
                        new Vector2(1, 1),
                        1,
                        new Quaternion().setFromAxis(Vector3.Y, 90),
                        textures.get(BRICK_IMG),
                        false
                    )
                );
            }
        }
    }

    @Override
    public void load() {
        Gdx.input.setCursorCatched(true);

        playerCamera = new PerspectiveCamera(
            70,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        playerCamera.position.set(0, 1, 0);
        playerCamera.lookAt(0, 0, 1);
        playerCamera.near = 0.05f;
        playerCamera.far = 100f;
        playerCamera.update();

        sounds.setVolume(Config.volume * 0.01f);

        createLevelLayout();

        keyboard = new KeyboardManager();
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

        graphics.render(
            entityManager.getEntities().toArray(new Entity[0]),
            playerCamera
        );
        graphics.endRender();
    }
}
