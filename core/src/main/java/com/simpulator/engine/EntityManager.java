package com.simpulator.engine;

import java.util.ArrayList;

/** Manages multiple entities. */
public class EntityManager {

    private ArrayList<Entity> entities;

    /** Create a new EntityManager with no entities. */
    public EntityManager() {
        this.entities = new ArrayList<Entity>();
    }

    /** Returns a copy of the list of entities. */
    public ArrayList<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    /** Adds an entity to be tracked. */
    public void add(Entity entity) {
        this.entities.add(entity);
    }

    /** Removes an entity. */
    public void remove(Entity entity) {
        this.entities.remove(entity);
    }

    /** Removes all entities. */
    public void removeAll() {
        this.entities.clear();
    }

    /** Calls collision handlers for collidable entities. */
    public void updateCollisions() {
        for (int i = 0; i < entities.size(); i++) {
            Entity e1 = entities.get(i);
            if (!(e1 instanceof CollidableEntity)) {
                continue;
            }

            for (int j = i + 1; j < entities.size(); j++) {
                Entity e2 = entities.get(j);
                if (!(e2 instanceof CollidableEntity)) {
                    continue;
                }

                CollidableEntity c1 = (CollidableEntity) e1;
                CollidableEntity c2 = (CollidableEntity) e2;
                if (c1.intersects(c2)) {
                    c1.onCollision(c2);
                    c2.onCollision(c1);
                }
            }
        }
    }

    /** Updates all entities. */
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }
}
