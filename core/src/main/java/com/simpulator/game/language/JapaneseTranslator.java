package com.simpulator.game.language;

import com.simpulator.game.trading.Item;

public class JapaneseTranslator implements Translator {

    @Override
    public String translateItemName(Item item) {
        switch (item) {
            case TRINKET:
                return "おかざり";
            case ROPE:
                return "なわ";
            case CLOTH:
                return "ぬの";
            case CANDLE:
                return "ろうそく";
            case FISH:
                return "ひもの";
            case PENDANT:
                return "ペンダント";
            case COMPASS:
                return "らしんばん";
            case SPICE:
                return "こうしんりょう";
            case MAP:
                return "ふるちず";
            case LENS:
                return "レンズ";
            case GEMSTONE:
                return "ほうせき";
            case IDOL:
                return "おうごんのぐうぞう";
            case SEXTANT:
                return "ろくぶんぎ";
            case CHALICE:
                return "せいはい";
            case ASTROLABE:
                return "アストロラーベ";
            case DRAGON_SCALE:
                return "龍鱗";
            case CROWN:
                return "こだいのおうかん";
            case ORB:
                return "しんぴのたま";
            default:
                return item.toString();
        }
    }
}
