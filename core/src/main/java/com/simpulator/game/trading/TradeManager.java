package com.simpulator.game.trading;

public class TradeManager {

    public enum TradeResult {
        FAILED,
        NORMAL,
        GOT_WANTS,
    }

    /** If receiving a wants item, give items up to this many times receive value. */
    private final float wantsThreshold;
    /** If receiving a non-wants item, give items up to this many times receive value. */
    private final float normalThreshold;

    public TradeManager(float wantsThreshold, float normalThreshold) {
        this.wantsThreshold = wantsThreshold;
        this.normalThreshold = normalThreshold;
    }

    public TradeResult trade(
        TradeOffer offer,
        Inventory ourInventory,
        Inventory theirInventory
    ) {
        float acceptThreshold = offer.receivesWants()
            ? wantsThreshold
            : normalThreshold;
        float valueRatio =
            (float) offer.getGives().value() /
            (float) offer.getReceives().value();

        if (valueRatio > acceptThreshold) {
            return TradeResult.FAILED;
        }

        if (ourInventory != null) {
            ourInventory.removeItem(offer.getGives());
            ourInventory.addItem(offer.getReceives());
        }
        if (theirInventory != null) {
            theirInventory.removeItem(offer.getReceives());
            theirInventory.addItem(offer.getGives());
        }

        return offer.receivesWants()
            ? TradeResult.GOT_WANTS
            : TradeResult.NORMAL;
    }
}
