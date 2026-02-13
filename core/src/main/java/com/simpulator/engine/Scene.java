package com.simpulator.engine;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public abstract class Scene {

    protected EntityManager entityManager;
    protected SoundManager sounds;
    protected TextureManager textures;

    public Scene() {
        entityManager = new EntityManager();
        sounds = new SoundManager();
        textures = new TextureManager();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Sound getSound(String path) {
        return sounds.get(path);
    }

    public Texture getTexture(String path) {
        return textures.get(path);
    }

    public abstract void load();

    public void unload() {
        // These fields can be safely reused in the next load()
        entityManager.removeAll();
        sounds.dispose();
        textures.dispose();
    }

    public void update(float deltaTime) {
        entityManager.update(deltaTime);
    }

    public long playSound(String path) {
        return sounds.play(path);
    }

    public abstract void render(GraphicsManager graphics);
}
