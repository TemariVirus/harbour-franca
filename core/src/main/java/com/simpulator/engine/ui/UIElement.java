package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** An element of a UI. */
public abstract class UIElement {

    private UILayout layout;
    private Rect bounds;
    private boolean visible = true;

    private UIElement parent = null;
    private final List<UIElement> children = new ArrayList<>();
    private final Map<Class<?>, List<UIListener<?>>> eventHandlers =
        new HashMap<>();

    protected UIElement(UILayout layout) {
        this.layout = layout;
        this.bounds = new Rect(0, 0, 0, 0);
    }

    public void setLayout(UILayout layout) {
        this.layout = layout;
        updateBounds(bounds);
    }

    public Rect getBounds() {
        return new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    /** Call when the parent's bounds have changed to update the bounds of this element and its children. */
    public void updateBounds(Rect parentBounds) {
        Rect oldBounds = bounds;
        bounds = layout.computeBounds(parentBounds);
        if (bounds.equals(oldBounds)) {
            return;
        }
        for (UIElement child : children) {
            child.updateBounds(bounds);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether this element and its children should be rendered and receive events.
     * This object is returned to allow for chaining.
     */
    public UIElement setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public UIElement getParent() {
        return parent;
    }

    /**
     * Inserts a new child to the end of the children list.
     * This object is returned to allow for chaining.
     */
    public UIElement addChild(UIElement child) {
        children.add(child);
        child.parent = this;
        return this;
    }

    /** Removes the child from the children list. */
    public void removeChild(UIElement child) {
        children.remove(child);
        child.parent = null;
    }

    /**
     * Inserts a new child at the specified index in the children list.
     * This object is returned to allow for chaining.
     */
    public UIElement insertChild(int index, UIElement child) {
        children.add(index, child);
        child.parent = this;
        return this;
    }

    protected Iterable<UIElement> getChildrenReversed() {
        return children.reversed();
    }

    /**
     * Adds an event listener for the specified event type.
     * This object is returned to allow for chaining.
     */
    public <T> UIElement addListener(
        Class<T> eventType,
        UIListener<T> handler
    ) {
        eventHandlers
            .computeIfAbsent(eventType, k -> new ArrayList<>())
            .add(handler);
        return this;
    }

    /**
     * Handles an event by calling all registered listeners for the event's type.
     * Returns true if any listener handled the event.
     */
    public <T> boolean handleEvent(T event) {
        List<UIListener<?>> handlers = eventHandlers.get(event.getClass());
        if (handlers == null) {
            return false;
        }
        boolean handled = false;
        for (UIListener<?> handler : handlers) {
            @SuppressWarnings("unchecked")
            UIListener<T> typedHandler = (UIListener<T>) handler;
            if (typedHandler.handle(event)) {
                handled = true;
            }
        }
        return handled;
    }

    /**
     * Update the UI element's state. Usually called once per frame.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void update(float deltaTime) {}

    /** Renders this UI element (but not its children) at the element's current bounds. */
    public abstract void render(TextureBatch batch);
}
