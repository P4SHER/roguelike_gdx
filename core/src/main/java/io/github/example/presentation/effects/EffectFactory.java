package io.github.example.presentation.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating various visual effects using the particle system.
 * Provides static methods for damage numbers, heal indicators, weapon hits, and spells.
 */
public class EffectFactory {
    private static final Map<String, Sprite> effectSprites = new HashMap<>();

    static {
        // Initialize colored sprite cache
        effectSprites.put("red", createColoredSprite(1f, 0f, 0f, 1f));      // Damage
        effectSprites.put("green", createColoredSprite(0f, 1f, 0f, 1f));    // Heal
        effectSprites.put("yellow", createColoredSprite(1f, 1f, 0f, 1f));   // Sparks
        effectSprites.put("orange", createColoredSprite(1f, 0.6f, 0f, 1f)); // Fire/sparks
        effectSprites.put("cyan", createColoredSprite(0f, 1f, 1f, 1f));     // Ice/cold
        effectSprites.put("purple", createColoredSprite(1f, 0f, 1f, 1f));   // Magic
        effectSprites.put("white", createColoredSprite(1f, 1f, 1f, 1f));    // Generic
    }

    /**
     * Create a colored 1x1 pixel sprite for particle effects.
     */
    private static Sprite createColoredSprite(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new Sprite(texture);
    }

    /**
     * Create a floating red damage number effect.
     * Particles float upward and fade out over 1.5 seconds.
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param damage damage amount to display
     * @param assets asset manager for textures
     */
    public static void createDamageNumber(ParticlePool pool, float x, float y, int damage, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createDamageNumber");
            return;
        }

        // Create 2-4 particles in a small cluster for the number effect
        int particleCount = 2 + (int)(Math.random() * 3); // 2-4 particles
        Sprite damageSprite = effectSprites.get("red");

        for (int i = 0; i < particleCount; i++) {
            // Small horizontal variation
            float vx = (float)(Math.random() - 0.5f) * 30f;
            float vy = 100f; // Upward velocity

            pool.rentParticle(x, y, vx, vy, 1.5f, damageSprite);
        }

        Logger.debug("Created damage number effect: " + damage + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a floating green heal number effect.
     * Particles float upward slower and fade out over 1.5 seconds.
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param heal heal amount to display
     * @param assets asset manager for textures
     */
    public static void createHealNumber(ParticlePool pool, float x, float y, int heal, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createHealNumber");
            return;
        }

        // Create 2-3 particles for heal effect (slower and greener)
        int particleCount = 2 + (int)(Math.random() * 2); // 2-3 particles
        Sprite healSprite = effectSprites.get("green");

        for (int i = 0; i < particleCount; i++) {
            // Minimal horizontal variation
            float vx = (float)(Math.random() - 0.5f) * 15f;
            float vy = 50f; // Slower upward velocity

            pool.rentParticle(x, y, vx, vy, 1.5f, healSprite);
        }

        Logger.debug("Created heal number effect: " + heal + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a weapon hit spark effect.
     * Multiple particles in a radial pattern with yellow/orange sparks.
     * Particles spread outward and fade quickly (0.6 seconds).
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param assets asset manager for textures
     */
    public static void createWeaponHit(ParticlePool pool, float x, float y, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createWeaponHit");
            return;
        }

        // Create 8 particles in radial pattern
        int particleCount = 8;
        float angleStep = (float)(2 * Math.PI / particleCount);
        float speed = 200f;
        float duration = 0.6f;

        // Alternate between yellow and orange for visual variety
        Sprite[] sparkSprites = {effectSprites.get("yellow"), effectSprites.get("orange")};

        for (int i = 0; i < particleCount; i++) {
            float angle = angleStep * i;
            float vx = (float)Math.cos(angle) * speed;
            float vy = (float)Math.sin(angle) * speed;

            Sprite sparkSprite = sparkSprites[i % 2];
            pool.rentParticle(x, y, vx, vy, duration, sparkSprite);
        }

        Logger.debug("Created weapon hit effect at (" + x + ", " + y + ")");
    }

    /**
     * Create a spell cast effect.
     * Type-specific visual with particles spreading outward.
     * Different colors and patterns based on spell type.
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param spellType type of spell (e.g., "fireball", "ice", "lightning")
     * @param assets asset manager for textures
     */
    public static void createSpellEffect(ParticlePool pool, float x, float y, String spellType, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createSpellEffect");
            return;
        }

        int particleCount;
        float speed;
        float duration;
        Sprite[] effectSpritesArray;

        // Type-specific configurations
        switch (spellType.toLowerCase()) {
            case "fireball":
            case "fire":
                particleCount = 12;
                speed = 150f;
                duration = 1.0f;
                effectSpritesArray = new Sprite[]{effectSprites.get("red"), effectSprites.get("orange"), effectSprites.get("yellow")};
                break;

            case "ice":
            case "freeze":
                particleCount = 10;
                speed = 120f;
                duration = 1.2f;
                effectSpritesArray = new Sprite[]{effectSprites.get("cyan"), effectSprites.get("white")};
                break;

            case "lightning":
            case "thunder":
                particleCount = 8;
                speed = 250f;
                duration = 0.5f;
                effectSpritesArray = new Sprite[]{effectSprites.get("yellow"), effectSprites.get("white")};
                break;

            case "heal":
            case "holy":
                particleCount = 10;
                speed = 100f;
                duration = 1.5f;
                effectSpritesArray = new Sprite[]{effectSprites.get("green"), effectSprites.get("white")};
                break;

            case "dark":
            case "shadow":
                particleCount = 12;
                speed = 130f;
                duration = 1.3f;
                effectSpritesArray = new Sprite[]{effectSprites.get("purple"), effectSprites.get("white")};
                break;

            default:
                // Generic magic effect
                particleCount = 10;
                speed = 140f;
                duration = 1.0f;
                effectSpritesArray = new Sprite[]{effectSprites.get("purple"), effectSprites.get("cyan"), effectSprites.get("white")};
        }

        // Create particles in radial pattern
        float angleStep = (float)(2 * Math.PI / particleCount);

        for (int i = 0; i < particleCount; i++) {
            float angle = angleStep * i + (float)(Math.random() * 0.3); // Add slight variation
            float vx = (float)Math.cos(angle) * speed;
            float vy = (float)Math.sin(angle) * speed;

            Sprite sprite = effectSpritesArray[i % effectSpritesArray.length];
            pool.rentParticle(x, y, vx, vy, duration, sprite);
        }

        Logger.debug("Created spell effect (" + spellType + ") at (" + x + ", " + y + ")");
    }

