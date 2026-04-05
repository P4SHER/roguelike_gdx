package io.github.example.presentation.effects;

import io.github.example.presentation.profiling.PerformanceProfiler;
import io.github.example.presentation.util.Logger;

/**
 * Performance test for the particle system.
 * Tests rendering performance with varying numbers of particles.
 */
public class ParticlePerformanceTest {
    private final ParticlePool particlePool;
    private final PerformanceProfiler profiler;
    private int activeParticles;

    public ParticlePerformanceTest(ParticlePool pool) {
        this.particlePool = pool;
        this.profiler = new PerformanceProfiler();
        this.activeParticles = 0;
    }

    /**
     * Adds a stress test load of particles to the pool.
     * @param count Number of particles to add
     */
    public void addStressLoad(int count) {
        for (int i = 0; i < count; i++) {
            float x = (float) Math.random() * 1920;
            float y = (float) Math.random() * 1080;
            float vx = ((float) Math.random() - 0.5f) * 200;
            float vy = ((float) Math.random() - 0.5f) * 200;
            
            // Add to particle pool with random duration
            particlePool.rentParticle(x, y, vx, vy, 2.0f + (float) Math.random(), null);
        }
        activeParticles = count;
        Logger.info("Particle stress test: Added " + count + " particles");
    }

    /**
     * Updates profiler with current frame performance.
     * @param delta Time elapsed this frame
     */
    public void updateFrame(float delta) {
        profiler.endFrame(delta);
    }

    /**
     * Get current FPS.
     */
    public float getFPS() {
        return profiler.getFPS();
    }

    /**
     * Get current frame time in milliseconds.
     */
    public float getFrameTime() {
        return profiler.getFrameTime();
    }

    /**
     * Get active particle count.
     */
    public int getActiveParticleCount() {
        return Math.min(activeParticles, particlePool.getActiveCount());
    }

    /**
     * Get performance report for current state.
     */
    public String getPerformanceReport() {
        return String.format(
            "Particles: %d | %s",
            getActiveParticleCount(),
            profiler.getPerformanceSummary()
        );
    }

    /**
     * Check if performance is acceptable (target: 60 FPS with 50+ particles).
     */
    public boolean isPerformanceAcceptable() {
        return getFPS() >= 55.0f && getActiveParticleCount() >= 50;
    }

    /**
     * Reset test metrics.
     */
    public void reset() {
        profiler.reset();
        activeParticles = 0;
    }
}
