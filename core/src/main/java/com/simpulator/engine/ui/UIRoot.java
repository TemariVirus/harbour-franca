package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import java.util.ArrayList;

public class UIRoot extends UIElement {

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
    public boolean handleMouseButtonEvent(MouseButtonEvent event) {
        UIWalker walker = new UIWalker();
        walker.push(this);

        while (!walker.isEmpty()) {
            UIElement element = walker.pop();
            if (!element.getBounds().contains(event.x, event.y)) {
                // Assume that children are fully contained within their parent
                continue;
            }
            if (!element.handleMouseButtonEvent(event)) {
                // Parent did not handle event, pass it to children
                walker.push(element);
            }
        }

        return true;
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