    /**
     * Create an experience gain indicator.
     * Small upward-moving particles in gold color.
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param amount XP amount gained
     * @param assets asset manager for textures
     */
    public static void createExperienceGain(ParticlePool pool, float x, float y, int amount, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createExperienceGain");
            return;
        }

        // Create 3 small particles for XP effect
        int particleCount = 3;
        Sprite xpSprite = effectSprites.get("yellow");

        for (int i = 0; i < particleCount; i++) {
            float vx = (float)(Math.random() - 0.5f) * 40f;
            float vy = 80f;

            pool.rentParticle(x, y, vx, vy, 1.2f, xpSprite);
        }

        Logger.debug("Created experience gain effect: " + amount + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a level up burst effect.
     * Large burst of particles in multiple colors.
     *
     * @param pool the particle pool to rent particles from
     * @param x world x position
     * @param y world y position
     * @param assets asset manager for textures
     */
    public static void createLevelUp(ParticlePool pool, float x, float y, AssetManager assets) {
        if (pool == null) {
            Logger.warn("ParticlePool is null in createLevelUp");
            return;
        }

        // Create burst effect with multiple colors
        int particleCount = 16;
        float angleStep = (float)(2 * Math.PI / particleCount);
        float speed = 200f;
        float duration = 1.0f;

        Sprite[] sprites = {
            effectSprites.get("yellow"),
            effectSprites.get("white"),
            effectSprites.get("cyan"),
            effectSprites.get("purple")
        };

        for (int i = 0; i < particleCount; i++) {
            float angle = angleStep * i;
            float vx = (float)Math.cos(angle) * speed;
            float vy = (float)Math.sin(angle) * speed;

            Sprite sprite = sprites[i % sprites.length];
            pool.rentParticle(x, y, vx, vy, duration, sprite);
        }

        Logger.debug("Created level up effect at (" + x + ", " + y + ")");
    }

    /**
     * Cleanup effect sprites when application closes.
     * Should be called during dispose phase.
     */
    public static void dispose() {
        for (Sprite sprite : effectSprites.values()) {
            if (sprite != null && sprite.getTexture() != null) {
                sprite.getTexture().dispose();
            }
        }
        effectSprites.clear();
        Logger.info("EffectFactory sprites disposed");
    }
}
