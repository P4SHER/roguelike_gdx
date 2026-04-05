package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Object pool for efficient particle management.
 * Reuses Particle objects instead of creating/destroying repeatedly.
 */
public class ParticlePool {
    private final List<Particle> pool;
    private final List<Particle> active;
    private final int maxParticles;

    public ParticlePool(int maxParticles) {
        this.maxParticles = maxParticles;
        this.pool = new ArrayList<>(maxParticles);
        this.active = new ArrayList<>(maxParticles / 2);

        // Pre-allocate particles
        for (int i = 0; i < maxParticles; i++) {
            pool.add(new Particle());
        }
        Logger.info("ParticlePool created with " + maxParticles + " particles");
    }

    /**
     * Get a particle from pool or create new if exhausted.
     */
    public Particle rentParticle(float x, float y, float vx, float vy, float duration, Sprite sprite) {
        Particle p;
        if (pool.isEmpty()) {
            // Pool exhausted, create new particle
            p = new Particle();
            Logger.warn("ParticlePool exhausted, creating new particle. Active: " + active.size());
        } else {
            p = pool.remove(pool.size() - 1);
        }
        p.init(x, y, vx, vy, duration, sprite);
        active.add(p);
        return p;
    }

    /**
     * Return particle to pool.
     */
    public void returnParticle(Particle p) {
        if (p == null) return;
        p.reset();
        active.remove(p);
        if (pool.size() < maxParticles) {
            pool.add(p);
        }
    }

    /**
     * Update all active particles and remove expired ones.
     */
    public void update(float delta) {
        for (int i = active.size() - 1; i >= 0; i--) {
            Particle p = active.get(i);
            p.update(delta);
            if (!p.active) {
                returnParticle(p);
            }
        }
    }

    /**
     * Render all active particles.
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (Particle p : active) {
            if (!p.active || p.sprite == null) continue;

            batch.setColor(1f, 1f, 1f, p.alpha);
            p.sprite.setPosition(p.x, p.y);
            p.sprite.setScale(p.scale);
            p.sprite.draw(batch);
        }
        batch.setColor(1f, 1f, 1f, 1f); // Reset
    }

    /**
     * Clear all particles.
     */
    public void clear() {
        active.clear();
        pool.clear();
    }

    /**
     * Get active particle count.
     */
    public int getActiveCount() {
        return active.size();
    }

    /**
     * Get pooled particle count.
     */
    public int getPooledCount() {
        return pool.size();
    }
}
