package com.simpulator.game.trading;

import java.util.Collections;
import java.util.Set;

public class TradeOffer {

    /** We give this item. */
    private final Item gives;
    /** We get this item. */
    private final Item receives;
    /** We want to receive these items. */
    private final Set<Item> wants;

    public TradeOffer(Item gives, Item receives, Set<Item> wants) {
        this.gives = gives;
        this.receives = receives;
        this.wants = wants;
    }

    public Item getGives() {
        return gives;
    }

    public Item getReceives() {
        return receives;
    }

    public Set<Item> getWants() {
        return Collections.unmodifiableSet(wants);
    }

    public boolean receivesWants() {
        return wants.contains(receives);
    }
}
