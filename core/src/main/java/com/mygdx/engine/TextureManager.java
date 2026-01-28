package com.mygdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;

public class TextureManager extends Cache<String, Texture> {

    public TextureManager() {
        super();
    }

    @Override
    protected Texture load(String path) {
        return new Texture(Gdx.files.internal(path));
    }
}
