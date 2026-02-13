package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class SoundManager extends ResourceCache<String, Sound> {

    private float volume = 1f;

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
    }

    @Override
    protected Sound load(String path) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    public long play(String path) {
        Sound sound = get(path);
        if (sound != null) {
            return sound.play(volume);
        }
        return -1;
    }
}
