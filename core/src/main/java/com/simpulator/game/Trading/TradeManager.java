package com.simpulator.game.Trading;

public class TradeManager {
    private final PlayerInventory inventory;
    private final int levelGoalValue;
    private final int acceptanceThreshold;

    public TradeManager(PlayerInventory inventory, int levelGoalValue, int acceptanceThreshold) {
        this.inventory = inventory;
        this.levelGoalValue = levelGoalValue;
        this.acceptanceThreshold = acceptanceThreshold;
    }

    /**
     * @param npcChoiceIndex    which NPC item the player selected
     * @param playerChoiceIndex which player item to give away
     */
    public TradeResult attemptTrade(Item playerItem, Item npcItem) {
        int diff = playerItem.getValue() - npcItem.getValue();

        if (diff < -acceptanceThreshold) {
            return TradeResult.FAILED;
        }

        inventory.removeItem(playerItem);
        inventory.addItem(npcItem);
        inventory.recalculateTotalValue();

        return (diff > acceptanceThreshold) ? TradeResult.NPC_HAPPY : TradeResult.SUCCESS;
    }

    public boolean isLevelComplete() {
        return inventory.getTotalTradeValue() >= levelGoalValue;
    }

    public int getProgressValue() {
        return inventory.getTotalTradeValue();
    }

    public int getLevelGoalValue() {
        return levelGoalValue;
    }
}