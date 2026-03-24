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
    private final float wantsThreshold;
    private final float normalThreshold;
    private final List<Item> items;
    private final Set<Item> wants;

    public MerchantData(
        String name,
        String imagePath,
        Language language,
        String dialogue,
        float wantsThreshold,
        float normalThreshold,
        Item[] items,
        Set<Item> wants
    ) {
        this.name = name;
        this.imagePath = imagePath;
        this.language = language;
        this.dialogue = dialogue;
        this.wantsThreshold = wantsThreshold;
        this.normalThreshold = normalThreshold;
        this.items = Arrays.asList(items);
        this.wants = new HashSet<>(wants);
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
