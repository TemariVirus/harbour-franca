package com.simpulator.game.ExploreScene;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import java.util.List;

// TODO: use center of the screen instead of top for targetting
/**
 * Determines which NPC (if any) the player is currently looking at
 * and is close enough to interact with.
 *
 * Uses a distance check + angle cone check (dot product of camera direction
 * vs direction-to-NPC) to find the best target.
 */
public class NpcTargetingSystem {

    /** Maximum distance (in world units) for NPC interaction. */
    private static final float INTERACTION_RANGE = 2f;
    /*
      ~0.95 corresponds to roughly an 18-degree cone.
    */
    private static final float AIM_THRESHOLD = 0.95f;

    private final Camera camera;
    private final List<MerchantEntity> npcs;
    private MerchantEntity targetedNpc;

    public NpcTargetingSystem(Camera camera, List<MerchantEntity> npcs) {
        this.camera = camera;
        this.npcs = npcs;
        this.targetedNpc = null;
    }

    /**
     * Recalculate which NPC the player is looking at.
     * Should be called once per frame.
     */
    public void update() {
        targetedNpc = null;
        float bestDot = AIM_THRESHOLD;

        Vector3 camPos = camera.position.cpy();
        Vector3 camDir = camera.direction.cpy().nor();

        for (MerchantEntity npc : npcs) {
            Vector3 npcPos = npc.getPosition();
            Vector3 toNpc = npcPos.sub(camPos);
            float distance = toNpc.len();

            if (distance > INTERACTION_RANGE || distance < 0.01f) {
                continue;
            }

            toNpc.nor();
            float dot = camDir.dot(toNpc);

            if (dot >= bestDot) {
                bestDot = dot;
                targetedNpc = npc;
            }
        }
    }

    /**
     * Returns the NPC the player is currently looking at and within range of,
     * or null if no NPC is targeted.
     */
    public MerchantEntity getTargetedNpc() {
        return targetedNpc;
    }
}
