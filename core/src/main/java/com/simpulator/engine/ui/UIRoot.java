package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.input.MouseManager.MouseButtonEvent;
import java.util.ArrayList;

public class UIRoot extends UIElement {

    public UIRoot() {
        super(
            new UIFlowLayout()
                .setPadAll(0)
                .setWidth(UIFlowLayout.EXPAND)
                .setHeight(UIFlowLayout.EXPAND)
        );
    }

    @Override
    public void render(TextureBatch batch, Rect bounds) {
        UIWalker walker = new UIWalker();
        walker.push(this, bounds);

        while (!walker.isEmpty()) {
            UIWalker.Entry entry = walker.pop();
            entry.element.render(batch, entry.bounds);
            walker.push(entry.element, entry.bounds);
        }
    }

    public void handleMouseButtonEvent(MouseButtonEvent event, Rect bounds) {
        UIWalker walker = new UIWalker();
        walker.push(this, bounds);

        while (!walker.isEmpty()) {
            UIWalker.Entry entry = walker.pop();
            if (!entry.bounds.contains(event.x, event.y)) {
                continue;
            }
            if (entry.element.handleMouseButtonEvent(event)) {
                continue;
            }
            walker.push(entry.element, entry.bounds);
        }
    }
}

final class UIWalker {

    public final class Entry {

        public final UIElement element;
        public final Rect bounds;

        public Entry(UIElement element, Rect bounds) {
            this.element = element;
            this.bounds = bounds;
        }
    }

    ArrayList<Entry> stack = new ArrayList<>();

    public boolean isEmpty() {
        return stack.size() == 0;
    }

    public void push(UIElement element, Rect bounds) {
        for (UIElement child : element.getChildrenReversed()) {
            stack.add(new Entry(child, child.getBounds(bounds)));
        }
    }

    public Entry pop() {
        return stack.removeLast();
    }
}
