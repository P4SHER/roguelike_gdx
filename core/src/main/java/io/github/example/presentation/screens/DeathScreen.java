package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;

/**
 * Death screen displayed when the player dies.
 * Shows final statistics: score, floor reached, enemies defeated, items found.
 * Buttons: Restart New Game, Return to Menu.
 * Replaces current screen (not overlay).
 */
public class DeathScreen implements Screen {
    private final DeathCallback callback;
    private int finalScore;
    private int floorReached;
    private int enemiesDefeated;
    private int itemsFound;
    private static Texture filledTexture;
    private static final Object textureLock = new Object();

    public interface DeathCallback {
        void onRestart();
        void onReturnToMenu();
    }

    public DeathScreen(DeathCallback callback) {
        this.callback = callback;
        this.finalScore = 0;
        this.floorReached = 1;
        this.enemiesDefeated = 0;
        this.itemsFound = 0;
    }

    /**
     * Sets the final game statistics.
     */
    public void setGameStats(int score, int floor, int enemies, int items) {
        this.finalScore = score;
        this.floorReached = floor;
        this.enemiesDefeated = enemies;
        this.itemsFound = items;
        Logger.info("Death stats set: Score=" + score + ", Floor=" + floor + 
            ", Enemies=" + enemies + ", Items=" + items);
    }

    @Override
    public void show() {
        Logger.info("DeathScreen показан");
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Draw dark overlay
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(getFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Render game over title
        renderGameOverTitle(batch);

        // Render final statistics
        renderStatistics(batch);

        // Render action buttons
        renderButtons(batch);
    }

    /**
     * Renders the "GAME OVER" title.
     */
    private void renderGameOverTitle(SpriteBatch batch) {
        float titleY = Constants.SCREEN_HEIGHT * 0.8f;
        batch.setColor(1f, 0.2f, 0.2f, 1f); // Red
        Logger.info("═══════════════════════════════════");
        Logger.info("          GAME OVER");
        Logger.info("═══════════════════════════════════");
        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders the final game statistics.
     */
    private void renderStatistics(SpriteBatch batch) {
        float centerX = Constants.SCREEN_WIDTH / 2f;
        float statsStartY = Constants.SCREEN_HEIGHT * 0.65f;
        float statSpacing = 50;

        batch.setColor(0.9f, 0.9f, 0.9f, 1f);

        // Final Score
        Logger.info("═══════════════════════════════════");
        Logger.info("FINAL STATISTICS");
        Logger.info("═══════════════════════════════════");
        Logger.info("Score: " + finalScore);
        Logger.info("Floor Reached: " + floorReached);
        Logger.info("Enemies Defeated: " + enemiesDefeated);
        Logger.info("Items Found: " + itemsFound);
        Logger.info("═══════════════════════════════════");

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders the action buttons.
     */
    private void renderButtons(SpriteBatch batch) {
        float centerX = Constants.SCREEN_WIDTH / 2f;
        float buttonY = Constants.SCREEN_HEIGHT * 0.2f;
        float buttonWidth = 200;
        float buttonHeight = 50;
        float buttonSpacing = 20;

        // Restart button
        float restartX = centerX - buttonWidth - buttonSpacing / 2f;
        batch.setColor(0.2f, 0.5f, 0.2f, 0.8f);
        batch.draw(getFilledTexture(), restartX, buttonY, buttonWidth, buttonHeight);

        // Menu button
        float menuX = centerX + buttonSpacing / 2f;
        batch.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        batch.draw(getFilledTexture(), menuX, buttonY, buttonWidth, buttonHeight);

        batch.setColor(1, 1, 1, 1);

        Logger.debug("Buttons: Restart | Return to Menu");
    }

    /**
     * Handles restart button - starts a new game.
     */
    public void restart() {
        if (callback != null) {
            Logger.info("Restarting game...");
            callback.onRestart();
        }
    }

    /**
     * Handles menu button - returns to main menu.
     */
    public void returnToMenu() {
        if (callback != null) {
            Logger.info("Returning to menu...");
            callback.onReturnToMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Death screen adapts to window size
    }

    @Override
    public void hide() {
        Logger.debug("DeathScreen hidden");
    }

    @Override
    public void dispose() {
        disposeFilledTexture();
        Logger.debug("DeathScreen disposed");
    }

    @Override
    public String getName() {
        return "DeathScreen";
    }

    /**
     * Gets or creates a static white texture for drawing rectangles.
     */
    private static Texture getFilledTexture() {
        if (filledTexture == null) {
            synchronized (textureLock) {
                if (filledTexture == null) {
                    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                    pixmap.setColor(1, 1, 1, 1);
                    pixmap.fill();
                    filledTexture = new Texture(pixmap);
                    pixmap.dispose();
                    Logger.debug("DeathScreen filledTexture created");
                }
            }
        }
        return filledTexture;
    }

    /**
     * Disposes the static texture. Called from PresentationLayer.dispose().
     */
    public static void disposeFilledTexture() {
        synchronized (textureLock) {
            if (filledTexture != null) {
                filledTexture.dispose();
                filledTexture = null;
                Logger.debug("DeathScreen filledTexture disposed");
            }
        }
    }
}
