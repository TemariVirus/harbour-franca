package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class JapaneseTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        // TODO: more translations
        switch (item) {
            case TRINKET:
                return "おかざり";
            case ROPE:
                return "なわ";
            // Make it more difficult as the rarity gets higher maybe
            case DRAGON_SCALE:
                return "龍鱗";
            default:
                return item.toString();
        }
    }
}
