package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Represents a single particle in the effects system.
 * Used in ParticlePool for object reuse.
 */
public class Particle {
    // Alpha fading configuration
    private static final float ALPHA_FADE_START_PERCENT = 0.7f; // Full opacity until 70% of lifetime

    public float x, y;              // Position
    public float vx, vy;            // Velocity
    public float lifetime;          // Current time to live
    public float duration;          // Total duration
    public Sprite sprite;           // Visual representation
    public float scale = 1.0f;      // Size multiplier
    public float alpha = 1.0f;      // Transparency (0-1)
    public boolean active = false;  // Is this particle active?

    /**
     * Initialize particle with position and velocity.
     */
    public void init(float x, float y, float vx, float vy, float duration, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifetime = duration;
        this.duration = duration;
        this.sprite = sprite;
        this.alpha = 1.0f;
        this.scale = 1.0f;
        this.active = true;
    }

    /**
     * Update particle: position, lifetime, alpha fade.
     * Alpha remains full for first 70% of lifetime, then fades smoothly over last 30%.
     */
    public void update(float delta) {
        if (!active) return;

        // Update position based on velocity
        x += vx * delta;
        y += vy * delta;

        // Update lifetime
        lifetime -= delta;
        if (lifetime <= 0) {
            active = false;
            return;
        }

        // Calculate progress from 0 (start) to 1 (end)
        float progress = 1.0f - (lifetime / duration);
        
        // Full opacity until 70% through lifetime, then fade over last 30%
        if (progress < ALPHA_FADE_START_PERCENT) {
            alpha = 1.0f;
        } else {
            // Fade from 1.0 to 0.0 over the last 30% of lifetime
            float fadeProgress = (progress - ALPHA_FADE_START_PERCENT) / (1.0f - ALPHA_FADE_START_PERCENT);
            alpha = 1.0f - fadeProgress;
        }
    }

    /**
     * Reset particle to inactive state.
     */
    public void reset() {
        active = false;
        lifetime = 0;
        duration = 0;
        alpha = 1.0f;
        scale = 1.0f;
        sprite = null;
    }
}
