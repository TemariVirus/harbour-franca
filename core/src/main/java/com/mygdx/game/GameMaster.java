package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.engine.Entity;
import com.mygdx.engine.IAction;
import com.mygdx.engine.KeyboardManager;
import com.mygdx.engine.KeyboardManager.BindType;
import com.mygdx.engine.MovementAction;
import com.mygdx.engine.TextureManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private TextureManager tm;
    private Entity entity;

    private KeyboardManager km;
    private IAction moveLeft;
    private IAction moveRight;

    @Override
    public void create() {
        batch = new SpriteBatch();
        tm = new TextureManager();
        entity = new Entity(100, 100, 100, 100, tm.get("libgdx.png"));

        km = new KeyboardManager();
        moveLeft = new MovementAction(entity, -100, -20, 500);
        km.bind(BindType.DOWN, Keys.LEFT, moveLeft);
        km.bind(BindType.UP, Keys.LEFT, moveLeft);
        moveRight = new MovementAction(entity, 20, 100, 50);
        km.bind(BindType.HOLD, Keys.RIGHT, moveRight);

        Gdx.input.setInputProcessor(km);
    }

    @Override
    public void render() {
        km.update(Gdx.graphics.getDeltaTime());

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        entity.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tm.dispose();
    }
}
