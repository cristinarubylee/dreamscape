package io.github.cristinarubylee.models;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {

    public enum ObjectType {
        PLAYER,
        PHOTON,
        NIGHTMARE
    }

    // Attributes for all game objects
    protected Vector2 position;
    protected Vector2 velocity;
    protected float width;
    protected float height;
    protected boolean destroyed;
    protected TextureRegion texture;
    protected Rectangle collider;

    /**
     * Constructs a trivial game object.
     * The created object has no position or size. These should be set by the subclasses.
     */
    public GameObject() {
        this.position = new Vector2(0.0f, 0.0f);
        this.velocity = new Vector2(0.0f, 0.0f);
        this.width = 1.0f;
        this.height = 1.0f;
        this.destroyed = false;
        this.collider = new Rectangle();
    }

    // Accessors
    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getSize() {
        return new Vector2(width, height);
    }

    public Vector2 getPosition() {
        return position;
    }

    /** Returns the x position of the object's bottom left corner */
    public float getX() {
        return position.x;
    }

    public void setX(float value) {
        position.x = value;
    }

    public float getY() {
        return position.y;
    }

    public void setY(float value) {
        position.y = value;
    }

    public void setPosition(Vector2 value) {
        this.position = value;
    }

    public void translateX(float value) {
        position.x += value;
    }

    public void translateY(float value) {
        position.y += value;
    }

    public void translate(Vector2 value) {
        position.x += value.x;
        position.y += value.y;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean value) {
        destroyed = value;
    }

    public abstract ObjectType getType();

    public Rectangle getCollider() {
        collider.set(position.x, position.y, width, height);
        return collider;
    }

    public void draw(Batch batch) {
        if (!destroyed) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
}
