package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class SoundManager extends ResourceCache<String, Sound> {

    private static float globalVolume = 1.0f;
    
    public static Music backgroundMusic;

    @Override
    protected Sound load(String path) {
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    public static void setGlobalVolume(float volume) {
        globalVolume = MathUtils.clamp(volume, 0f, 1f);
        
        // Update the music volume 
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(globalVolume);
        }
    }

    public static float getGlobalVolume() {
        return globalVolume;
    }

    public long play(String path) {
        Sound sound = get(path);
        if (sound != null) {
            return sound.play(globalVolume);
        }
        return -1;
    }
}