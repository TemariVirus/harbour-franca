package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager extends ResourceCache<String, Sound> {

    public SoundManager() {
        super();
    }

    @Override
    protected Sound load(String path) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }
}
