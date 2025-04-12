package io.github.cristinarubylee.models;

import com.badlogic.gdx.physics.box2d.*;

public class Photon extends GameObject{
    private int life = 0;

    public ObjectType getType() {
        return ObjectType.PHOTON;
    }

    public int getLife(){
        return life;
    }

    public void incLife(){
        life++;
    }

    public Photon(World world, float x, float y) {
        super(world, x, y, 0.2f, 0.2f);
    }

    @Override
    protected void addFixtures(Body body) {
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circle = new CircleShape();
        circle.setRadius(0.1f);

        fixtureDef.shape = circle;

        //fixtureDef.isSensor = true;

        fixtureDef.filter.categoryBits = CATEGORY_PHOTON;
        fixtureDef.filter.maskBits = MASK_PHOTON;

        body.createFixture(fixtureDef);
        body.setBullet(true);
        circle.dispose();
    }
}
