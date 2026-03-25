package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;

public class Timer extends UIElement {

    private static final Texture background = UiHelper.getWhiteTexture();

    private float timeLeft;
    private boolean isRunning = true;

    private Color backgroundColor;
    private Text text;

    public Timer(
        float initialTime,
        Color backgroundColor,
        UILayout layout,
        Text text
    ) {
        super(layout);
        this.timeLeft = initialTime;
        this.backgroundColor = backgroundColor;
        this.text = text;
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
        Rect bounds = getBounds();
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        if (width > 0 && height > 0) {
            batch.setColor(backgroundColor);
            batch.draw(background, bounds.left, bounds.top, width, height);
            batch.setColor(Color.WHITE);
        }
    }
}
