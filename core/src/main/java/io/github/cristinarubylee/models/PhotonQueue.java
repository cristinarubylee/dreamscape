package io.github.cristinarubylee.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PhotonQueue {
    private final Texture photonTexture;
    private final Array<Photon> photons;
    private final float speed = 2f;
    private final float spacing = 1f;

    public PhotonQueue(Texture photonTexture) {
        this.photonTexture = photonTexture;
        this.photons = new Array<>();
    }

    public void fire(float x, float y) {
        if (photons.isEmpty() || (photons.get(photons.size - 1)).getX() > x + spacing){
            Photon photon = new Photon();
            photon.setTexture(new TextureRegion(photonTexture));
            photon.setSize(0.5f, 0.5f);
            photon.setPosition(new Vector2(x, y));
            photons.add(photon);
        }
    }

    public void update(float delta) {
        for (int i = photons.size - 1; i >= 0; i--) {
            Photon photon = photons.get(i);
            if (photon.isDestroyed()){
                photons.removeIndex(i);
            }
            else {
                photon.translateX(speed * delta);
            }
        }
    }

    public Array<Photon> getPhotons() {
        return photons;
    }
}
