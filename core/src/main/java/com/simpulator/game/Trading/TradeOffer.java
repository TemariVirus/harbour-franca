package com.simpulator.game.Trading;

import java.util.List;

public class TradeOffer {
    private final List<Item> npcChoices;
    private final List<Item> playerChoices;
    private final List<String> npcDialogueLabels;

    public TradeOffer(List<Item> npcChoices, List<Item> playerChoices, List<String> npcDialogueLabels) {
        if (npcChoices.size() != 3 || playerChoices.size() != 3 || npcDialogueLabels.size() != 3)
            throw new IllegalArgumentException("Must have exactly 3 of each");
        this.npcChoices = List.copyOf(npcChoices);
        this.playerChoices = List.copyOf(playerChoices);
        this.npcDialogueLabels = List.copyOf(npcDialogueLabels);
    }

    public List<Item> getNpcChoices() {
        return npcChoices;
    }

    public List<Item> getPlayerChoices() {
        return playerChoices;
    }

    public List<String> getNpcDialogueLabels() {
        return npcDialogueLabels;
    }
}