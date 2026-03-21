package com.simpulator.game.Trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TradeOfferFactory {
    private final Random random;
    private final int acceptanceThreshold;

    public TradeOfferFactory(Random random, int acceptanceThreshold) {
        this.random = random;
        this.acceptanceThreshold = acceptanceThreshold;
    }

    public TradeOffer createOffer(PlayerInventory inventory, String[] npcDialogueOptions) {
        List<Item> playerItems = new ArrayList<>(inventory.getItems());
        if (playerItems.size() < 3)
            throw new IllegalStateException("Need at least 3 items in inventory");
        
        // Calculate the average value of the player's current inventory.
        // This drives what tier of items the NPC will offer — so trades feel relevant to where the player is in progression.
        double avgValue = playerItems.stream()
                .mapToInt(Item::getValue)
                .average()
                .orElse(0);
        ItemRarity avgRarity = getRarityForValue((int) avgValue);

        
        // Build 3 NPC items spanning one tier below, equal to, and one tier above the player's average. 
        List<Item> npcItems = new ArrayList<>();
        List<String> npcDialogue = new ArrayList<>();

        npcItems.add(generateNpcItem(getRarityBelow(avgRarity)));
        npcItems.add(generateNpcItem(avgRarity));
        npcItems.add(generateNpcItem(getRarityAbove(avgRarity)));

        // Pair each item with the NPC's language string at the same index.
        npcDialogue.add(npcDialogueOptions[0]);
        npcDialogue.add(npcDialogueOptions[1]);
        npcDialogue.add(npcDialogueOptions[2]);

        // Randomizes which button position each item/label pair appears on, so the correct answer isn't always the same button every round.
        List<Integer> indices = new ArrayList<>(List.of(0, 1, 2));
        Collections.shuffle(indices, random);

        List<Item> shuffledNpcItems = new ArrayList<>();
        List<String> shuffledNpcDialogue = new ArrayList<>();
        for (int i : indices) {
            shuffledNpcItems.add(npcItems.get(i));
            shuffledNpcDialogue.add(npcDialogue.get(i));
        }

        // Player choices - one item below threshold, one within threshold, and one above
        int avg = (int) avgValue;

        // Item the NPC will reject (player offering too little)
        Item playerLower = playerItems.stream()
                .filter(i -> i.getValue() < avg - acceptanceThreshold)
                .findFirst()
                .orElse(playerItems.get(0));

        // Item the NPC will accept as fair (within the acceptance threshold)
        Item playerEqual = playerItems.stream()
                .filter(i -> {
                    int d = i.getValue() - avg;
                    return d <= 0 && d >= -acceptanceThreshold;
                })
                .max(Comparator.comparingInt(Item::getValue))
                .orElse(playerItems.get(1 % playerItems.size()));

        // Item that makes the NPC happy (player offering more than needed)
        Item playerHigher = playerItems.stream()
                .filter(i -> i.getValue() > avg)
                .max(Comparator.comparingInt(Item::getValue))
                .orElse(playerItems.get(2 % playerItems.size()));

        // Shuffle player choices too so the "best" item isn't always the same button
        List<Item> playerChoices = new ArrayList<>(List.of(playerLower, playerEqual, playerHigher));
        Collections.shuffle(playerChoices, random);

        return new TradeOffer(shuffledNpcItems, playerChoices, shuffledNpcDialogue);
    }

    private ItemRarity getRarityForValue(int value) {
        if (value >= 50)
            return ItemRarity.EPIC;
        if (value >= 25)
            return ItemRarity.RARE;
        return ItemRarity.COMMON;
    }

    private ItemRarity getRarityBelow(ItemRarity rarity) {
        switch (rarity) {
            case EPIC:
                return ItemRarity.RARE;
            case RARE:
                return ItemRarity.COMMON;
            default:
                return ItemRarity.COMMON;
        }
    }

    private ItemRarity getRarityAbove(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return ItemRarity.RARE;
            case RARE:
                return ItemRarity.EPIC;
            default:
                return ItemRarity.EPIC;
        }
    }

    private Item generateNpcItem(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return new Item("npc_common", "Trinket", ItemRarity.COMMON, 6);
            case RARE:
                return new Item("npc_rare", "Pendant", ItemRarity.RARE, 25);
            case EPIC:
                return new Item("npc_epic", "Gemstone", ItemRarity.EPIC, 50);
            case LEGENDARY:
                return new Item("npc_legendary", "Dragon Scale", ItemRarity.LEGENDARY, 100);
            default:
                return new Item("npc_common", "Trinket", ItemRarity.COMMON, 6);
        }
    }
}