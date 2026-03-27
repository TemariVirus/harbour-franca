package com.simpulator.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.simpulator.engine.graphics.GraphicsManager;
import com.simpulator.engine.scene.Scene;
import com.simpulator.engine.ui.UIRoot;
import com.simpulator.game.trading.Inventory;
import com.simpulator.game.trading.Item;
import com.simpulator.game.ui.Box;
import com.simpulator.game.ui.Text;
import com.simpulator.game.ui.UIRelativeLayout;
import com.simpulator.game.ui.UIRelativeLayout.Alignment;

/**
 * In-game HUD overlay showing mission objective, player inventory,
 * interaction prompts, and a crosshair.
 */
public class GameHUD implements Scene {

    private static final Color BACKGROUD_COLOR = new Color(
        0.2f,
        0.2f,
        0.2f,
        0.7f
    );

    private final Viewport viewport = new ExtendViewport(640, 480);
    private final BitmapFont font;
    private final UIRoot uiRoot = new UIRoot();

    private final int levelGoal;
    private final Inventory playerInventory;

    // HUD elements
    private Text objectiveLabel;
    private Text hintLabel;
    private Text crosshair;
    private Text interactionPrompt;
    private Box inventoryGroup;
    private Text[] inventoryLabels;
    private Text hintCountLabel;
    private float hintTimer = 0f;

    public GameHUD(int levelGoal, Inventory playerInventory) {
        this.levelGoal = levelGoal;
        this.playerInventory = playerInventory;

        font = new BitmapFont(Gdx.files.internal("fonts/en.fnt"));
        buildHUD();
    }

    private void buildHUD() {
        // --- Top row: objective in top-center ---
        objectiveLabel = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            Color.YELLOW,
            new UIRelativeLayout.Builder().padTop(20).height(10.5f).getLayout()
        );
        updateObjectiveText();
        uiRoot.addChild(objectiveLabel);

        hintCountLabel = new Text(
            "Hints: 1", // Default starting text
            font,
            Text.Alignment.END, // Aligns text to the right
            Color.CYAN,
            new UIRelativeLayout.Builder()
                .xAlignment(Alignment.END) // Pushes it to the right side of the screen
                .padTop(20) // Matches the height of the objective label
                .padRight(20) // Keeps it from touching the edge
                .height(10.5f)
                .getLayout()
        );
        uiRoot.addChild(hintCountLabel);

        hintLabel = new Text(
            "",
            font,
            Text.Alignment.CENTER,
            Color.CYAN,
            new UIRelativeLayout.Builder().padTop(45).height(10.5f).getLayout()
        );
        hintLabel.setVisible(false); // Hide hint by default
        uiRoot.addChild(hintLabel);

        // --- Center: crosshair and interaction prompt ---
        crosshair = new Text(
            "+",
            font,
            Text.Alignment.CENTER,
            new Color(1f, 1f, 1f, 0.7f),
            new UIRelativeLayout.Builder()
                .yAlignment(Alignment.CENTER)
                .height(11)
                .getLayout()
        );
        uiRoot.addChild(crosshair);

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
        inventoryGroup = new Box(
            0,
            Color.CLEAR,
            Color.CLEAR,
            new UIRelativeLayout()
        );
        uiRoot.addChild(inventoryGroup);

        inventoryLabels = new Text[Inventory.MAX_ITEMS];
        for (int i = 0; i < Inventory.MAX_ITEMS; i++) {
            final float SLOT_WIDTH = 110;
            final float SLOT_HEIGHT = 35;
            final float SLOT_SPACING = 5;

            Box slotBox = new Box(
                0,
                Color.CLEAR,
                BACKGROUD_COLOR,
                new UIRelativeLayout.Builder()
                    .yAlignment(Alignment.END)
                    .padBottom(15)
                    .padLeft(15 + i * (SLOT_WIDTH + SLOT_SPACING))
                    .width(SLOT_WIDTH)
                    .height(SLOT_HEIGHT)
                    .getLayout()
            );
            slotBox.setVisible(false);
            inventoryGroup.addChild(slotBox);

            inventoryLabels[i] = new Text(
                "",
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

    @Override
    public boolean onFocus() {
        return false;
    }

    private void updateObjectiveText() {
        objectiveLabel.setText(
            "$" + playerInventory.getTotalValue() + " / $" + levelGoal
        );
    }

    public void setObjectiveVisible(boolean visible) {
        objectiveLabel.setVisible(visible);
    }

    public void setCrosshairVisible(boolean visible) {
        crosshair.setVisible(visible);
    }

    public void setPrompt(String text) {
        interactionPrompt.setText(text);
    }

    public void setPromptVisible(boolean visible) {
        interactionPrompt.setVisible(visible);
    }

    public void setInventoryVisible(boolean visible) {
        inventoryGroup.setVisible(visible);
    }

    @Override
    public void update(float deltaTime) {
        updateObjectiveText();
        int i = 0;
        for (Item item : playerInventory.getItems()) {
            inventoryLabels[i].setText(item.toString());
            inventoryLabels[i].getParent().setVisible(true);
            i++;
        }
        for (; i < inventoryLabels.length; i++) {
            inventoryLabels[i].getParent().setVisible(false);
        }
        uiRoot.update(deltaTime);
        if (hintTimer > 0) {
            hintTimer -= deltaTime;
            if (hintTimer <= 0) {
                hideHint();
            }
        }
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
        viewport.setScreenPosition(
            viewport.getScreenX() + x,
            viewport.getScreenY() + y
        );
        uiRoot.updateBounds(viewport);

        graphics.beginRender(viewport);
        graphics.render(uiRoot, null);
        graphics.endRender();
    }

    public void showHint(String text) {
        hintLabel.setText(text);
        hintLabel.setVisible(true);
        hintTimer = 5.0f;
    }

    public void hideHint() {
        hintLabel.setVisible(false);
    }

    public void updateHintCount(int count) {
        hintCountLabel.setText("Hints: " + count);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
