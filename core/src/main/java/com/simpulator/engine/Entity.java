package com.simpulator.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.simpulator.engine.graphics.EntityRenderer;
import com.simpulator.engine.graphics.Renderable;
import com.simpulator.engine.graphics.TextureBatch;

/**
 * An entity with 3D position, 3D size, and 3D rotation.
 */
public abstract class Entity implements Movable, Renderable {

    protected Vector3 position;
    protected Vector3 size;
    protected Quaternion rotation;
    /** Position, size and rotation encoded in a 4x4 matrix, in world units. */
    private Matrix4 transform;
    private boolean transformDirty = true;
    /** Controls how the entity is rendered. */
    protected EntityRenderer renderer;

    protected Entity(
        Vector3 position,
        Vector3 size,
        Quaternion rotation,
        EntityRenderer renderer
    ) {
        this.position = position.cpy();
        this.size = size.cpy();
        this.rotation = rotation.cpy();
        this.transform = new Matrix4();
        this.renderer = renderer;
    }

    /** Returns the center of the entity in world space. */
    public Vector3 getPosition() {
        return position.cpy();
    }

    /** Sets the center of the entity in world space. */
    public void setPosition(Vector3 position) {
        this.position.set(position);
        transformDirty = true;
    }

    public Vector3 getSize() {
        return size.cpy();
    }

    public void setSize(Vector3 size) {
        this.size.set(size);
        transformDirty = true;
    }

    public Quaternion getRotation() {
        return rotation.cpy();
    }

    public void setRotation(Quaternion rotation) {
        this.rotation.set(rotation);
        transformDirty = true;
    }

    /** Sets the rotation of the entity in world space. */
    public void setRotation(Vector3 axis, float radians) {
        this.rotation.setFromAxisRad(axis, radians);
        transformDirty = true;
    }

    /** Returns the entity's world space transform. */
    public Matrix4 getTransform() {
        if (transformDirty) {
            transform
                .idt()
                .translate(position)
                .rotate(rotation)
                .scale(size.x, size.y, size.z);
            transformDirty = false;
        }
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
        position.add(delta);
        transformDirty = true;
    }

    public void scale(Vector3 scale) {
        size.scl(scale);
        transformDirty = true;
    }

    public void rotate(Quaternion delta) {
        rotation.mul(delta);
        transformDirty = true;
    }

    @Override
    public void rotate(Vector3 axis, float radians) {
        rotation.mul(new Quaternion().setFromAxisRad(axis, radians));
        transformDirty = true;
    }

    @Override
    public boolean isVisible(Camera camera) {
        return renderer.isVisible(camera, this);
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
