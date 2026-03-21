package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import java.util.ArrayList;

public abstract class UIElement {

    private UILayout layout;
    private ArrayList<UIElement> children = new ArrayList<>();

    protected UIElement(UILayout layout) {
        this.layout = layout;
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

    public Rect getBounds(Rect parentBounds) {
        return layout.computeBounds(parentBounds);
    }

    public Iterable<UIElement> getChildrenReversed() {
        return children.reversed();
    }

    public abstract void render(TextureBatch batch, Rect bounds);

    public boolean handleMouseButtonEvent(MouseButtonEvent event) {
        return false;
    }
}
