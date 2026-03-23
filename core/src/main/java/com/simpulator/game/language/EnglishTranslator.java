package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class EnglishTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        // Names are already in English
        return item.toString();
    }
}
