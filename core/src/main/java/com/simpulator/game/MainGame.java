package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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
import com.simpulator.engine.SceneManager;

public class MainGame extends SwitchableScene {

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;
    private EntityManager entityManager;
    private EntityRemover entityRemover;
    private InputMultiplexer inputMux;
    private KeyboardManager keyboard;
    private MouseManager mouse;
    private CollidableEntity pushable;
    private CollidableEntity playerEntity;
    private RoamingEntity enemy;

    public MainGame(SceneManager sceneManager) {
        super(sceneManager);
        entityManager = new EntityManager();
    }

    @Override
    public void load() {
        Gdx.input.setCursorCatched(true);
        entityRemover = new EntityRemover(entityManager);

        playerCamera = new PerspectiveCamera(
            70,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        playerCamera.position.set(0, 0, 0);
        playerCamera.lookAt(0, 0, -1);
        playerCamera.near = 50f;
        playerCamera.far = 3000f;
        playerCamera.update();

        pushable = new CollidableEntity(
            new Vector3(100, 100, -200),
            new Vector2(200, 100),
            0,
            new Quaternion().idt(),
            textures.get("libgdx.png")
        );

        playerEntity = new CollidableEntity(
            new Vector3(0, 0, -200),
            new Vector2(200, 200),
            10,
            new Quaternion().idt(),
            textures.get("libgdx.png")
        );

        enemy = new RoamingEntity(new Vector3(-200, 0, -100), new Vector2(100, 100), 10, textures.get("libgdx.png"));
        enemy.setTint(com.badlogic.gdx.graphics.Color.RED); 
        entityManager.add(enemy);

        entityManager.add(pushable);
        entityManager.add(playerEntity);

        keyboard = new KeyboardManager();

        // @formatter:off
        keyboard.bind(ButtonBindType.HOLD, Keys.W, new TranslateCameraAction(playerCamera, new Vector3(0, 0, -100)));
        keyboard.bind(ButtonBindType.HOLD, Keys.A, new TranslateCameraAction(playerCamera, new Vector3(-100, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.S, new TranslateCameraAction(playerCamera, new Vector3(0, 0, 100)));
        keyboard.bind(ButtonBindType.HOLD, Keys.D, new TranslateCameraAction(playerCamera, new Vector3(100, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.SHIFT_LEFT, new TranslateCameraAction(playerCamera, new Vector3(0, -100, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.SPACE, new TranslateCameraAction(playerCamera, new Vector3(0, 100, 0)));

        keyboard.bind(ButtonBindType.HOLD, Keys.R, new MoveAction(pushable, new Vector3(1, 0, 1), 4));
        keyboard.bind(ButtonBindType.HOLD, Keys.LEFT, new MoveAction(playerEntity, new Vector3(-100, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.RIGHT, new MoveAction(playerEntity, new Vector3(100, 0, 0)));
        keyboard.bind(ButtonBindType.HOLD, Keys.UP, new MoveAction(playerEntity, new Vector3(0, 0, -100)));
        keyboard.bind(ButtonBindType.HOLD, Keys.DOWN, new MoveAction(playerEntity, new Vector3(0, 0, 100)));

        keyboard.bind(ButtonBindType.DOWN, Keys.ESCAPE, switchSceneAction(Scenes.MainMenu));
        // @formatter:on

        mouse = new MouseManager();
        // TODO: add sensitivity to settings
        mouse.bindMove(new RotateCameraAction(playerCamera, 0.15f));
        mouse.bindButton(
            ButtonBindType.HOLD,
            Buttons.LEFT,
            new SpawnBulletAction(
                entityRemover,
                textures.get("droplet.png"),
                sounds.get("pop.mp3"),
                playerCamera,
                0.5f,
                500f
            )
        );

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
        entityManager.update(deltaTime);
        entityRemover.update();

        if (
            entityManager.getEntities().contains(pushable) &&
            entityManager.getEntities().contains(playerEntity)
        ) {
            Vector3 mtv = new Vector3();
            if (pushable.intersects(playerEntity, mtv)) {
                pushable.translate(mtv);
            }
            Vector3 mtvEnemy = new Vector3().setZero();
            if (playerEntity.intersects(enemy, mtvEnemy)) {
                playerEntity.translate(mtvEnemy);

                sounds.get("pop.mp3").play();
            }
        }
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        graphics.render(
            entityManager.getEntities().toArray(new Entity[0]),
            playerCamera
        );
        graphics.endRender();
    }
}
