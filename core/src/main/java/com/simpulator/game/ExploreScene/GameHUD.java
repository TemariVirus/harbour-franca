package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.Widget;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.ui.UIRelativeLayout;
import com.simpulator.engine.ui.UIRelativeLayout.Alignment;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;

/**
 * In-game HUD overlay showing mission objective, player inventory,
 * interaction prompts, and a crosshair.
 */
public class GameHUD implements Widget, Disposable {

    private static final Color inventoryBackgroundColor = new Color(
        0.2f,
        0.2f,
        0.2f,
        0.7f
    );

    private final Viewport viewport;
    private BitmapFont font;
    private UIRoot uiRoot;

    // HUD elements
    private Text objectiveLabel;
    private Text interactionPrompt;
    private Text[] inventoryLabels;

    public GameHUD() {
        this.viewport = new ExtendViewport(640, 480);
        buildHUD();
    }

    private void buildHUD() {
        font = new BitmapFont();
        uiRoot = new UIRoot();

        // --- Top row: objective in top-center ---
        objectiveLabel = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            Color.YELLOW,
            new UIRelativeLayout.Builder().padTop(20).height(10.5f).getLayout()
        );
        setObjective(0, 0);
        uiRoot.addChild(objectiveLabel);

        // --- Center: crosshair and interaction prompt ---
        uiRoot.addChild(
            new Text(
                "+",
                font,
                Text.Alignment.CENTER,
                Color.WHITE,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.CENTER)
                    .height(11)
                    .getLayout()
            )
        );

        interactionPrompt = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            Color.YELLOW,
            new UIRelativeLayout.Builder()
                .yAlignment(Alignment.CENTER)
                .height(10.5f)
                .padTop(120)
                .getLayout()
        );
        uiRoot.addChild(interactionPrompt);

        // --- Bottom row: inventory slots ---
        inventoryLabels = new Text[3];
        for (int i = 0; i < 3; i++) {
            final float SLOT_WIDTH = 110;
            final float SLOT_HEIGHT = 35;
            final float SLOT_SPACING = 5;

            Box slotBox = new Box(
                0,
                Color.CLEAR,
                inventoryBackgroundColor,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.END)
                    .padBottom(15)
                    .padLeft(15 + i * (SLOT_WIDTH + SLOT_SPACING))
                    .width(SLOT_WIDTH)
                    .height(SLOT_HEIGHT)
                    .getLayout()
            );
            uiRoot.addChild(slotBox);

            inventoryLabels[i] = new Text(
                "[Empty]",
                font,
                Text.Alignment.CENTER,
                Color.WHITE,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.CENTER)
                    .height(10)
                    .getLayout()
            );
            slotBox.addChild(inventoryLabels[i]);
        }
    }

    @Override
    public InputProcessor getInputProcessor() {
        return null;
    }

    /**
     * Update the mission objective display.
     *
     * @param current Current dollar value accumulated.
     * @param target  Target dollar value to win.
     */
    public void setObjective(int current, int target) {
        objectiveLabel.setText("$" + current + " / $" + target);
    }

    /**
     * Update the inventory display.
     *
     * @param items Array of item names (up to 3).
     */
    public void setInventory(String[] items) {
        for (int i = 0; i < inventoryLabels.length; i++) {
            if (items != null && i < items.length && items[i] != null) {
                inventoryLabels[i].setText(items[i]);
            } else {
                inventoryLabels[i].setText("[Empty]");
            }
        }
    }

    /** Show the interaction prompt. */
    public void showInteractionPrompt(String text) {
        interactionPrompt.setText(text);
        interactionPrompt.setVisible(true);
    }

    /** Hide the interaction prompt. */
    public void hideInteractionPrompt() {
        interactionPrompt.setVisible(false);
    }

    @Override
    public void update(float deltaTime) {
        uiRoot.update(deltaTime);
    }

    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        viewport.update(width, height, true);
        viewport.setScreenPosition(x, y);
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
