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
        if (playerItems.isEmpty())
            throw new IllegalStateException("Player inventory is empty");

        double avgValue = playerItems.stream().mapToInt(Item::getValue).average().orElse(0);
        int avg = (int) avgValue;

        int badDealValue = avg + acceptanceThreshold * 3;
        int fairValue = avg;
        int greatValue = Math.max(1, avg - acceptanceThreshold);

        List<String> usedIds = new ArrayList<>();
        List<Item> npcItems = new ArrayList<>();
        npcItems.add(drawClosest(getRarityForValue(badDealValue), badDealValue, usedIds));
        npcItems.add(drawClosest(getRarityForValue(fairValue), fairValue, usedIds));
        npcItems.add(drawClosest(getRarityForValue(greatValue), greatValue, usedIds));

        List<String> npcDialogue = new ArrayList<>();
        npcDialogue.add(npcDialogueOptions[0]);
        npcDialogue.add(npcDialogueOptions[1]);
        npcDialogue.add(npcDialogueOptions[2]);

        List<Integer> indices = new ArrayList<>(List.of(0, 1, 2));
        Collections.shuffle(indices, random);

        List<Item> shuffledNpcItems = new ArrayList<>();
        List<String> shuffledNpcDialogue = new ArrayList<>();
        for (int i : indices) {
            shuffledNpcItems.add(npcItems.get(i));
            shuffledNpcDialogue.add(npcDialogue.get(i));
        }

        return new TradeOffer(shuffledNpcItems, shuffledNpcDialogue);
    }

    private Item drawClosest(ItemRarity rarity, int targetValue, List<String> usedIds) {
        List<Item> pool = new ArrayList<>(ItemPool.getPool(rarity));
        pool.removeIf(item -> usedIds.contains(item.getId()));
        if (pool.isEmpty())
            pool = new ArrayList<>(ItemPool.getPool(rarity));

        Item closest = pool.stream()
                .min(Comparator.comparingInt(i -> Math.abs(i.getValue() - targetValue)))
                .get();

        usedIds.add(closest.getId());
        return closest; // real pool item with real pool value
    }

    private ItemRarity getRarityForValue(int value) {
        if (value >= 50)
            return ItemRarity.EPIC;
        if (value >= 25)
            return ItemRarity.RARE;
        return ItemRarity.COMMON;
    }
}