package com.simpulator.game;

import com.simpulator.engine.Entity;
import com.simpulator.engine.EntityManager;
import java.util.HashSet;

public class EntityRemover {

    private EntityManager entityManager;
    private HashSet<Entity> toRemove;

    public EntityRemover(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.toRemove = new HashSet<>();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void markForRemoval(Entity entity) {
        toRemove.add(entity);
    }

    public void update() {
        for (Entity entity : toRemove) {
            entityManager.remove(entity);
        }
        toRemove.clear();
    }
}
