package com.simpulator.game.Trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerInventory {
    private final List<Item> items = new ArrayList<>();
    private int totalTradeValue = 0;

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalTradeValue() {
        return totalTradeValue;
    }

    public void addTradeValue(int value) {
        totalTradeValue += value;
    }

    public void subtractTradeValue(int value) {
        totalTradeValue -= value;
    }

    public void recalculateTotalValue() {
        totalTradeValue = items.stream().mapToInt(Item::getValue).sum();
    }

    public void clear() {
        items.clear();
        totalTradeValue = 0;
    }
}