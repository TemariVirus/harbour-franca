package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

/** Translates strings into a preset language. */
public interface Translator {
    /** Returns the item's name in the translator's language. */
    String translateItemName(Item item);
}
