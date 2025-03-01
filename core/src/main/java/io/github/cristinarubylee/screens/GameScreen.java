package io.github.cristinarubylee.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import io.github.cristinarubylee.GDXRoot;
import io.github.cristinarubylee.controllers.*;
import io.github.cristinarubylee.models.*;

public class GameScreen implements Screen {
    final GDXRoot game;

    Texture backgroundTexture;
    Texture bucketTexture;
    Texture dropTexture;
    Sound dropSound;
    Music music;
    float dropTimer;

    InputController control;
    CollisionController collisionController;
    ShapeRenderer shapeRenderer;

    Player player;
    PhotonQueue photons;
    Array<Nightmare> nightmares;
    Array<GameObject> objects;

    public GameScreen(final GDXRoot game) {
        this.game = game;

        // Load images for the background, bucket, and droplet
        backgroundTexture = new Texture("background.png");
        bucketTexture = new Texture("bucket.png");
        dropTexture = new Texture("drop.png");

        // Load the drop sound effect and background music
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);

        nightmares = new Array<>();
        control = new InputController();
        photons = new PhotonQueue(dropTexture);

        player = new Player();
        player.setTexture(new TextureRegion(bucketTexture));
        player.setX(1);

        collisionController = new CollisionController(
            game.viewport.getWorldWidth(),
            game.viewport.getWorldHeight(),
            1
        );
        shapeRenderer = new ShapeRenderer();

        // Initialize list to store all GameObjects
        objects = new Array<>();
    }

    @Override
    public void show() {
        // Start the playback of the background music when the screen is shown
        music.play();
    }

    @Override
    public void render(float delta) {
        input();
        logic();
        draw();
    }

    private void input() {
        control.readInput();
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (control.getMovement() > 0) {
            player.translateY(speed * delta);
        }
        else if (control.getMovement() < 0) {
            player.translateY(-speed * delta);
        }

        if (control.didPressFire()) {
            photons.fire(player.getX() + player.getWidth(), player.getY() + player.getHeight() / 2);
        }

    }

    private void logic() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float playerHeight = player.getHeight();
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Clear objects list and add the player
        objects = new Array<>();
        objects.add(player);

        // Clamp player position within the screen bounds
        player.setY(MathUtils.clamp(player.getY(), 0, worldHeight - playerHeight));
        updateNightmares(deltaTime);
        updatePhotons(deltaTime);

        collisionController.processCollisions(objects);

        // Spawn nightmares at intervals
        dropTimer += deltaTime;
        if (dropTimer > 1f) {
            dropTimer = 0;
            createNightmare();
        }
    }


    private void updateNightmares(float deltaTime) {
        for (int i = nightmares.size - 1; i >= 0; i--) {
            Nightmare nightmare = nightmares.get(i);
            nightmare.translateX(-2f * deltaTime);

            if (nightmare.isDestroyed() || nightmare.getY() < -nightmare.getWidth()) {
                nightmares.removeIndex(i);
            } else {
                objects.add(nightmare);
            }
        }
    }

    private void updatePhotons(float deltaTime) {
        photons.update(deltaTime);
        for (Photon photon : photons.getPhotons()) {
            objects.add(photon);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        game.font.draw(game.batch, "Player Health: " + player.getCurrHealth(), 0, worldHeight);
        game.font.draw(game.batch, "Total Objects: " + objects.size, 0, worldHeight - 1);

        for (GameObject object : objects) {
            object.draw(game.batch);
        }

        game.batch.end();

        // Draw colliders for debugging
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        for (GameObject object : objects) {
            Rectangle collider = object.getCollider();
            shapeRenderer.rect(collider.x, collider.y, collider.width, collider.height);
        }
        shapeRenderer.end();
    }

    private void createNightmare() {
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        Nightmare nightmare = new Nightmare();
        nightmare.setTexture(new TextureRegion(dropTexture));
        nightmare.setSize(1, 1);
        nightmare.setX(worldWidth);
        nightmare.setY(MathUtils.random(0f, worldHeight - 1));
        nightmares.add(nightmare);
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        dropSound.dispose();
        music.dispose();
        dropTexture.dispose();
        bucketTexture.dispose();
    }
}
