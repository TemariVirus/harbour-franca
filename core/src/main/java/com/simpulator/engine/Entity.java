package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A rectangular entity with 3D position, 2D size, and 3D rotation.
 * The appearence is defined by a TextureRegion.
 */
public class Entity implements Moveable {

    /** Position, size and rotation encoded in a 4x4 matrix, in world units. */
    public Matrix4 transform;
    /** The texture region used to render the entity. */
    private TextureRegion textureRegion;
    /** Projected vertices for rendering. Prevents an allcoation on every render() call. */
    private final float[] vertexData = new float[20];

    public Entity(Matrix4 transform, TextureRegion textureRegion) {
        this.transform = transform.cpy();
        this.textureRegion = textureRegion;
    }

    public Entity(
        Vector3 position,
        Vector2 size,
        Quaternion rotation,
        TextureRegion textureRegion
    ) {
        this.transform = new Matrix4(
            position,
            rotation.nor(),
            new Vector3(size.x, size.y, 1)
        );
        this.textureRegion = textureRegion;
    }

    public Entity(
        Vector3 position,
        Vector2 size,
        Quaternion rotation,
        Texture texture
    ) {
        this.transform = new Matrix4(
            position,
            rotation.nor(),
            new Vector3(size.x, size.y, 1)
        );
        this.textureRegion = new TextureRegion(texture);
    }

    public Vector3 getPosition() {
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        return position;
    }

    public void setPosition(Vector3 position) {
        transform.setTranslation(position);
    }

    public Vector2 getSize() {
        Vector3 scale = new Vector3();
        transform.getScale(scale);
        return new Vector2(scale.x, scale.y);
    }

    public void setSize(Vector2 size) {
        transform.setToScaling(size.x, size.y, 1);
    }

    public Quaternion getRotation() {
        Quaternion rotation = new Quaternion();
        transform.getRotation(rotation);
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        Vector3 scale = new Vector3();
        transform.getScale(scale);
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        transform.set(position, rotation.nor(), scale);
    }

    public void setRotation(Vector3 axis, float radians) {
        transform.setToRotationRad(axis, radians);
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public void setTextureRegion(Texture textureRegion) {
        this.textureRegion = new TextureRegion(textureRegion);
    }

    @Override
    public void translate(Vector3 delta) {
        transform.val[Matrix4.M03] += delta.x;
        transform.val[Matrix4.M13] += delta.y;
        transform.val[Matrix4.M23] += delta.z;
    }

    public void scale(Vector2 scale) {
        transform.scale(scale.x, scale.y, 1);
    }

    public void rotate(Quaternion delta) {
        transform.rotate(delta);
    }

    @Override
    public void rotate(Vector3 axis, float radians) {
        transform.rotateRad(axis, radians);
    }

    public void transform(Matrix4 transform) {
        this.transform.mul(transform);
    }

    public void render(SpriteBatch batch, Camera camera) {
        // TODO: frustum culling
        for (int i = 0; i < 4; i++) {
            boolean isLeft = i < 2;
            boolean isBottom = (i == 0) || (i == 3);

            float x = isLeft ? -0.5f : 0.5f;
            float y = isBottom ? -0.5f : 0.5f;
            float u = isLeft ? textureRegion.getU() : textureRegion.getU2();
            float v = isBottom ? textureRegion.getV2() : textureRegion.getV();
            Vector3 vertPos = new Vector3(x, y, 0); // Local space
            vertPos.mul(transform); // World space
            camera.project(vertPos); // Screen space

            vertexData[i * 5 + 0] = vertPos.x;
            vertexData[i * 5 + 1] = vertPos.y;
            vertexData[i * 5 + 2] = Color.WHITE_FLOAT_BITS;
            vertexData[i * 5 + 3] = u;
            vertexData[i * 5 + 4] = v;
        }
        batch.draw(textureRegion.getTexture(), vertexData, 0, 20);
    }
}
