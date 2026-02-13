package com.simpulator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.Action;
import com.simpulator.engine.ButtonManager.ButtonBindType;
import com.simpulator.engine.Entity;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.Scene;
import com.simpulator.engine.SceneManager;

public class MainGame extends Scene {

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;
    private KeyboardManager km;
    private CollidableEntity pushable;
    private CollidableEntity playerEntity;
    private SceneManager sceneManager;

    public MainGame(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void load() {
        // Cam
        playerCamera = new PerspectiveCamera(
            70,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        playerCamera.position.set(0, 0, 0);
        playerCamera.lookAt(0, 0, 0);
        playerCamera.near = 1f;
        playerCamera.far = 300f;
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

        km.bind(ButtonBindType.HOLD, Keys.W, new MoveCameraAction(playerCamera, new Vector3(0, 0, -100)));
        km.bind(ButtonBindType.HOLD, Keys.A, new MoveCameraAction(playerCamera, new Vector3(-100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.S, new MoveCameraAction(playerCamera, new Vector3(0, 0, 100)));
        km.bind(ButtonBindType.HOLD, Keys.D, new MoveCameraAction(playerCamera, new Vector3(100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.SHIFT_LEFT, new MoveCameraAction(playerCamera, new Vector3(0, -100, 0)));
        km.bind(ButtonBindType.HOLD, Keys.SPACE, new MoveCameraAction(playerCamera, new Vector3(0, 100, 0)));

        km.bind(ButtonBindType.HOLD, Keys.R, new MoveAction<KeyboardManager.KeyEvent>(pushable, new Vector3(1, 0, 1), 4));
        km.bind(ButtonBindType.HOLD, Keys.LEFT, new MoveAction<KeyboardManager.KeyEvent>(playerEntity, new Vector3(-100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.RIGHT, new MoveAction<KeyboardManager.KeyEvent>(playerEntity, new Vector3(100, 0, 0)));
        km.bind(ButtonBindType.HOLD, Keys.UP, new MoveAction<KeyboardManager.KeyEvent>(playerEntity, new Vector3(0, 0, -100)));
        km.bind(ButtonBindType.HOLD, Keys.DOWN, new MoveAction<KeyboardManager.KeyEvent>(playerEntity, new Vector3(0, 0, 100)));

        km.bind(
            ButtonBindType.DOWN,
            Keys.ESCAPE,
            new Action<KeyboardManager.KeyEvent>() {
                @Override
                public void act(
                    float deltaTime,
                    KeyboardManager.KeyEvent extraData
                ) {
                    sceneManager.switchScene("MainMenu");
                }
            }
        );

        Gdx.input.setInputProcessor(km);
    }

    @Override
    public void update(float deltaTime) {
        clock.forward(deltaTime);
        km.update(deltaTime, clock.getSeconds());
        entityManager.update(deltaTime);

        Vector3 mtv = new Vector3().setZero();
        if (pushable.intersects(playerEntity, mtv)) {
            pushable.translate(mtv);
        }
    }

    @Override
    public void render(GraphicsManager graphics) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        graphics.renderEntities(
            entityManager.getEntities().toArray(new Entity[0]),
            playerCamera
        );
        graphics.endRender();
    }
}
