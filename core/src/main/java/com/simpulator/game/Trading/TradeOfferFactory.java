package com.simpulator.game.Trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TradeOfferFactory {
    private final Random random;
    private final int acceptanceThreshold;

    // These pools represent the possible items the NPC can offer, categorized by
    // rarity.
    private static final List<Item> COMMON_POOL = List.of(
            new Item("npc_trinket", "Trinket", ItemRarity.COMMON, 5),
            new Item("npc_rope", "Rope", ItemRarity.COMMON, 6),
            new Item("npc_cloth", "Cloth", ItemRarity.COMMON, 7),
            new Item("npc_candle", "Candle", ItemRarity.COMMON, 5),
            new Item("npc_fish", "Dried Fish", ItemRarity.COMMON, 6));

    private static final List<Item> RARE_POOL = List.of(
            new Item("npc_pendant", "Pendant", ItemRarity.RARE, 25),
            new Item("npc_compass", "Compass", ItemRarity.RARE, 28),
            new Item("npc_spice", "Spice Jar", ItemRarity.RARE, 26),
            new Item("npc_map", "Old Map", ItemRarity.RARE, 30),
            new Item("npc_lens", "Glass Lens", ItemRarity.RARE, 27));

    private static final List<Item> EPIC_POOL = List.of(
            new Item("npc_gemstone", "Gemstone", ItemRarity.EPIC, 50),
            new Item("npc_idol", "Gold Idol", ItemRarity.EPIC, 55),
            new Item("npc_sextant", "Sextant", ItemRarity.EPIC, 52),
            new Item("npc_chalice", "Chalice", ItemRarity.EPIC, 53),
            new Item("npc_astrolabe", "Astrolabe", ItemRarity.EPIC, 55));

    private static final List<Item> LEGENDARY_POOL = List.of(
            new Item("npc_dragon_scale", "Dragon Scale", ItemRarity.LEGENDARY, 100),
            new Item("npc_crown", "Ancient Crown", ItemRarity.LEGENDARY, 105),
            new Item("npc_orb", "Mystic Orb", ItemRarity.LEGENDARY, 110));

    public TradeOfferFactory(Random random, int acceptanceThreshold) {
        this.random = random;
        this.acceptanceThreshold = acceptanceThreshold;
    }

    public TradeOffer createOffer(PlayerInventory inventory, String[] npcDialogueOptions) {
        int buffer = acceptanceThreshold * 2;
        List<Item> playerItems = new ArrayList<>(inventory.getItems());
        if (playerItems.isEmpty())
            throw new IllegalStateException("Player inventory is empty");

        double avgValue = playerItems.stream()
                .mapToInt(Item::getValue)
                .average()
                .orElse(0);
        int avg = (int) avgValue;

        int badDealValue = avg + acceptanceThreshold + buffer;
        int fairValue = avg;
        int greatValue = Math.max(1, avg - acceptanceThreshold - buffer);
        if (greatValue < 1)
            greatValue = 1;

        List<Item> npcItems = new ArrayList<>();
        npcItems.add(new Item("npc_bad", getRarityName(badDealValue), getRarityForValue(badDealValue), badDealValue));
        npcItems.add(new Item("npc_fair", getRarityName(fairValue), getRarityForValue(fairValue), fairValue));
        npcItems.add(new Item("npc_great", getRarityName(greatValue), getRarityForValue(greatValue), greatValue));

        List<String> npcDialogue = new ArrayList<>();
        npcDialogue.add(npcDialogueOptions[0]);
        npcDialogue.add(npcDialogueOptions[1]);
        npcDialogue.add(npcDialogueOptions[2]);

        // Shuffle both together
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

    // Pick a display name from the pool for flavour, but value is set externally
    private String getRarityName(int value) {
        ItemRarity rarity = getRarityForValue(value);
        List<Item> pool;
        switch (rarity) {
            case RARE:
                pool = RARE_POOL;
                break;
            case EPIC:
                pool = EPIC_POOL;
                break;
            case LEGENDARY:
                pool = LEGENDARY_POOL;
                break;
            default:
                pool = COMMON_POOL;
                break;
        }
        // Random name from pool for variety, but doesn't affect trade logic
        return pool.get(random.nextInt(pool.size())).getName();
    }

    private ItemRarity getRarityForValue(int value) {
        if (value >= 50)
            return ItemRarity.EPIC;
        if (value >= 25)
            return ItemRarity.RARE;
        return ItemRarity.COMMON;
    }
}