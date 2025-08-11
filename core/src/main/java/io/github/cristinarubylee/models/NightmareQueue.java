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
    private float speed = 5f; // Represents bullets shot per second
    private float radius = 1;

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
                    float tempX = radius * MathUtils.cosDeg(i * 45);
                    float tempY = radius * MathUtils.sinDeg(i * 45);
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
        float angularSpeed = 100f;
        currentAngle = (currentAngle + angularSpeed * delta) % 360;

        center.add(-speed * delta, 0);

        for (int i = 0; i < nightmares.size; i++){
            Nightmare nightmare = nightmares.get(i);

            // Only update the nightmare if it isn't set to be destroyed
            if (nightmare != null && !nightmare.isDestroyed() && nightmare.body != null){

                float angle = (currentAngle + 45 * i);

                nightmare.setX(center.x + radius * MathUtils.cosDeg(angle));
                nightmare.setY(center.y + radius * MathUtils.sinDeg(angle));


                // Applying force leads to over-shooting: creates creepy, almost organic movement!!
//                float force = 3f;
//                Vector2 target = new Vector2(center.x + radius * MathUtils.cosDeg(angle),center.y + radius * MathUtils.sinDeg(angle) );
//                Vector2 currentPos = new Vector2(nightmare.getX(), nightmare.getY());
//                Vector2 direction = target.sub(currentPos);
//                nightmare.body().applyForceToCenter(direction.scl(force), true);

                if (nightmare.getX() < -2 * nightmare.getWidth()) {
                    nightmare.setDestroyed(true);
                }
            }
        }

    }

    public void removeDestroyed(){
        for (int i = nightmares.size - 1; i >= 0; i--){
            Nightmare nightmare = nightmares.get(i);
            if (nightmare != null && nightmare.isDestroyed()){
                nightmares.set(i, null);
            }
        }
    }

    public Array<Nightmare> getNightmares() {
        return nightmares;
    }
}
