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
    public TradeResult attemptTrade(TradeOffer offer, int npcChoiceIndex, int playerChoiceIndex) {
        Item npcItem = offer.getNpcChoices().get(npcChoiceIndex);
        Item playerItem = offer.getPlayerChoices().get(playerChoiceIndex);

        int diff = playerItem.getValue() - npcItem.getValue();

        if (diff < -acceptanceThreshold) {
            return TradeResult.FAILED;
        }

        inventory.removeItem(playerItem);
        inventory.addItem(npcItem);
        inventory.subtractTradeValue(playerItem.getValue());
        inventory.addTradeValue(npcItem.getValue());

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