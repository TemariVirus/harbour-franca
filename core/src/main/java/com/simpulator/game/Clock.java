package com.simpulator.game;

public class Clock {

    private float seconds = 0;

    public Clock(float seconds) {
        this.seconds = seconds;
    }

    public float getSeconds() {
        return seconds;
    }

    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    public void reset() {
        this.seconds = 0;
    }

    public void forward(float seconds) {
        // Ensure monoticity
        if (seconds <= 0) {
            return;
        }
        this.seconds += seconds;
    }

    public void backward(float seconds) {
        // Ensure monoticity
        if (seconds <= 0) {
            return;
        }
        this.seconds -= seconds;
    }
}
