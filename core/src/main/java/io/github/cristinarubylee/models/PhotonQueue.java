package io.github.cristinarubylee.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhotonQueue {
    private final Texture photonTexture;
    private final Array<Photon> photons;
    private final float speed;
    private final float spacing;
    private final float lifespan;
    private final World world;
    private final Sound photonSound;

    public PhotonQueue(World world, Texture photonTexture, Sound photonSound) {
        this.photonTexture = photonTexture;
        this.photons = new Array<>();
        this.world = world;
        this.photonSound = photonSound;

        // Set default values for photon queue
        speed = 5f;
        spacing = 2f;
        lifespan = 9999f;
    }

    public PhotonQueue(World world, Texture photonTexture, float speed, float spacing, float lifespan, Sound photonSound) {
        this.photonTexture = photonTexture;
        this.photonSound = photonSound;
        this.photons = new Array<>();
        this.world = world;
        this.speed = speed;
        this.spacing = spacing;
        this.lifespan = lifespan;
    }
    public void fire(float x, float y) {
        // Clean up trailing null-body photons
        while (!photons.isEmpty() && photons.peek().body() == null) {
            photons.pop();
        }

        // Check spacing condition OR if the list is now empty
        if (photons.isEmpty() || photons.peek().getX() > x + spacing) {
            Photon photon = new Photon(world, x, y);
            photon.setTexture(new TextureRegion(photonTexture));
            photons.add(photon);
            photonSound.play();
        }
    }


    public void update(float delta) {
        for (int i = photons.size - 1; i >= 0; i--) {
            Photon photon = photons.get(i);

            // Only update photon if it isn't set to be destroyed
            if (!photon.isDestroyed() && photon.body != null){
                if (photon.getLife() > lifespan){
                    photon.setDestroyed(true);
                } else if (photon.getX() > 16){
                    photon.setDestroyed(true);
                } else {
                    photon.translateX(speed * delta);
                    photon.incLife();
                }
            }
        }
    }

    public void removeDestroyed() {
        for (int i = photons.size - 1; i >= 0; i--){
            Photon photon = photons.get(i);
            if (photon.isDestroyed()){
                photons.removeIndex(i);
            }
        }
    }

    public Array<Photon> getPhotons() {
        return photons;
    }
}
