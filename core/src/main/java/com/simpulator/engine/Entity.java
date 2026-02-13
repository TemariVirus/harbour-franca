package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

/**
 * A rectangular entity with 3D position, 2D size, and 3D rotation.
 * The appearence is defined by a TextureRegion and tint.
 */
public class Entity implements Moveable, Renderable<SpriteBatch> {

    /** Position, size and rotation encoded in a 4x4 matrix, in world units. */
    protected Matrix4 transform;
    /** The tint color applied when rendering the entity. */
    protected Color tint = Color.WHITE.cpy();
    /** The texture region used to render the entity. */
    protected TextureRegion textureRegion;

    protected Entity(Matrix4 transform, TextureRegion textureRegion) {
        this.transform = transform;
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

    /** Returns the center of the entity in world space. */
    public Vector3 getPosition() {
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        return position;
    }

    /** Sets the center of the entity in world space. */
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

    /** Sets the rotation of the entity in world space. */
    public void setRotation(Vector3 axis, float radians) {
        transform.setToRotationRad(axis, radians);
    }

    /** Returns the entity's world space transform. */
    public Matrix4 getTransform() {
        return transform.cpy();
    }

    public Color getTint() {
        return tint.cpy();
    }

    public void setTint(Color tint) {
        this.tint = tint.cpy();
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    /** Returns the vertex in local space, indexed in clockwise order. */
    public Vector3 getLocalVertex(int index) {
        switch (index) {
            case 0:
                return new Vector3(-0.5f, -0.5f, 0); // Bottom left
            case 1:
                return new Vector3(-0.5f, 0.5f, 0); // Top left
            case 2:
                return new Vector3(0.5f, 0.5f, 0); // Top right
            case 3:
                return new Vector3(0.5f, -0.5f, 0); // Bottom right
            default:
                throw new IllegalArgumentException(
                    "Index must be in range [0, 3]"
                );
        }
    }

    /** Returns the vertex in world space, indexed in clockwise order. */
    public Vector3 getVertex(int index) {
        return getLocalVertex(index).mul(transform);
    }

    /** Returns the vertex in world space with a local Z offset, indexed in clockwise order. */
    public Vector3 getVertex(int index, float localZ) {
        Vector3 localVertex = getLocalVertex(index);
        localVertex.z = localZ;
        return localVertex.mul(transform);
    }

    /** Returns all vertices in world space in clockwise order. */
    public Vector3[] getVertices() {
        return new Vector3[] {
            getVertex(0),
            getVertex(1),
            getVertex(2),
            getVertex(3),
        };
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

    /**
     * Update the entity's state. Usually called once per frame.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void update(float deltaTime) {}

    @Override
    public void rotate(Vector3 axis, float radians) {
        transform.rotateRad(axis, radians);
    }

    /** Applies the given matrix to the entity's world space transform. */
    public void transform(Matrix4 transform) {
        this.transform.mul(transform);
    }

    /** Returns whether the entity is currently in view of the given camera. */
    public boolean isVisible(Camera camera) {
        OrientedBoundingBox obb = new OrientedBoundingBox(
            new BoundingBox(getLocalVertex(0), getLocalVertex(2)),
            transform
        );
        return camera.frustum.boundsInFrustum(obb);
    }

    @Override
    public void render(SpriteBatch batch, Camera camera) {
        if (!isVisible(camera)) {
            return;
        }

        float[] vertexData = new float[20];
        for (int i = 0; i < 4; i++) {
            boolean isLeft = i < 2;
            boolean isBottom = (i == 0) || (i == 3);

            float u = isLeft ? textureRegion.getU() : textureRegion.getU2();
            float v = isBottom ? textureRegion.getV2() : textureRegion.getV();
            Vector3 vertPos = getVertex(i); // World space
            camera.project(vertPos); // Screen space

            vertexData[i * 5 + 0] = vertPos.x;
            vertexData[i * 5 + 1] = vertPos.y;
            vertexData[i * 5 + 2] = tint.toFloatBits();
            vertexData[i * 5 + 3] = u;
            vertexData[i * 5 + 4] = v;
        }
        batch.draw(textureRegion.getTexture(), vertexData, 0, 20);
    }
}
