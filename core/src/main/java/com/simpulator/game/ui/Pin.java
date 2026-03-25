package com.simpulator.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.Entity;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;
import com.simpulator.engine.scene.TextureCache;

public class Pin implements Renderable {
    private final Entity target;
    private final String texturePath = "Pin.png";
    private final Vector3 screenPos = new Vector3();
    private final TextureCache textures;
    
    // Matrix to hold our 2D screen projection
    private final Matrix4 orthoMatrix = new Matrix4();

    public Pin(Entity target, TextureCache textures) {
        this.target = target;
        this.textures = textures;
    }

    public Entity getTarget() {
        return target;
    }

    @Override
    public boolean isVisible(Camera camera) {

        screenPos.set(target.getPosition());
        screenPos.y += target.getSize().y + 0.5f; // Offset slightly higher above the head
        camera.project(screenPos);
        
 
        return screenPos.z >= 0 && screenPos.z <= 1;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        Matrix4 oldProj = batch.getProjectionMatrix().cpy();
        

        orthoMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(orthoMatrix);
        
        batch.draw(
            textures.get(texturePath),
            screenPos.x - 16, // Center horizontally
            screenPos.y, 
            32, 32
        );
        
        batch.setProjectionMatrix(oldProj);
    }

    @Override
    public float getZOrder(Camera camera) {
        return screenPos.z;
    }
}