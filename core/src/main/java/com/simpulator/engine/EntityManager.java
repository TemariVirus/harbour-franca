package com.simpulator.engine;

import java.util.ArrayList;

public class EntityManager {

    private ArrayList<Entity> entities;

    public EntityManager() {
        this.entities = new ArrayList<Entity>();
    }

    public void add(Entity entity) {
        this.entities.add(entity);
    }

    public void remove(Entity entity) {
        this.entities.remove(entity);
    }

    public void removeAll() {
        this.entities.clear();
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }
    }

    public ArrayList<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
}
