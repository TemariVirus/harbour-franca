package com.simpulator.engine.ui;

import com.simpulator.engine.graphics.TextureBatch;

/** A region in the UI that can contain other UI elements. */
public class UIRegion extends UIElement {

    public UIRegion(UILayout layout) {
        super(layout);
    }

    @Override
    public void render(TextureBatch batch) {}
}
