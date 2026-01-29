package com.simpulator.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Entity {

    /** The x position of the bottom left corner of the entity, in pixels. */
    private float x;
    /** The y position of the bottom left corner of the entity, in pixels. */
    private float y;
    /** The width of the entity, in pixels. */
    private float width;
    /** The height of the entity, in pixels. */
    private float height;
    /** The counter-clockwise rotation of the entity, in degrees. */
    private float rotation;
    /** The texture region used to render the entity. */
    private TextureRegion textureRegion;

    public Entity(
        float x,
        float y,
        float width,
        float height,
        TextureRegion textureRegion
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0f;
        this.textureRegion = textureRegion;
    }

    public Entity(
        float x,
        float y,
        float width,
        float height,
        Texture texture
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0f;
        this.textureRegion = new TextureRegion(texture);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
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

    public void translate(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public void scale(float scaleX, float scaleY) {
        this.width *= scaleX;
        this.height *= scaleY;
    }

    /** Rotates the entity by the specified amount in degrees, counter-clockwise. */
    public void rotate(float deltaRotation) {
        this.rotation += deltaRotation;
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureRegion, x, y, 0, 0, width, height, 1, 1, rotation);
    }
}
