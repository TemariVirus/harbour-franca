package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.simpulator.engine.Widget;
import com.simpulator.engine.graphics.GraphicsManager;

/**
 * In-game HUD overlay showing mission objective, player inventory,
 * interaction prompts, and a crosshair.
 */
public class GameHUD implements Widget, Disposable {

    private final Stage stage;
    private final Skin skin;

    // HUD elements
    private Label objectiveLabel;
    private Label[] inventorySlots;
    private Label interactionPrompt;
    private Label crosshair;

    public GameHUD(Skin skin) {
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        buildHUD();
    }

    private void buildHUD() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // --- Top row: objective in top-center ---
        objectiveLabel = new Label("$0 / $50", skin, "title");
        objectiveLabel.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
        root.add(objectiveLabel).expandX().top().padTop(20).colspan(2).row();

        // --- Center: crosshair and interaction prompt ---
        Table centerTable = new Table();
        crosshair = new Label("+", skin, "title");
        centerTable.add(crosshair).center().row();

        interactionPrompt = new Label("", skin, "prompt");
        interactionPrompt.setVisible(false);
        centerTable.add(interactionPrompt).center().padTop(30);

        root.add(centerTable).center().colspan(2).row();

        // --- Bottom row: inventory slots ---
        Table bottomRow = new Table();
        inventorySlots = new Label[3];
        for (int i = 0; i < 3; i++) {
            inventorySlots[i] = new Label("[Empty]", skin);
            Table slotBox = new Table();
            slotBox.setBackground(
                skin.newDrawable(
                    "white",
                    new com.badlogic.gdx.graphics.Color(0.2f, 0.2f, 0.2f, 0.7f)
                )
            );
            slotBox.add(inventorySlots[i]).pad(8);
            bottomRow.add(slotBox).width(110).height(35).padRight(5);
        }
        root.add(bottomRow).expand().bottom().left().padBottom(15).padLeft(20);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
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
        for (int i = 0; i < inventorySlots.length; i++) {
            if (items != null && i < items.length && items[i] != null) {
                inventorySlots[i].setText(items[i]);
            } else {
                inventorySlots[i].setText("[Empty]");
            }
        }
    }

    /**
     * Show the interaction prompt for an NPC.
     *
     * @param npcName The NPC's display name.
     */
    public void showInteractionPrompt(String npcName) {
        interactionPrompt.setText("[E] Trade with " + npcName);
        interactionPrompt.setVisible(true);
    }

    /** Hide the interaction prompt. */
    public void hideInteractionPrompt() {
        interactionPrompt.setVisible(false);
    }

    @Override
    public void update(float deltaTime) {
        stage.act(deltaTime);
    }

    /**
     * Render the HUD. Call after the main scene render.
     */
    @Override
    public void render(
        GraphicsManager graphics,
        int x,
        int y,
        int width,
        int height
    ) {
        stage.getViewport().update(width, height, true);
        stage.getViewport().setScreenPosition(x, y);

        // stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
