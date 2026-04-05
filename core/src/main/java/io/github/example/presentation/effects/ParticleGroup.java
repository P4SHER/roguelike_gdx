package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.util.Logger;

/**
 * Groups particles by effect type.
 * Manages lifecycle and rendering of a specific effect category.
 *
 * Each ParticleGroup:
 * - Owns its own ParticlePool for efficient memory management
 * - Tracks animation progress and effect state
 * - Supports update/render lifecycle
 * - Can be disposed independently
 */
public class ParticleGroup {
    private final EffectType effectType;
    private final ParticlePool pool;
    private float elapsedTime;
    private int totalCreated;

    /**
     * Initialize a particle group for specific effect type.
     *
     * @param effectType Type of effect this group manages
     * @param maxParticles Maximum particles for this group's pool
     */
    public ParticleGroup(EffectType effectType, int maxParticles) {
        this.effectType = effectType;
        this.pool = new ParticlePool(maxParticles);
        this.elapsedTime = 0;
        this.totalCreated = 0;

        Logger.debug("ParticleGroup created for " + effectType + " with " + maxParticles + " particles");
    }

    /**
     * Update all particles in this group.
     * Removes inactive particles automatically.
     *
     * @param delta Time since last frame (seconds)
     */
    public void update(float delta) {
        elapsedTime += delta;
        pool.update(delta);
    }

    /**
     * Render all particles in this group.
     *
     * @param batch SpriteBatch for rendering
     * @param camera Camera for view matrix
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        pool.render(batch, camera);
    }

    /**
     * Clear all particles from this group.
     */
    public void clear() {
        pool.clear();
        totalCreated = 0;
        elapsedTime = 0;
    }

    /**
     * Dispose and cleanup resources.
     */
    public void dispose() {
        pool.clear();
        Logger.debug("ParticleGroup " + effectType + " disposed");
    }

    /**
     * Get the particle pool for this group.
     * Used by ParticleSystem to create particles.
     *
     * @return ParticlePool instance
     */
    public ParticlePool getParticlePool() {
        return pool;
    }

    /**
     * Get active particle count in this group.
     *
     * @return Number of active particles
     */
    public int getActiveCount() {
        return pool.getActiveCount();
    }

    /**
     * Get pooled (available) particle count.
     *
     * @return Number of particles in pool
     */
    public int getPooledCount() {
        return pool.getPooledCount();
    }

    /**
     * Get effect type for this group.
     *
     * @return EffectType this group manages
     */
    public EffectType getEffectType() {
        return effectType;
    }

    /**
     * Get total time elapsed since group creation.
     * Useful for animation timing.
     *
     * @return Elapsed time in seconds
     */
    public float getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Get total number of particles created in this group's lifetime.
     *
     * @return Total particles created
     */
    public int getTotalCreated() {
        return totalCreated;
    }

    /**
     * Increment particle creation counter.
     * Called internally when particles are created.
     */
    protected void incrementParticleCount() {
        totalCreated++;
    }
}
