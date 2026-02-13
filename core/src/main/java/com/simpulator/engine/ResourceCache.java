package com.simpulator.engine;

import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;

public abstract class ResourceCache<
    K,
    V extends Disposable
> implements Disposable {

    private final HashMap<K, V> items = new HashMap<>();

    /** Loads the item into memory without the cache. */
    protected abstract V load(K key);

    /**
     * Gets the item from the cache. If the item is not found, loads the item
     * into the cache and then returns it.
     */
    public V get(K key) {
        V value = items.get(key);
        if (value == null) {
            value = load(key);
            items.put(key, value);
        }
        return value;
    }

    /** Iterates over the cached items. */
    protected Iterable<V> values() {
        return items.values();
    }

    /** Removes the item from the cache and disposes of it. */
    public void remove(K key) {
        V value = items.remove(key);
        if (value != null) {
            value.dispose();
        }
    }

    /** Disposes of all items in the cache and clears it. */
    public void dispose() {
        for (V value : items.values()) {
            value.dispose();
        }
        items.clear();
    }
}
