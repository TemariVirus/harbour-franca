package com.simpulator.game.language;

public final class Translators {

    private Translators() {}

    public static Translator get(Language language) {
        switch (language) {
            case ENGLISH:
                return new EnglishTranslator();
            case VIETNAMESE:
                return new VietnameseTranslator();
            case JAPANESE:
                return new JapaneseTranslator();
            case CHINESE:
                return new ChineseTranslator();
            default:
                throw new IllegalArgumentException(
                    "Unsupported language: " + language
                );
        }
    }
}
