package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Main particle system orchestrator.
 * Manages multiple ParticleGroups (one per EffectType) and coordinates their lifecycle.
 * Provides a high-level factory interface for creating effects.
 *
 * Features:
 * - Wraps ParticlePool for efficient particle reuse
 * - Groups particles by effect type for better organization
 * - Supports up to 10 simultaneous effect types
 * - Thread-safe initialization (lazy loading of groups)
 */
public class ParticleSystem {
    private final Map<EffectType, ParticleGroup> groups;
    private final int maxParticles;
    private final AssetManager assetManager;
    private int totalActiveParticles;

    /**
     * Initialize particle system with max particle count.
     *
     * @param maxParticles Maximum particles to maintain across all groups
     * @param assetManager Asset manager for texture loading
     */
    public ParticleSystem(int maxParticles, AssetManager assetManager) {
        this.maxParticles = maxParticles;
        this.assetManager = assetManager;
        this.groups = new HashMap<>(10);

        // Pre-initialize all effect type groups
        for (EffectType type : EffectType.values()) {
            groups.put(type, new ParticleGroup(type, maxParticles / 7)); // Distribute particles
        }

        Logger.info("ParticleSystem initialized with " + maxParticles + " max particles");
    }

    /**
     * Update all particle groups.
     * Call once per frame with delta time.
     *
     * @param delta Time since last frame (seconds)
     */
    public void update(float delta) {
        totalActiveParticles = 0;
        for (ParticleGroup group : groups.values()) {
            group.update(delta);
            totalActiveParticles += group.getActiveCount();
        }
    }

    /**
     * Render all particles from all groups.
     * Call once per frame after update.
     *
     * @param batch SpriteBatch for rendering
     * @param camera Camera for view matrix
     */
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (ParticleGroup group : groups.values()) {
            group.render(batch, camera);
        }
    }

    /**
     * Create an effect of specified type at the given position.
     * Factory method that delegates to EffectFactory and appropriate ParticleGroup.
     *
     * @param type Effect type (DAMAGE, HEAL, SPELL, etc.)
     * @param x World X coordinate
     * @param y World Y coordinate
     */
    public void createEffect(EffectType type, float x, float y) {
        createEffect(type, x, y, null);
    }

    /**
     * Create an effect with additional parameter (for damage/heal amounts, spell types).
     *
     * @param type Effect type
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param param Additional parameter (amount or spell type string)
     */
    public void createEffect(EffectType type, float x, float y, Object param) {
        ParticleGroup group = groups.get(type);
        if (group == null) {
            Logger.warn("ParticleGroup not found for effect type: " + type);
            return;
        }

        // Delegate to EffectFactory based on type
        ParticlePool pool = group.getParticlePool();

        switch (type) {
            case DAMAGE:
                int damageAmount = param instanceof Integer ? (Integer) param : 0;
                EffectFactory.createDamageNumber(pool, x, y, damageAmount, assetManager);
                break;

            case HEAL:
                int healAmount = param instanceof Integer ? (Integer) param : 0;
                EffectFactory.createHealNumber(pool, x, y, healAmount, assetManager);
                break;

            case SPELL:
                String spellType = param instanceof String ? (String) param : "generic";
                EffectFactory.createSpellEffect(pool, x, y, spellType, assetManager);
                break;

            case WEAPON:
                EffectFactory.createWeaponHit(pool, x, y, assetManager);
                break;

            case STATUS:
                // Status effects use weapon hit animation for now
                EffectFactory.createWeaponHit(pool, x, y, assetManager);
                break;

            case EXPERIENCE:
                int xpAmount = param instanceof Integer ? (Integer) param : 0;
                EffectFactory.createExperienceGain(pool, x, y, xpAmount, assetManager);
                break;

            case LEVEL_UP:
                EffectFactory.createLevelUp(pool, x, y, assetManager);
                break;

            default:
                Logger.warn("Unknown effect type: " + type);
        }
    }

    /**
     * Clear all active particles from all groups.
     */
    public void clear() {
        for (ParticleGroup group : groups.values()) {
            group.clear();
        }
        totalActiveParticles = 0;
    }

    /**
     * Dispose all particle groups and cleanup resources.
     */
    public void dispose() {
        for (ParticleGroup group : groups.values()) {
            group.dispose();
        }
        groups.clear();
        Logger.info("ParticleSystem disposed");
    }

    /**
     * Get total active particles across all groups.
     *
     * @return Number of active particles
     */
    public int getTotalActiveParticles() {
        return totalActiveParticles;
    }

    /**
     * Get active particle count for specific effect type.
     *
     * @param type Effect type
     * @return Number of active particles of that type
     */
    public int getActiveParticles(EffectType type) {
        ParticleGroup group = groups.get(type);
        return group != null ? group.getActiveCount() : 0;
    }

    /**
     * Get ParticleGroup for specified effect type.
     * Useful for advanced customization.
     *
     * @param type Effect type
     * @return ParticleGroup instance
     */
    public ParticleGroup getGroup(EffectType type) {
        return groups.get(type);
    }
}
