package com.simpulator.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.Action;
import com.simpulator.engine.input.MouseManager.MouseButton;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import com.simpulator.engine.input.MouseManager.MouseMoveEvent;
import com.simpulator.engine.ui.Rect;
import com.simpulator.engine.ui.UIElement;
import com.simpulator.engine.ui.UILayout;
import java.util.ArrayList;

public class Slider extends UIElement {

    private static final Texture texture = UiHelper.getWhiteTexture();

    private float value;
    private float min;
    private float max;
    private float step;
    private ArrayList<Action<Float>> valueListeners = new ArrayList<>();

    private Color backgroundColor;
    private float backGroundHeight;
    private Color knobColor;
    private float knobSize;
    private boolean isDragging = false;

    public Slider(
        float min,
        float max,
        float step,
        Color backgroundColor,
        float backGroundHeight,
        Color knobColor,
        float knobSize,
        UILayout layout
    ) {
        super(layout);
        this.value = max;
        this.min = min;
        this.max = max;
        this.step = step;

        this.backgroundColor = backgroundColor;
        this.backGroundHeight = backGroundHeight;
        this.knobColor = knobColor;
        this.knobSize = knobSize;

        addListener(MouseButtonEvent.class, e -> {
            if (e.button != MouseButton.LEFT.code()) {
                return false;
            }
            switch (e.type) {
                case DOWN:
                    if (getBounds().contains(e.x, e.y)) {
                        isDragging = true;
                        return true;
                    }
                    break;
                case UP:
                    isDragging = false;
                    break;
                default:
                    break;
            }
            return false;
        });
        addListener(MouseMoveEvent.class, e -> {
            if (!isDragging) {
                return false;
            }

            Rect bounds = getBounds();
            float t =
                (float) (e.x - bounds.left) /
                (float) (bounds.right - bounds.left);
            float newValue = Math.clamp(min + t * (max - min), min, max);
            if (step > 0) {
                newValue = Math.round(newValue / step) * step;
            }
            setValue(newValue, true);
            return false;
        });
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value, boolean notifyListeners) {
        this.value = Math.clamp(value, min, max);
        if (notifyListeners) {
            for (Action<Float> action : valueListeners) {
                action.act(this.value);
            }
        }
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        setValue(value, true);
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        setValue(value, true);
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setBackGroundHeight(float height) {
        this.backGroundHeight = height;
    }

    public void setKnobColor(Color color) {
        this.knobColor = color;
    }

    public void setKnobSize(float size) {
        this.knobSize = size;
    }

    public void addValueListener(Action<Float> action) {
        valueListeners.add(action);
    }

    @Override
    public void render(TextureBatch batch) {
        Rect bounds = getBounds();
        int width = bounds.right - bounds.left;
        int height = bounds.bottom - bounds.top;

        if (width > 0 && backGroundHeight > 0) {
            batch.setColor(backgroundColor);
            batch.draw(
                texture,
                bounds.left,
                bounds.top + (height - backGroundHeight) / 2,
                width,
                backGroundHeight
            );
        }
        if (knobSize > 0) {
            float t = (value - min) / (max - min);
            float knobX = bounds.left + t * width;
            batch.setColor(knobColor);
            batch.draw(
                texture,
                knobX - knobSize / 2,
                bounds.top + (height - knobSize) / 2,
                knobSize,
                knobSize
            );
        }
        batch.setColor(Color.WHITE);
    }
}
