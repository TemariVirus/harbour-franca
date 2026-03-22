package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import java.util.ArrayList;

public abstract class UIElement {

    private UILayout layout;
    private Rect bounds;
    private ArrayList<UIElement> children = new ArrayList<>();

    protected UIElement(UILayout layout) {
        this.layout = layout;
        this.bounds = new Rect(0, 0, 0, 0);
    }

    protected Rect getBounds() {
        return bounds;
    }

    public UIElement addChild(UIElement child) {
        children.add(child);
        return this;
    }

    public void removeChild(UIElement child) {
        children.remove(child);
    }

    public void insertChild(int index, UIElement child) {
        children.add(index, child);
    }

    public void resize(Rect parentBounds) {
        Rect oldBounds = bounds;
        bounds = layout.computeBounds(parentBounds);
        if (bounds.equals(oldBounds)) {
            return;
        }
        for (UIElement child : children) {
            child.resize(bounds);
        }
    }

    protected Iterable<UIElement> getChildrenReversed() {
        return children.reversed();
    }

    public abstract void render(TextureBatch batch);

    public boolean handleMouseButtonEvent(MouseButtonEvent event) {
        return false;
    }
}
