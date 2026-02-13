package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

/** Manages short sound effects. */
public class SoundManager extends ResourceCache<String, Sound> {

    private float volume = 1f;

    /** Get the current volume. */
    public float getVolume() {
        return volume;
    }

    /** Set a new volume. Currently playing sounds are not affected. */
    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
    }

    @Override
    protected Sound load(String path) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    /** Play the given sound file. Returns the sound ID, or -1 if the sound could not be played. */
    public long play(String path) {
        Sound sound = get(path);
        if (sound != null) {
            return sound.play(volume);
        }
        return -1;
    }
}
