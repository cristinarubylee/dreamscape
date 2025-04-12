package io.github.cristinarubylee.models;

import com.badlogic.gdx.physics.box2d.*;

public class Nightmare extends GameObject{
    int damage = 10;

    public ObjectType getType() {
        return ObjectType.NIGHTMARE;
    }

    public int getDamage(){
        return damage;
    }

    public Nightmare(World world, float x, float y) {
        super(world, x, y, 1f, 1f);
    }

    @Override
    protected void addFixtures(Body body) {
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        fixtureDef.shape = circle;

        fixtureDef.isSensor = true;

        fixtureDef.filter.categoryBits = CATEGORY_NIGHTMARE;
        fixtureDef.filter.maskBits = MASK_NIGHTMARE;
        fixtureDef.filter.groupIndex = -2;

        body.createFixture(fixtureDef);
        circle.dispose();
    }


}
