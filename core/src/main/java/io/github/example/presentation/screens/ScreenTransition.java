package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Base class for screen transitions with fade animations.
 * Manages transition progress and alpha blending.
 */
public abstract class ScreenTransition {
    private float duration; // Total transition duration in seconds
    private float elapsedTime; // Time elapsed since transition started
    private boolean isCompleted;
    private ShapeRenderer shapeRenderer;

    /**
     * Creates a screen transition with specified duration.
     * @param duration Total transition duration in seconds
     */
    public ScreenTransition(float duration) {
        this.duration = duration;
        this.elapsedTime = 0;
        this.isCompleted = false;
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * Updates the transition progress.
     * @param delta Time elapsed since last frame in seconds
     */
    public void update(float delta) {
        if (isCompleted) {
            return;
        }

        elapsedTime += delta;
        if (elapsedTime >= duration) {
            elapsedTime = duration;
            isCompleted = true;
        }
    }

    /**
     * Gets the current progress of the transition (0.0 to 1.0).
     * @return Progress value between 0.0 and 1.0
     */
    public float getProgress() {
        return Math.min(elapsedTime / duration, 1.0f);
    }

    /**
     * Returns whether the transition is completed.
     * @return True if transition has finished
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Gets the alpha value for fade effect at current progress.
     * Default implementation returns progress value.
     * @return Alpha value between 0.0 and 1.0
     */
    protected float getAlpha() {
        return getProgress();
    }

    /**
     * Renders the transition effect on top of current rendering.
     * @param batch SpriteBatch for rendering
     * @param width Screen width
     * @param height Screen height
     */
    public abstract void render(SpriteBatch batch, int width, int height);

    /**
     * Resets the transition to initial state.
     */
    public void reset() {
        elapsedTime = 0;
        isCompleted = false;
    }

    /**
     * Sets the transition duration.
     * @param duration Duration in seconds
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }

    /**
     * Gets the transition duration.
     * @return Duration in seconds
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Helper method to draw a full-screen colored overlay with alpha blending.
     * Uses ShapeRenderer to draw a filled rectangle overlay.
     * @param batch SpriteBatch for rendering
     * @param width Screen width
     * @param height Screen height
     * @param color Color for the overlay
     * @param alpha Alpha blending value
     */
    protected void drawOverlay(SpriteBatch batch, int width, int height, Color color, float alpha) {
        batch.end();
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color.r, color.g, color.b, Math.max(0, Math.min(1, alpha)));
        shapeRenderer.rect(0, 0, width, height);
        shapeRenderer.end();
        
        batch.begin();
    }

    /**
     * Disposes ShapeRenderer resources.
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}

