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
import com.simpulator.engine.SceneManager;

public class MainGame extends SwitchableScene {

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;
    private EntityManager entityManager;
    private InputMultiplexer inputMux;
    private KeyboardManager km;
    private MouseManager mm;
    private CollidableEntity pushable;
    private CollidableEntity playerEntity;

    public MainGame(SceneManager sceneManager) {
        super(sceneManager);
        entityManager = new EntityManager();
    }

    @Override
    public void load() {
        Gdx.input.setCursorCatched(true);

        playerCamera = new PerspectiveCamera(
            70,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        playerCamera.position.set(0, 0, 0);
        playerCamera.lookAt(0, 0, 0);
        playerCamera.near = 10f;
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

        entityManager.add(pushable);
        entityManager.add(playerEntity);

        km = new KeyboardManager();

        // TODO: move according to rotation
        MovableCamera movableCam = new MovableCamera(playerCamera);
        // @formatter:off
        km.bind(ButtonBindType.HOLD, Keys.W, new MoveAction(movableCam, new Vector3(0, 0, -100)));
        km.bind(ButtonBindType.HOLD, Keys.A, new MoveAction(movableCam, new Vector3(-100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.S, new MoveAction(movableCam, new Vector3(0, 0, 100)));
        km.bind(ButtonBindType.HOLD, Keys.D, new MoveAction(movableCam, new Vector3(100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.SHIFT_LEFT, new MoveAction(movableCam, new Vector3(0, -100, 0)));
        km.bind(ButtonBindType.HOLD, Keys.SPACE, new MoveAction(movableCam, new Vector3(0, 100, 0)));

        km.bind(ButtonBindType.HOLD, Keys.R, new MoveAction(pushable, new Vector3(1, 0, 1), 4));
        km.bind(ButtonBindType.HOLD, Keys.LEFT, new MoveAction(playerEntity, new Vector3(-100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.RIGHT, new MoveAction(playerEntity, new Vector3(100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.UP, new MoveAction(playerEntity, new Vector3(0, 0, -100)));
        km.bind(ButtonBindType.HOLD, Keys.DOWN, new MoveAction(playerEntity, new Vector3(0, 0, 100)));

        km.bind(ButtonBindType.DOWN, Keys.ESCAPE, switchSceneAction(Scenes.MainMenu));
        // @formatter:on

        mm = new MouseManager();
        mm.bindMove(new FirstPersonCameraAction(playerCamera, 0.15f));

        // TODO: shoot droplets on mouse click. dropplets will delete entities and make sound on contact
        inputMux = new InputMultiplexer();
        inputMux.addProcessor(km);
        inputMux.addProcessor(mm);
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
        km.update(deltaTime, clock.getSeconds());
        mm.update(deltaTime, clock.getSeconds());
        entityManager.update(deltaTime);

        Vector3 mtv = new Vector3().setZero();
        if (pushable.intersects(playerEntity, mtv)) {
            pushable.translate(mtv);
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
