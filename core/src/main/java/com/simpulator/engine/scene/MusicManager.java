package com.simpulator.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashSet;
import java.util.Set;

/** Manages streaming music files. */
public class MusicManager implements Disposable {

    private class MusicCache extends ResourceCache<String, Music> {

        @Override
        protected Music load(String path) {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(path));
            music.setLooping(true);
            music.setVolume(volume);
            return music;
        }

        @Override
        public void dispose() {
            for (Music music : super.values()) {
                music.stop();
            }
            super.dispose();
        }
    }

    private final MusicCache musicCache = new MusicCache();
    private final Set<String> playingMusics = new HashSet<>();
    private float volume = 1f;

    /** Get the current volume. */
    public float getVolume() {
        return volume;
    }

    /** Set a new volume. Currently playing musics are affected as well. */
    public void setVolume(float volume) {
        this.volume = MathUtils.clamp(volume, 0f, 1f);
        for (Music music : musicCache.values()) {
            music.setVolume(this.volume);
        }
    }

    /** Returns whether the given file is playing. */
    public boolean isMusicPlaying(String path) {
        Music music = musicCache.get(path);
        return music.isPlaying();
    }

    /** Start playing the given file. */
    public void startMusic(String path) {
        Music music = musicCache.get(path);
        if (!music.isPlaying()) {
            music.setVolume(volume);
            music.play();
            playingMusics.add(path);
        }
    }

    /** Pause playback of the given file. startMusic() will resume at the current position. */
    public void pauseMusic(String path) {
        Music music = musicCache.get(path);
        if (music.isPlaying()) {
            music.pause();
            playingMusics.remove(path);
        }
    }

    /** Stop playback of the given file. startMusic() will start from the beginning. */
    public void stopMusic(String path) {
        Music music = musicCache.get(path);
        if (music.isPlaying()) {
            music.stop();
            playingMusics.remove(path);
        }
    }

    /** Stop playback of all currently playing music files. startMusic() will start from the beginning. */
    public void stopAllMusic() {
        for (String path : playingMusics) {
            Music music = musicCache.get(path);
            if (music.isPlaying()) {
                music.stop();
            }
        }
        playingMusics.clear();
    }

    @Override
    public void dispose() {
        musicCache.dispose();
    }
}
