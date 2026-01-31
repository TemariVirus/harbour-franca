package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.Clock;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.KeyboardManager.BindType;
import com.simpulator.engine.MoveAction;
import com.simpulator.engine.TextureManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;
    private SpriteBatch batch;
    private TextureManager tm;
    private KeyboardManager km;
    private CollidableEntity entity1;
    private CollidableEntity entity2;

    @Override
    public void create() {
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

        batch = new SpriteBatch();
        tm = new TextureManager();
        entity1 = new CollidableEntity(
            new Vector3(100, 100, -200),
            new Vector2(200, 100),
            0,
            new Quaternion().idt(),
            // new Quaternion().setFromAxisRad(1, 0, 1, 1),
            tm.get("libgdx.png")
        );
        entity2 = new CollidableEntity(
            new Vector3(0, 0, -200),
            new Vector2(200, 200),
            2,
            new Quaternion().idt(),
            tm.get("libgdx.png")
        );

        km = new KeyboardManager();
        km.bind(
            BindType.HOLD,
            Keys.W,
            new MoveCameraAction(playerCamera, new Vector3(0, 0, -100))
        );
        km.bind(
            BindType.HOLD,
            Keys.A,
            new MoveCameraAction(playerCamera, new Vector3(-100, 0, 0))
        );
        km.bind(
            BindType.HOLD,
            Keys.S,
            new MoveCameraAction(playerCamera, new Vector3(0, 0, 100))
        );
        km.bind(
            BindType.HOLD,
            Keys.D,
            new MoveCameraAction(playerCamera, new Vector3(100, 0, 0))
        );
        km.bind(
            BindType.HOLD,
            Keys.SHIFT_LEFT,
            new MoveCameraAction(playerCamera, new Vector3(0, -100, 0))
        );
        km.bind(
            BindType.HOLD,
            Keys.SPACE,
            new MoveCameraAction(playerCamera, new Vector3(0, 100, 0))
        );

        km.bind(
            BindType.HOLD,
            Keys.R,
            new MoveAction<KeyboardManager.KeyEvent>(
                entity1,
                new Vector3(1, 0, 1),
                4
            )
        );

        km.bind(
            BindType.HOLD,
            Keys.LEFT,
            new MoveAction<KeyboardManager.KeyEvent>(
                entity2,
                new Vector3(-100, 0, 0)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.RIGHT,
            new MoveAction<KeyboardManager.KeyEvent>(
                entity2,
                new Vector3(100, 0, 0)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.UP,
            new MoveAction<KeyboardManager.KeyEvent>(
                entity2,
                new Vector3(0, 0, -100)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.DOWN,
            new MoveAction<KeyboardManager.KeyEvent>(
                entity2,
                new Vector3(0, 0, 100)
            )
        );

        Gdx.input.setInputProcessor(km);
    }

    @Override
    public void render() {
        clock.forward(Gdx.graphics.getDeltaTime());
        km.update(Gdx.graphics.getDeltaTime(), clock.getSeconds());

        System.out.println(entity1.intersects(entity2));

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        entity1.render(batch, playerCamera);
        entity2.render(batch, playerCamera);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tm.dispose();
    }
}
