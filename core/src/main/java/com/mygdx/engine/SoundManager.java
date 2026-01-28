package com.mygdx.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

public class SoundManager extends Cache<String, Sound> {

    public SoundManager() {
        super();
    }

    @Override
    protected Sound load(String path) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }
}
