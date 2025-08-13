package io.github.cristinarubylee.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import io.github.cristinarubylee.GDXRoot;
import io.github.cristinarubylee.controllers.*;
import io.github.cristinarubylee.models.*;
import io.github.cristinarubylee.util.ParallaxBackground;

public class GameScreen implements Screen {
    // Constants
    private static final float TIME_STEP = 1/60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final float PLAYER_SPEED = 4f;
    private static final float NIGHTMARE_SPAWN_INTERVAL = 2;
    private static final String PATH_PREFIX = "assets/";

    // Game reference
    private final GDXRoot game;

    // Resources
    private Texture backgroundTexture;
    private Texture bucketTexture;
    private Texture dropTexture;
    private Sound dropSound;
    private Music music;

    // Physics and rendering
    private World world;
    private CollisionController collisionController;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    // Game state
    private float dropTimer;
    private boolean debugMode;
    private boolean pause;
    private ParallaxBackground bg;

    // Controllers
    private InputController control;

    // Game objects
    private Player player;
    private PhotonQueue photons;
    private Array<NightmareQueue> nightmareQueues;
    private Array<Body> bodiesToDestroy;
    private Array<GameObject> objects;

    public GameScreen(final GDXRoot game) {
        this.game = game;
        initPhysics();
        loadResources();
        initGameObjects();

        debugMode = false;
        pause = false;
        bodiesToDestroy = new Array<>();
    }

    private void initPhysics() {
        world = new World(new Vector2(0, 0), false);
        collisionController = new CollisionController();
        world.setContactListener(collisionController);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera(16, 10);
        camera.position.set(8, 5, 0);
        camera.update();
    }

    private void loadResources() {
        // Load images
        backgroundTexture = game.assetManager.get(PATH_PREFIX + "background.png");
        bucketTexture = game.assetManager.get(PATH_PREFIX + "bucket.png");
        dropTexture = game.assetManager.get(PATH_PREFIX + "drop.png");

        // Load audio
        dropSound = game.assetManager.get(PATH_PREFIX + "drop.mp3");
        music = game.assetManager.get(PATH_PREFIX + "music.mp3");
        music.setLooping(true);
        music.setVolume(0.5f);
    }

    private void initGameObjects() {
        control = new InputController();

        // Initialize player
        player = new Player(world, 1, 5);
        player.setTexture(new TextureRegion(bucketTexture));

        // Initialize projectiles and enemies
        photons = new PhotonQueue(world, dropTexture, dropSound);
        nightmareQueues = new Array<>();

        // Initialize object tracking
        objects = new Array<>();

        // Initialize parallax
        bg = new ParallaxBackground(10, camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        bg.addLayer(3f, backgroundTexture);

    }

    @Override
    public void show() {
         music.play();
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        input();
        logic(delta);
        draw();

        // Process physics step
        world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        // Clean up destroyed bodies after physics step
        cleanupBodies();

        if (debugMode) {
            debugRenderer.render(world, camera.combined);
        }
    }

    private void input() {
        control.readInput();
        float delta = Gdx.graphics.getDeltaTime();

        if (!pause) {
            // Player movement
            if (control.getMovement() > 0) {
                player.translateY(PLAYER_SPEED * delta);
            } else if (control.getMovement() < 0) {
                player.translateY(-PLAYER_SPEED * delta);
            }

            // Player firing
            if (control.didPressFire()) {
                photons.fire(player.getX() + player.getWidth(), player.getY() + player.getHeight() / 2);
            }
        }

        // Debug toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            debugMode = !debugMode;
        }

        // Pause toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            pause = !pause;
        }
    }

    private void logic(float deltaTime) {
        if (pause) {
            return;
        }

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float playerHeight = player.getHeight();

        // Clear objects list and add the player
        objects.clear();
        objects.add(player);

        // Clamp player position within the screen bounds
        player.setY(MathUtils.clamp(player.getY(), playerHeight/2, worldHeight - playerHeight/2));
        player.setX(MathUtils.clamp(player.getX(), playerHeight/2, worldWidth - playerHeight/2));

        // Update game objects
        updateNightmares(deltaTime);
        updatePhotons(deltaTime);

        // Handle spawning new nightmares
        spawnNightmares(deltaTime, worldWidth, worldHeight);

        bg.translateX(deltaTime);
    }

    private void spawnNightmares(float deltaTime, float worldWidth, float worldHeight) {
        // Spawn nightmares at intervals
        dropTimer -= deltaTime;
        if (dropTimer <= 0f) {
            dropTimer = NIGHTMARE_SPAWN_INTERVAL;

            float centerY = MathUtils.random(2, worldHeight - 2);
            nightmareQueues.add(new NightmareQueue(world, worldWidth + 1, centerY, NightmareQueue.NightmareType.CIRCLE));
        }
    }

    private void updatePhotons(float deltaTime) {
        for (Photon photon : photons.getPhotons()) {
            if (photon.isDestroyed()) {
                markForDestruction(photon.body());
            } else {
                objects.add(photon);
            }
        }
        photons.removeDestroyed();
        photons.update(deltaTime);
    }

    private void updateNightmares(float deltaTime) {
        for (NightmareQueue nightmareQueue : nightmareQueues) {
            for (Nightmare nightmare : nightmareQueue.getNightmares()) {
                if (nightmare == null){
                    continue;
                }

                if (nightmare.isDestroyed()) {
                    markForDestruction(nightmare.body());
                } else {
                    objects.add(nightmare);
                }
            }
            nightmareQueue.removeDestroyed();
            nightmareQueue.update(deltaTime);
        }
    }

    private void markForDestruction(Body body) {
        if (body != null && !bodiesToDestroy.contains(body, true)) {
            bodiesToDestroy.add(body);
        }
    }

    private void cleanupBodies() {
        // Remove bodies after physics step to avoid concurrent modification
        for (Body body : bodiesToDestroy) {
            if (body != null) {
                GameObject obj = (GameObject)(body.getUserData());
                obj.setBody(null);
                world.destroyBody(body);
            }
        }
        bodiesToDestroy.clear();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();

        bg.draw(game.batch);

        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Draw background
//        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);

        // Draw UI elements
        game.font.draw(game.batch, "Player Health: " + player.getCurrHealth(), 0, worldHeight);
        game.font.draw(game.batch, "Total Objects: " + objects.size, 0, worldHeight - 1);

        // Draw all game objects
        for (GameObject object : objects) {
            object.draw(game.batch);
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        pause = true;
    }

    @Override
    public void resume() {
        pause = false;
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    @Override
    public void dispose() {
        // Dispose of all resources to prevent memory leaks
        backgroundTexture.dispose();
        dropSound.dispose();
        music.dispose();
        dropTexture.dispose();
        bucketTexture.dispose();
        world.dispose();
        debugRenderer.dispose();
    }
}
