package com.simpulator.engine.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.PointerEvent;
import java.util.ArrayList;

/** The root of the UI tree. */
public class UIRoot extends UIElement implements Renderable {

    public UIRoot() {
        super(
            new UILayout() {
                @Override
                public Rect computeBounds(Rect parentBounds) {
                    return parentBounds;
                }
            }
        );
    }

    /** Resizes the root to fill the viewport. */
    public void updateBounds(Viewport viewport) {
        updateBounds(
            new Rect(
                0,
                0,
                (int) Math.ceil(viewport.getWorldWidth()),
                (int) Math.ceil(viewport.getWorldHeight())
            )
        );
    }

    @Override
    public void render(TextureBatch batch) {
        UIWalker walker = new UIWalker();
        walker.push(this);

        while (!walker.isEmpty()) {
            UIElement element = walker.pop();
            element.render(batch);
            walker.push(element);
        }
    }

    @Override
    public <T> boolean handleEvent(T event) {
        if (super.handleEvent(event)) {
            return true;
        }

        UIWalker walker = new UIWalker();
        walker.push(this);
        while (!walker.isEmpty()) {
            UIElement element = walker.pop();
            if (event instanceof PointerEvent) {
                PointerEvent e = (PointerEvent) event;
                if (!element.getBounds().contains(e.x, e.y)) {
                    walker.push(element); // Children may be outside of parent bounds
                    continue;
                }
            }
            if (!element.handleEvent(event)) {
                // Parent did not handle event, pass it to children
                walker.push(element);
            }
        }

        return true;
    }

    @Override
    public boolean isVisible(Camera camera) {
        return true;
    }

    @Override
    public float getZOrder(Camera camera) {
        return -1;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        render(batch);
    }
}

final class UIWalker {

    ArrayList<UIElement> stack = new ArrayList<>();

    public boolean isEmpty() {
        return stack.size() == 0;
    }

    public void push(UIElement element) {
        for (UIElement child : element.getChildrenReversed()) {
            stack.add(child);
        }
    }

    public UIElement pop() {
        return stack.removeLast();
    }
}
