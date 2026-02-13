package com.simpulator.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

public class SoundManager extends ResourceCache<String, Sound> {

    private float volume = 1f;
    private Music backgroundMusic;

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(this.volume);
        }
    }

    public void setBgm(Music bgm) {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            if (backgroundMusic != bgm) {
                backgroundMusic.dispose();
            }
        }
        backgroundMusic = bgm;
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(volume);
    }

    public void setBgm(String path) {
        Music bgm = Gdx.audio.newMusic(Gdx.files.internal(path));
        setBgm(bgm);
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

    public boolean isBgmPlaying() {
        return backgroundMusic != null && backgroundMusic.isPlaying();
    }

    public void startBgm() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.setVolume(1f);
            backgroundMusic.play();
        }
    }

    public void pauseBgm() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    public void stopBgm() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }
}
