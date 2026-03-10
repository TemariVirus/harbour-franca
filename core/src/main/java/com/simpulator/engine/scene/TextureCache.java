package com.simpulator.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/** Manages loading and unloading of textures. */
public class TextureCache extends ResourceCache<String, Texture> {

    @Override
    protected Texture load(String path) {
        return new Texture(Gdx.files.internal(path));
    }
}
