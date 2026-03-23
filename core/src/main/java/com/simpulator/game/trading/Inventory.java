package com.simpulator.game.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    public static final int MAX_ITEMS = 3;

    private final List<Item> items = new ArrayList<>();

    public void addItem(Item item) {
        if (items.size() >= MAX_ITEMS) {
            throw new IllegalStateException(
                "Inventory is full. Cannot add more items."
            );
        }
        items.add(item);
    }

    public boolean removeItem(Item item) {
        boolean result = items.remove(item);

        return result;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int getTotalValue() {
        return items.stream().mapToInt(Item::value).sum();
    }

    public void clear() {
        items.clear();
    }
}
