package io.github.cristinarubylee.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class NightmareQueue {
    private final TextureRegion basic = new TextureRegion(new Texture("drop.png"));

    private final Array<Nightmare> nightmares;

    private Vector2 center;
    private float currentAngle = 0;
    private float speed = 2f;

    public enum NightmareType {
        CIRCLE,
        WALL
    }
    public NightmareQueue(World world, float x, float y, NightmareType nightmareType) {
        nightmares = new Array<>();
        center = new Vector2(x,y);

        switch (nightmareType){
            case CIRCLE:
                for (int i = 0; i < 8; i++){
                    float tempX = MathUtils.cosDeg(i * 45);
                    float tempY = MathUtils.sinDeg(i * 45);
                    nightmares.add(createNightmare(world, tempX + x, tempY + y));
                }
            case WALL:

        }
    }

    private Nightmare createNightmare(World world, float x, float y){
        Nightmare nightmare = new Nightmare(world, x, y);
        nightmare.setTexture(basic);
        return nightmare;
    }

    public void update(float delta){
        float angularSpeed = 10f;
        currentAngle += angularSpeed * delta;

        for (int i = 0; i < nightmares.size; i++){
            Nightmare nightmare = nightmares.get(i);

            if (nightmare.getX() < -2*nightmare.getWidth()){
                nightmare.setDestroyed(true);
            }

            if (nightmare.isDestroyed()){
                nightmares.removeIndex(i);
            }
            else {
                float angle = (currentAngle + 45 * i) + (angularSpeed * delta);

                nightmare.setX(center.x + MathUtils.cosDeg(angle));
                nightmare.setY(center.y + MathUtils.sinDeg(angle));
            }
        }

        center.add(-speed * delta, 0);
    }

    public Array<Nightmare> getNightmares() {
        return nightmares;
    }
}
