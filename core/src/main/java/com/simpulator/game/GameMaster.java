package com.simpulator.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityManager;
import com.simpulator.engine.GraphicsManager;
import com.simpulator.engine.KeyboardManager;
import com.simpulator.engine.KeyboardManager.BindType;
import com.simpulator.engine.TextureManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {

    private final Clock clock = new Clock(0);
    private PerspectiveCamera playerCamera;
    private GraphicsManager gm;
    private TextureManager tm;
    private KeyboardManager km;
    private EntityManager em;
    private CollidableEntity pushable;
    private CollidableEntity playerEntity;

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

        gm = new GraphicsManager();
        tm = new TextureManager();
        em = new EntityManager();
        pushable = new CollidableEntity(
            new Vector3(100, 100, -200),
            new Vector2(200, 100),
            0,
            new Quaternion().idt(),
            // new Quaternion().setFromAxisRad(1, 0, 1, 1),
            tm.get("libgdx.png")
        );
        playerEntity = new CollidableEntity(
            new Vector3(0, 0, -200),
            new Vector2(200, 200),
            10,
            new Quaternion().idt(),
            tm.get("libgdx.png")
        );
        em.add(pushable);
        em.add(playerEntity);

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
                pushable,
                new Vector3(1, 0, 1),
                4
            )
        );

        km.bind(
            BindType.HOLD,
            Keys.LEFT,
            new MoveAction<KeyboardManager.KeyEvent>(
                playerEntity,
                new Vector3(-100, 0, 0)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.RIGHT,
            new MoveAction<KeyboardManager.KeyEvent>(
                playerEntity,
                new Vector3(100, 0, 0)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.UP,
            new MoveAction<KeyboardManager.KeyEvent>(
                playerEntity,
                new Vector3(0, 0, -100)
            )
        );
        km.bind(
            BindType.HOLD,
            Keys.DOWN,
            new MoveAction<KeyboardManager.KeyEvent>(
                playerEntity,
                new Vector3(0, 0, 100)
            )
        );

        Gdx.input.setInputProcessor(km);
    }

    @Override
    public void render() {
        clock.forward(Gdx.graphics.getDeltaTime());
        km.update(Gdx.graphics.getDeltaTime(), clock.getSeconds());
        em.update(Gdx.graphics.getDeltaTime());

        Vector3 mtv = new Vector3().setZero();
        System.out.printf(
            "%b %b (%f, %f, %f)\n",
            pushable.intersects(playerEntity),
            pushable.intersects(playerEntity, mtv),
            mtv.x,
            mtv.y,
            mtv.z
        );
        if (pushable.intersects(playerEntity, mtv)) {
            // pushable.translate(mtv);
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        gm.renderEntities(
            em.getEntities().toArray(new Entity[0]),
            playerCamera
        );
        gm.endRender();
    }

    @Override
    public void dispose() {
        gm.dispose();
        tm.dispose();
    }
}
