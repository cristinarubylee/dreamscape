package io.github.cristinarubylee.models;

import com.badlogic.gdx.physics.box2d.*;

import java.awt.*;
import java.awt.geom.RectangularShape;

public class Player extends GameObject{
    private int totalHealth;
    private int currHealth;

    public Player(World world, float x, float y){
        super(world, x, y, 1f, 1f);
        totalHealth = 100;
        currHealth = 100;
    }

    @Override
    protected void addFixtures(Body body) {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape square = new PolygonShape();

        square.setAsBox(0.5f, 0.5f);

        fixtureDef.filter.categoryBits = CATEGORY_PLAYER;
        fixtureDef.filter.maskBits = MASK_PLAYER;

        fixtureDef.shape = square;
        body.createFixture(fixtureDef);
        square.dispose();
    }


    public ObjectType getType() {
        return ObjectType.PLAYER;
    }

    public int getTotalHealth(){return totalHealth;}
    public int getCurrHealth(){return currHealth;}


    /**
     * Changes the current health by x, decreasing it if x is negative and increasing it if x is positive.
     *
     * Health is bounded by 0 and totalHealth values.
     */
    public void changeHealth(int x) {
        currHealth = x < 0 ? Math.max(currHealth + x, 0) : Math.min(currHealth + x, totalHealth);
    }
}
