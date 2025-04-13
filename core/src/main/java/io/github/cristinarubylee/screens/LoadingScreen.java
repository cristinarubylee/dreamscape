package io.github.cristinarubylee.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.cristinarubylee.GDXRoot;

public class LoadingScreen implements Screen {
    // Game reference
    private final GDXRoot game;

    private float worldWidth;
    private float worldHeight;

    private Texture splash;
    private Texture playButton;
    private Texture progress;
    private TextureRegion backleft;
    private TextureRegion backright;
    private TextureRegion background;
    private TextureRegion foreleft;
    private TextureRegion foreright;
    private TextureRegion foreground;

    public LoadingScreen(final GDXRoot game) {
        this.game = game;

        worldWidth = game.viewport.getWorldWidth();
        worldHeight = game.viewport.getWorldHeight();

        game.assetManager.load("assets/loading/play.png", Texture.class);
        game.assetManager.load("assets/loading/progress.png", Texture.class);
        game.assetManager.load("assets/loading/splash.png", Texture.class);

        game.assetManager.finishLoading();

        splash = game.assetManager.get("assets/loading/splash.png");
        playButton = game.assetManager.get("assets/loading/play.png");
        progress = game.assetManager.get("assets/loading/progress.png");

        backleft = new TextureRegion(progress, 0, 0, 24, 45);
        backright = new TextureRegion(progress, 296, 0, 24, 45);
        background = new TextureRegion(progress, 24, 0, 272, 45);

        foreleft = new TextureRegion(progress, 0, 45, 24, 45);
        foreright = new TextureRegion(progress, 296, 45, 24, 45);
        foreground = new TextureRegion(progress, 24, 45, 272, 45);

        game.assetManager.load("assets/background.png", Texture.class);
        game.assetManager.load("assets/bucket.png", Texture.class);
        game.assetManager.load("assets/drop.png", Texture.class);
        game.assetManager.load("assets/music.mp3", Music.class);
        game.assetManager.load("assets/drop.mp3", Sound.class);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.assetManager.update();
        draw();

        if (game.assetManager.getProgress() >= 1.0f && Gdx.input.justTouched()) {
            // Convert screen coordinates to world coordinates
            Vector2 touch = game.viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            float buttonX = worldWidth / 2f - 2f;
            float buttonY = worldHeight * 0.1f;
            float buttonWidth = 4f;
            float buttonHeight = 4f;

            if (touch.x >= buttonX && touch.x <= buttonX + buttonWidth &&
                touch.y >= buttonY && touch.y <= buttonY + buttonHeight) {

                game.setScreen(new GameScreen(game));
                this.dispose();
            }
        }

    }

    private void draw(){
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.batch.draw(splash, 0,0, worldWidth, worldHeight);

        if (game.assetManager.getProgress() < 1.0f) {
            drawProgress();
        } else {
            game.batch.draw(playButton, worldWidth/2 - 2, worldHeight * 0.1f, 4, 4);
        }

        game.batch.end();
    }

    /**
     * Updates the progress bar according to the loading progress
     */
    private void drawProgress() {
        float barWidth = 12f;
        float barHeight = 1f;
        float capWidth = 1f;
        float centerX = worldWidth / 2f;
        float centerY = worldHeight * 0.2f;

        float loaded = game.assetManager.getProgress();

        float barX = centerX - barWidth / 2f;
        float progressX = barX + capWidth;
        float progressW = loaded * (barWidth - 2 * capWidth);

        // Background
        game.batch.draw(backleft, barX, centerY, capWidth, barHeight);
        game.batch.draw(background, barX + capWidth, centerY, barWidth - 2 * capWidth, barHeight);
        game.batch.draw(backright, barX + barWidth - capWidth, centerY, capWidth, barHeight);

        // Foreground
        game.batch.draw(foreleft, barX, centerY, capWidth, barHeight);
        if (loaded > 0) {
            game.batch.draw(foreground, progressX, centerY, progressW, barHeight);
            game.batch.draw(foreright, progressX + progressW, centerY, capWidth, barHeight);
        } else {
            game.batch.draw(foreright, progressX, centerY, capWidth, barHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        game.assetManager.unload("assets/loading/splash.png");
        game.assetManager.unload("assets/loading/play.png");
        game.assetManager.unload("assets/loading/progress.png");
    }
}
