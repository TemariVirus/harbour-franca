package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;
import com.simpulator.engine.ui.UIRelativeLayout;

public class Timer extends UIElement {

    private float timeLeft;
    private boolean isRunning = true;
    private Text text;

    public Timer(
        BitmapFont font,
        Color color,
        float initialTime,
        UILayout layout
    ) {
        super(layout);
        this.timeLeft = initialTime;
        this.text = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            color,
            new UIRelativeLayout()
        );
        addChild(text);
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void restart(float time) {
        this.timeLeft = time;
        this.isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public boolean isFinished() {
        return timeLeft <= 0;
    }

    @Override
    public void update(float deltaTime) {
        if (isRunning()) {
            timeLeft -= deltaTime;
            timeLeft = Math.max(timeLeft, 0);

            int m = (int) (timeLeft / 60);
            int s = (int) (timeLeft % 60);
            text.setText(String.format("%d:%02d", m, s));
        }
        if (isFinished()) {
            setRunning(false);
        }
    }

    @Override
    public void render(TextureBatch batch) {
        // TODO: background
    }
}
