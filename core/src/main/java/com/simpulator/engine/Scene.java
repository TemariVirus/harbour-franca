package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;


public class Scene {
    private Camera camera;
    protected EntityManager entityManager;
    protected SoundManager sounds;
    protected TextureManager textures;

    public Scene() {
        this.entityManager = new EntityManager();
        this.sounds = new SoundManager();
        this.textures = new TextureManager();
    }

    public void load() {

    }


    public void unload() {
        sounds.dispose();
        textures.dispose();
    }

    public Sound getSound(String path) {
        return sounds.get(path);
    }

    public Texture getTexture(String path) {
        return textures.get(path);
    }


    public void update(float deltaTime) {
        entityManager.update(deltaTime);
    }


    public void render(GraphicsManager graphics) {

    }
}