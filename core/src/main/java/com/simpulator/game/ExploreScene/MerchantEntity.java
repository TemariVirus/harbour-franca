package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.RectangleRenderer;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.CuboidEntity;
import com.simpulator.game.levels.MerchantData;
import com.simpulator.game.trading.Item;
import com.simpulator.game.trading.TradeProcessor;
import com.simpulator.game.trading.TradeProcessor.TradeResult;

/**
 * A static Merchant entity that the player can interact with to trade.
 * Each Merchant can only be traded with once.
 */
public class MerchantEntity extends CuboidEntity {

    private MerchantData data;
    private TradeProcessor tradeManager;
    private Entity player;
    private boolean canTrade = true;

    public MerchantEntity(
        Vector3 position,
        TextureCache textures,
        MerchantData data,
        Entity player
    ) {
        super(
            position,
            new Vector3(1, 2, 0.2f),
            new Quaternion().setFromAxis(Vector3.Y, 0),
            new RectangleRenderer(
                new TextureRegion(textures.get(data.getImagePath()))
            ),
            false
        );
        this.data = data;
        this.tradeManager = new TradeProcessor(
            data.getWantsThreshold(),
            data.getNormalThreshold(),
            data.getWants()
        );
        this.player = player;
    }

    /** Returns whether this merchant can still be traded with. */
    public boolean canTrade() {
        return canTrade;
    }

    public MerchantData getData() {
        return data;
    }

    public TradeResult trade(int giveIndex, Item receive) {
        if (!canTrade) {
            throw new IllegalStateException(
                "Merchant has already been traded with."
            );
        }
        canTrade = false;

        return tradeManager.trade(getData().getItems().get(giveIndex), receive);
    }

    @Override
    public void update(float deltaTime) {
        // Face player
        Vector3 toPlayer = player.getPosition().sub(position);
        float targetAngle = (float) Math.atan2(toPlayer.x, toPlayer.z);
        rotation.setFromAxis(Vector3.Y, targetAngle);
    }
}
