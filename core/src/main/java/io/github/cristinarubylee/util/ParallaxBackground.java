package io.github.cristinarubylee.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class ParallaxBackground {

    private class Layer {
        float factor;
        Texture texture;

        public Layer(float factor, Texture texture){
            this.factor = factor;
            this.texture = texture;
            this.texture.setWrap(
                Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge
            );
        }

        private void draw(Batch batch, Vector2 position, Camera camera, int width, int height){
            int xOffset = (int) (position.x * factor) % texture.getWidth();
            int yOffset = (int) (position.y * factor) % texture.getHeight();

            TextureRegion region = new TextureRegion(texture);
            region.setRegionX(xOffset);
            region.setRegionY(yOffset);
            region.setRegionWidth(width);
            region.setRegionHeight(height);

            batch.draw(region, 0, 0, camera.viewportWidth, camera.viewportHeight);
        }


    }

    private ArrayList<Layer> layers = new ArrayList<>();
    private int speed;
    private Camera camera;
    private int width;
    private int height;
    public Vector2 position = new Vector2();

    public ParallaxBackground(int speed, Camera camera, int width, int height){
        this.speed = speed;
        this.camera = camera;
        this.width = width;
        this.height = height;
    }

    /**
    Adds layers to background (initial layers are drawn further back)
     */
    public void addLayer(float factor, Texture texture){
        layers.add(new Layer(factor, texture));
    }

    public void translateX(float delta){
        position.x = position.x < 0 ? 0 : position.x + speed * delta;
    }

    public void draw(Batch batch){
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Layer layer : layers) {
            layer.draw(batch, position, camera, width, height);
        }
        batch.end();
    }

}
