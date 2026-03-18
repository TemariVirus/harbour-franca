package com.simpulator.game;

import java.util.ArrayList;
import java.util.List;

public class Level {
    public String levelId;
    public String displayName;
    public String skyboxTexturePrefix; 
    public String backgroundMusic;
    
    public float playerStartX;
    public float playerStartY;
    public float playerStartZ;

    public List<EntityConfig> entitiesToSpawn = new ArrayList<>();

    public static class EntityConfig {
        public String entityType; 
        public float x, y, z;
        
        public EntityConfig(String type, float x, float y, float z) {
            this.entityType = type;
            this.x = x; this.y = y; this.z = z;
        }
    }
}