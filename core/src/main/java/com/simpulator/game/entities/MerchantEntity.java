package com.simpulator.game.entities;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.RectangleRenderer;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.scene.TextureCache;
import com.simpulator.game.levels.MerchantData;
import com.simpulator.game.trading.Item;
import com.simpulator.game.trading.TradeProcessor;
import com.simpulator.game.trading.TradeProcessor.TradeResult;

/**
 * A static Merchant entity that the player can interact with to trade.
 * Each Merchant can only be traded with once.
 */
public class MerchantEntity extends CuboidEntity {

    private TextureRegion pinTexture;
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
        this.pinTexture = new TextureRegion(textures.get("Pin.png"));
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

    public void setCanTrade(boolean canTrade) {
        this.canTrade = canTrade;
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
        setRotation(Vector3.Y, targetAngle);
    }

    @Override
    public boolean isVisible(Camera camera) {
        // Pin is always visible
        return canTrade() || super.isVisible(camera);
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        super.render(batch, camera);
        if (canTrade()) {
            Vector3 pos = new Vector3(position);
            pos.y += size.y * 0.5f;
            float dist = camera.position.dst(pos);
            batch.draw3D(
                pinTexture,
                new Matrix4()
                    .translate(pos)
                    .rotate(rotation)
                    .translate(0, 0, dist - 0.1f) // Move towards camera to render above other entities
                    .scale(0.05f / dist, 0.05f / dist, 1)
                    .translate(0, 2f, 0) // Place above merchant head
            );
        }
    }
}
