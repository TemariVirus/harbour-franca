package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureManager extends ResourceCache<String, Texture> {

    @Override
    protected Texture load(String path) {
        return new Texture(Gdx.files.internal(path));
    }
}
