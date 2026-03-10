package com.simpulator.game;

import com.simpulator.engine.scene.SoundManager;

public class SoundPlayer {

    private SoundManager sounds;
    private String soundPath;

    public SoundPlayer(SoundManager sounds, String soundPath) {
        this.sounds = sounds;
        this.soundPath = soundPath;
    }

    public void play() {
        sounds.play(soundPath);
    }
}
