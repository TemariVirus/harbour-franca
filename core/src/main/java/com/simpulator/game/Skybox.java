package com.simpulator.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;

// Centers on cam
public class Skybox implements Renderable {

    private TextureRegion[] faces; // 0=Front, 1=Back, 2=Left, 3=Right, 4=Top, 5=Bottom
    private Matrix4 tmpTransform = new Matrix4(); // Don't allocate a new matrix on every render call

    private float scale;

    public Skybox(TextureRegion[] faces, float cameraFarDinstance) {
        if (faces.length != 6) {
            throw new IllegalArgumentException(
                "Skybox requires exactly 6 texture faces."
            );
        }

        this.faces = faces;
        this.scale = cameraFarDinstance;
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        Vector3 camPos = camera.position;
        float halfScale = this.scale * 0.5f;

        // Front Face
        tmpTransform.setToTranslation(camPos.x, camPos.y, camPos.z - halfScale);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[0], tmpTransform);

        // Back Face
        tmpTransform.setToTranslation(camPos.x, camPos.y, camPos.z + halfScale);
        tmpTransform.rotate(Vector3.Y, 180);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[1], tmpTransform);

        // Left Face
        tmpTransform.setToTranslation(camPos.x - halfScale, camPos.y, camPos.z);
        tmpTransform.rotate(Vector3.Y, -90);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[2], tmpTransform);

        // Right Face
        tmpTransform.setToTranslation(camPos.x + halfScale, camPos.y, camPos.z);
        tmpTransform.rotate(Vector3.Y, 90);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[3], tmpTransform);

        // Top Face
        tmpTransform.setToTranslation(camPos.x, camPos.y + halfScale, camPos.z);
        tmpTransform.rotate(Vector3.X, -90);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[4], tmpTransform);

        // Bottom Face
        tmpTransform.setToTranslation(camPos.x, camPos.y - halfScale, camPos.z);
        tmpTransform.rotate(Vector3.X, 90);
        tmpTransform.scale(scale, scale, 1);
        batch.draw3D(faces[5], tmpTransform);
    }

    @Override
    public boolean isVisible(Camera camera) {
        return true; // Skybox is always visible
    }
}
