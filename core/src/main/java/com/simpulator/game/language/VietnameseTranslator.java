package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class VietnameseTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        // TODO: more translations
        switch (item) {
            default:
                return item.toString();
        }
    }
}
