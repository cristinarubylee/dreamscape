package io.github.cristinarubylee.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class GameObject {

    public Body body() {
        return body;
    }

    protected static final short CATEGORY_PLAYER = 0x0001;
    protected static final short CATEGORY_NIGHTMARE = 0x0002;
    protected static final short CATEGORY_PHOTON = 0x0004;

    // Define which categories collide with which
    protected static final short MASK_PLAYER = CATEGORY_NIGHTMARE;  // Player collides with nightmares
    protected static final short MASK_NIGHTMARE = CATEGORY_PLAYER | CATEGORY_PHOTON;  // Nightmares collide with player and photons
    protected static final short MASK_PHOTON = CATEGORY_NIGHTMARE;  // Photons collide with nightmares


    public enum ObjectType {
        PLAYER,
        PHOTON,
        NIGHTMARE
    }

    // Attributes for all game objects
    protected Body body;
    protected World world;
    private TextureRegion texture;
    private float width;
    private float height;
    private boolean isDestroyed;

    /**
     * Constructs a trivial game object.
     * The created object has no position or size. These should be set by the subclasses.
     */
    public GameObject(World world, float x, float y, float width, float height) {
        this.world = world;
        this.width = width;
        this.height = height;

        isDestroyed = false;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x,y);

        this.body = world.createBody(bodyDef);
        body.setUserData(this);
        addFixtures(body);
    }

    protected abstract void addFixtures(Body body);

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


    /** Returns the x position of the object's bottom left corner */
    public float getX() {
        return body.getPosition().x;
    }

    public void setX(float value) {
        body.setTransform(value, body.getPosition().y, body.getAngle());
    }

    public float getY() {
        return body.getPosition().y;
    }

    public void setY(float value) {
        body.setTransform(body.getPosition().x, value, body.getAngle());
    }

    public void setPosition(Vector2 value) {
        body.setTransform(value, body.getAngle());
    }

    public void translateX(float value) {
        body.setTransform(value + body.getPosition().x, body.getPosition().y, body.getAngle());
    }

    public void translateY(float value) {
        body.setTransform(body.getPosition().x, value + body.getPosition().y, body.getAngle());
    }

    public void translate(Vector2 value) {
        body.setTransform(body.getPosition().x + value.x, body.getPosition().y + value.y, body.getAngle());
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean value) {
        isDestroyed = value;
    }

    public abstract ObjectType getType();


    public void draw(Batch batch) {
        // Since Box2D bodies are centered while LibGDX draws from the bottom-left, we subtract to align
        if (!isDestroyed) {
            batch.draw(texture, body.getPosition().x - width/2, body.getPosition().y - height/2, width, height);
        }
    }
}
