package com.simpulator.game.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {

    public static final int MAX_ITEMS = 3;

    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        if (items.size() >= MAX_ITEMS) {
            throw new IllegalStateException(
                "Inventory is full. Cannot add more items."
            );
        }
        items.add(item);
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public void removeAt(int index) {
        items.remove(index);
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
