package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * An entity with 3D position, 2D size, and 3D rotation.
 */
public abstract class Entity implements Movable, Renderable {

    /** Position, size and rotation encoded in a 4x4 matrix, in world units. */
    protected Matrix4 transform;
    /** Controls how the entity is rendered. */
    protected EntityRenderer renderer;

    protected Entity(Matrix4 transform, EntityRenderer renderer) {
        this.transform = transform;
        this.renderer = renderer;
    }

    protected Entity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        EntityRenderer renderer
    ) {
        this.transform = new Matrix4(position, rotation.nor(), size);
        this.renderer = renderer;
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

    public Vector3 getSize() {
        Vector3 scale = new Vector3();
        return transform.getScale(scale);
    }

    public void setSize(Vector3 size) {
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        Quaternion rotation = new Quaternion();
        transform.getRotation(rotation);
        transform.set(position, rotation, size);
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
        Vector3 scale = new Vector3();
        transform.getScale(scale);
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        Quaternion rotation = new Quaternion().setFromAxis(axis, radians);
        transform.set(position, rotation.nor(), scale);
    }

    /** Returns the entity's world space transform. */
    public Matrix4 getTransform() {
        return transform.cpy();
    }

    public EntityRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(EntityRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void translate(Vector3 delta) {
        transform.val[Matrix4.M03] += delta.x;
        transform.val[Matrix4.M13] += delta.y;
        transform.val[Matrix4.M23] += delta.z;
    }

    public void scale(Vector3 scale) {
        transform.scale(scale.x, scale.y, scale.z);
    }

    public void rotate(Quaternion delta) {
        transform.rotate(delta);
    }

    @Override
    public void rotate(Vector3 axis, float radians) {
        transform.rotateRad(axis, radians);
    }

    /** Applies the given matrix to the entity's world space transform. */
    public void transform(Matrix4 transform) {
        this.transform.mul(transform);
    }

    @Override
    public boolean isVisible(Camera camera) {
        return renderer.isVisible(camera, this);
    }

    @Override
    public float getZOrder(Camera camera) {
        return renderer.getZOrder(camera, this);
    }

    @Override
    public void render(TextureBatch batch, Camera camera) {
        renderer.render(batch, camera, this);
    }

    /**
     * Update the entity's state. Usually called once per frame.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void update(float deltaTime) {}
}
