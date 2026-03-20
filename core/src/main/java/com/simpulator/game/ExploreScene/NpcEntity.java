package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.graphics.EntityRenderer;
import com.simpulator.engine.graphics.RectangleRenderer;
import com.simpulator.game.CuboidEntity;

/**
 * A static NPC entity that the player can interact with to trade.
 * Each NPC speaks a different language and can only be traded with once.
 */
public class NpcEntity extends CuboidEntity {

    /** Possible trade states for this NPC. */
    public enum TradeState {
        /** NPC is available to trade. */
        AVAILABLE,
        /** NPC has already traded with the player. */
        TRADED,
        /** NPC is angry and rejected the trade (player lost an item). */
        ANGRY
    }

    private final String name;
    private final String language;
    private TradeState tradeState;

    // Dialogue options in the NPC's language.
    // Index 0 = "worth more", 1 = "worth same", 2 = "worth less"
    private final String[] dialogueOptions;

    // Inner thoughts shown after player picks a dialogue option.
    // Index 0 = "worth more" reaction, 1 = "worth same", 2 = "worth less"
    private final String[] innerThoughts;

    /**
     * Create a new NPC entity.
     *
     * @param position        World position of the NPC.
     * @param size            Size of the NPC's collision/render box.
     * @param renderer        How to render this NPC visually.
     * @param name            Display name (e.g. "Chinese Merchant").
     * @param language        Language label (e.g. "Chinese").
     * @param dialogueOptions 3 dialogue strings in the NPC's language.
     * @param innerThoughts   3 inner thought strings (English reactions).
     */
    public NpcEntity(
        Vector3 position,
        Vector3 size,
        EntityRenderer renderer,
        String name,
        String language,
        String[] dialogueOptions,
        String[] innerThoughts
    ) {
        super(position, size, new Quaternion().setFromAxis(Vector3.Y, 0), renderer, false);
        this.name = name;
        this.language = language;
        this.tradeState = TradeState.AVAILABLE;
        this.dialogueOptions = dialogueOptions;
        this.innerThoughts = innerThoughts;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public TradeState getTradeState() {
        return tradeState;
    }

    public void setTradeState(TradeState tradeState) {
        this.tradeState = tradeState;
    }

    /** Returns true if this NPC can still be traded with. */
    public boolean canTrade() {
        return tradeState == TradeState.AVAILABLE;
    }

    /**
     * Returns the 3 dialogue options in the NPC's language.
     * Index 0 = worth more, 1 = worth same, 2 = worth less.
     */
    public String[] getDialogueOptions() {
        return dialogueOptions;
    }

    /**
     * Returns the 3 inner thought strings (English).
     * Index 0 = reaction to "worth more", etc.
     */
    public String[] getInnerThoughts() {
        return innerThoughts;
    }
}
