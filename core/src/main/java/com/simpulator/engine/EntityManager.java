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

    /** Updates all entities. */
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }
}
