package com.simpulator.game.levels;

import com.simpulator.game.language.Language;
import com.simpulator.game.trading.Item;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MerchantData {

    private final String name;
    private final String imagePath;
    private final Language language;
    private final String dialogue;
    private final String gotWantsDialogue;
    private final String tradedDialogue;
    private final String tradeFailedDialogue;
    private final String timesUpDialogue;
    private final float wantsThreshold;
    private final float normalThreshold;
    private final List<Item> items;
    private final Set<Item> wants;
    public final String hint;

    public MerchantData(
        String name,
        String imagePath,
        Language language,
        String dialogue,
        String gotWantsDialogue,
        String tradedDialogue,
        String tradeFailedDialogue,
        String timesUpDialogue,
        float wantsThreshold,
        float normalThreshold,
        Item[] items,
        Set<Item> wants,
        String hint
    ) {
        this.name = name;
        this.imagePath = imagePath;
        this.language = language;
        this.dialogue = dialogue;
        this.gotWantsDialogue = gotWantsDialogue;
        this.tradedDialogue = tradedDialogue;
        this.tradeFailedDialogue = tradeFailedDialogue;
        this.timesUpDialogue = timesUpDialogue;
        this.wantsThreshold = wantsThreshold;
        this.normalThreshold = normalThreshold;
        this.items = Arrays.asList(items);
        this.wants = new HashSet<>(wants);
        this.hint = hint;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Language getLanguage() {
        return language;
    }

    public String getDialogue() {
        return dialogue;
    }

    public String getGotWantsDialogue() {
        return gotWantsDialogue;
    }

    public String getTradedDialogue() {
        return tradedDialogue;
    }

    public String getTradeFailedDialogue() {
        return tradeFailedDialogue;
    }

    public String getTimesUpDialogue() {
        return timesUpDialogue;
    }

    public float getWantsThreshold() {
        return wantsThreshold;
    }

    public float getNormalThreshold() {
        return normalThreshold;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Set<Item> getWants() {
        return Collections.unmodifiableSet(wants);
    }

    public boolean wantsItem(Item item) {
        return wants.contains(item);
    }
}
