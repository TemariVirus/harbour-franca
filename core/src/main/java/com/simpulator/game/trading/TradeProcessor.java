package com.simpulator.game.trading;

import java.util.Set;

public class TradeProcessor {

    public enum TradeResult {
        FAILED,
        TRADED,
        GOT_WANTS,
    }

    /** If receiving a wants item, give items up to this many times receive value. */
    private final float wantsThreshold;
    /** If receiving a non-wants item, give items up to this many times receive value. */
    private final float normalThreshold;
    /** We want these items */
    private final Set<Item> wants;

    public TradeProcessor(
        float wantsThreshold,
        float normalThreshold,
        Set<Item> wants
    ) {
        this.wantsThreshold = wantsThreshold;
        this.normalThreshold = normalThreshold;
        this.wants = wants;
    }

    public TradeResult trade(Item give, Item receive) {
        // Deny +2 or more rarity
        int rarityDifference = give.rarity().tier() - receive.rarity().tier();
        if (rarityDifference > 1) {
            return TradeResult.FAILED;
        }

        float acceptThreshold = wants.contains(receive)
            ? wantsThreshold
            : normalThreshold;
        float valueRatio = (float) give.value() / (float) receive.value();

        if (valueRatio > acceptThreshold) {
            return TradeResult.FAILED;
        }
        return wants.contains(receive)
            ? TradeResult.GOT_WANTS
            : TradeResult.TRADED;
    }
}
