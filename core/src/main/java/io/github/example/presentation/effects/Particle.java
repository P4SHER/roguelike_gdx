package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Represents a single particle in the effects system.
 * Used in ParticlePool for object reuse.
 */
public class Particle {
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

        // Fade out as particle approaches end of life
        alpha = lifetime / duration;
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
