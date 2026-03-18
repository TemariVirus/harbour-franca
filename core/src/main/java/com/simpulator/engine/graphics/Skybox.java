package com.simpulator.engine.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
// Centers on cam
public class Skybox implements Renderable {
    private TextureRegion[] faces; //0=Front, 1=Back, 2=Left, 3=Right, 4=Top, 5=Bottom
    private Matrix4 transform = new Matrix4();
    

    private float scale; 

    public Skybox(TextureRegion[] faces, float cameraFarDinstance) {
        if (faces.length != 6) {
            throw new IllegalArgumentException("Skybox requires exactly 6 texture faces.");
        }
        this.faces = faces;
        // Scale the box
        this.scale = cameraFarDinstance * 0.5f; 
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        Vector3 camPos = camera.position;

        // Front Face (Z - scale)
        transform.setToTranslation(camPos.x, camPos.y, camPos.z - scale);
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[0], transform);

        // Back Face (Z + scale)
        transform.setToTranslation(camPos.x, camPos.y, camPos.z + scale);
        transform.rotate(0, 1, 0, 180); // Face inward
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[1], transform);

        // Left Face (X - scale)
        transform.setToTranslation(camPos.x - scale, camPos.y, camPos.z);
        transform.rotate(0, 1, 0, -90);
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[2], transform);

        // Right Face (X + scale)
        transform.setToTranslation(camPos.x + scale, camPos.y, camPos.z);
        transform.rotate(0, 1, 0, 90);
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[3], transform);

        // Top Face (Y + scale)
        transform.setToTranslation(camPos.x, camPos.y + scale, camPos.z);
        transform.rotate(1, 0, 0, -90);
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[4], transform);

        // Bottom Face (Y - scale)
        transform.setToTranslation(camPos.x, camPos.y - scale, camPos.z);
        transform.rotate(1, 0, 0, 90);
        transform.scale(scale * 2, scale * 2, 1);
        batch.draw3D(faces[5], transform);
    }

    @Override
    public boolean isVisible(Camera camera) {
        return true; // Skybox is always visible
    }

    @Override
    public float getZOrder(Camera camera) {
        // make sure it renders behind everything
        return Float.MAX_VALUE; 
    }
}