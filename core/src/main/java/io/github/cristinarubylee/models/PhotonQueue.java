package io.github.cristinarubylee.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhotonQueue {
    private final Texture photonTexture;
    private final Array<Photon> photons;
    private final float speed = 5f;
    private final float spacing = 2f;
    private final float lifespan = 9999f;
    private final World world;

    public PhotonQueue(World world, Texture photonTexture) {
        this.photonTexture = photonTexture;
        this.photons = new Array<>();
        this.world = world;
    }

    public void fire(float x, float y) {
        if (photons.isEmpty() || (photons.get(photons.size - 1)).getX() > x + spacing){
            Photon photon = new Photon(world, x, y);
            photon.setTexture(new TextureRegion(photonTexture));
            photons.add(photon);
        }
    }

    public void update(float delta) {
        for (int i = photons.size - 1; i >= 0; i--) {
            Photon photon = photons.get(i);
            if (photon.isDestroyed()){
                photons.removeIndex(i);
            }
            if (photon.getLife() > lifespan){
                photon.setDestroyed(true);
            }

            if (photon.getX() > 16){
                photon.setDestroyed(true);
            }
            else {
                photon.translateX(speed * delta);
                photon.incLife();
            }
        }
    }

    public Array<Photon> getPhotons() {
        return photons;
    }
}
