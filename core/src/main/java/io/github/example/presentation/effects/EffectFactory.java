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
    // Effect timing constants (seconds)
    private static final float DURATION_DAMAGE = 1.75f;      // Damage numbers: 1.5-2.0s
    private static final float DURATION_HEAL = 1.75f;        // Heal particles: 1.5-2.0s
    private static final float DURATION_SPARK = 1.2f;        // Spark effects: slower fade
    private static final float DURATION_SPELL_FIRE = 1.0f;   // Spell effects: 1.0-1.5s
    private static final float DURATION_SPELL_ICE = 1.2f;
    private static final float DURATION_SPELL_LIGHTNING = 0.8f;
    private static final float DURATION_SPELL_HOLY = 1.5f;
    private static final float DURATION_SPELL_SHADOW = 1.3f;
    private static final float DURATION_SPELL_GENERIC = 1.1f;
    private static final float DURATION_XP = 1.5f;
    private static final float DURATION_LEVEL_UP = 1.2f;

    // Velocity constants
    private static final float VEL_DAMAGE_Y = 120f;          // Damage numbers upward
    private static final float VEL_DAMAGE_X_SPREAD = 50f;    // Horizontal spread
    private static final float VEL_HEAL_Y = 80f;             // Heal upward (slower)
    private static final float VEL_HEAL_X_SPREAD = 25f;      // Small horizontal spread
    private static final float VEL_SPARK_BASE = 200f;        // Sparks radiate
    private static final float VEL_SPELL_FIRE = 180f;
    private static final float VEL_SPELL_ICE = 150f;
    private static final float VEL_SPELL_LIGHTNING = 280f;   // Lightning fast
    private static final float VEL_SPELL_HOLY = 120f;
    private static final float VEL_SPELL_SHADOW = 160f;
    private static final float VEL_XP_Y = 100f;
    private static final float VEL_XP_X_SPREAD = 60f;
    private static final float VEL_LEVELUP_BASE = 220f;

    private static final Map<String, Sprite> effectSprites = new HashMap<>();

    static {
        // Initialize colored sprite cache with exact colors for visual effects
        effectSprites.put("red", createColoredSprite(1.0f, 0.0f, 0.0f, 1f));      // Damage (#FF0000)
        effectSprites.put("green", createColoredSprite(0.0f, 1.0f, 0.0f, 1f));    // Heal (#00FF00)
        effectSprites.put("yellow", createColoredSprite(1.0f, 1.0f, 0.0f, 1f));   // XP/Experience (#FFFF00)
        effectSprites.put("orange", createColoredSprite(1.0f, 0.647f, 0.0f, 1f)); // Special effects (#FFA500)
        effectSprites.put("cyan", createColoredSprite(0.0f, 1.0f, 1.0f, 1f));     // Status effects (#00FFFF)
        effectSprites.put("purple", createColoredSprite(0.6f, 0.2f, 1.0f, 1f));   // Magic/spell effects (#9933FF)
        effectSprites.put("white", createColoredSprite(1.0f, 1.0f, 1.0f, 1f));    // General effects (#FFFFFF)
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
     * Particles float upward and fade out over 1.75 seconds.
     * 2-4 particles create a clustered number appearance.
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
            // Small horizontal variation for visual interest
            float vx = (float)(Math.random() - 0.5f) * VEL_DAMAGE_X_SPREAD;
            float vy = VEL_DAMAGE_Y; // Strong upward velocity

            pool.rentParticle(x, y, vx, vy, DURATION_DAMAGE, damageSprite);
        }

        Logger.debug("Created damage number effect: " + damage + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a floating green heal number effect.
     * Particles float upward smoothly and fade out over 1.75 seconds.
     * 2-3 particles with minimal horizontal spread.
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
            // Minimal horizontal variation for clean appearance
            float vx = (float)(Math.random() - 0.5f) * VEL_HEAL_X_SPREAD;
            float vy = VEL_HEAL_Y; // Smooth upward velocity

            pool.rentParticle(x, y, vx, vy, DURATION_HEAL, healSprite);
        }

        Logger.debug("Created heal number effect: " + heal + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a weapon hit spark effect.
     * Multiple particles in a radial pattern with yellow/orange sparks.
     * Particles spread outward with smooth fading (1.2 seconds).
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

        // Alternate between yellow and orange for visual variety
        Sprite[] sparkSprites = {effectSprites.get("yellow"), effectSprites.get("orange")};

        for (int i = 0; i < particleCount; i++) {
            float angle = angleStep * i;
            float vx = (float)Math.cos(angle) * VEL_SPARK_BASE;
            float vy = (float)Math.sin(angle) * VEL_SPARK_BASE;

            Sprite sparkSprite = sparkSprites[i % 2];
            pool.rentParticle(x, y, vx, vy, DURATION_SPARK, sparkSprite);
        }

        Logger.debug("Created weapon hit effect at (" + x + ", " + y + ")");
    }

    /**
     * Create a spell cast effect.
     * Type-specific visual with particles spreading outward.
     * Duration 1.0-1.5 seconds with smooth fading via alpha.
     * Different colors and speeds based on spell type.
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
                speed = VEL_SPELL_FIRE;
                duration = DURATION_SPELL_FIRE;
                effectSpritesArray = new Sprite[]{effectSprites.get("red"), effectSprites.get("orange"), effectSprites.get("yellow")};
                break;

            case "ice":
            case "freeze":
                particleCount = 10;
                speed = VEL_SPELL_ICE;
                duration = DURATION_SPELL_ICE;
                effectSpritesArray = new Sprite[]{effectSprites.get("cyan"), effectSprites.get("white")};
                break;

            case "lightning":
            case "thunder":
                particleCount = 8;
                speed = VEL_SPELL_LIGHTNING;
                duration = DURATION_SPELL_LIGHTNING;
                effectSpritesArray = new Sprite[]{effectSprites.get("yellow"), effectSprites.get("white")};
                break;

            case "heal":
            case "holy":
                particleCount = 10;
                speed = VEL_SPELL_HOLY;
                duration = DURATION_SPELL_HOLY;
                effectSpritesArray = new Sprite[]{effectSprites.get("green"), effectSprites.get("white")};
                break;

            case "dark":
            case "shadow":
                particleCount = 12;
                speed = VEL_SPELL_SHADOW;
                duration = DURATION_SPELL_SHADOW;
                effectSpritesArray = new Sprite[]{effectSprites.get("purple"), effectSprites.get("white")};
                break;

            default:
                // Generic magic effect
                particleCount = 10;
                speed = VEL_SPELL_FIRE;
                duration = DURATION_SPELL_GENERIC;
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
     * Duration 1.5 seconds with smooth fading.
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
            float vx = (float)(Math.random() - 0.5f) * VEL_XP_X_SPREAD;
            float vy = VEL_XP_Y;

            pool.rentParticle(x, y, vx, vy, DURATION_XP, xpSprite);
        }

        Logger.debug("Created experience gain effect: " + amount + " at (" + x + ", " + y + ")");
    }

    /**
     * Create a level up burst effect.
     * Large burst of particles in multiple colors.
     * Duration 1.2 seconds with smooth alpha fade.
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

        Sprite[] sprites = {
            effectSprites.get("yellow"),
            effectSprites.get("white"),
            effectSprites.get("cyan"),
            effectSprites.get("purple")
        };

        for (int i = 0; i < particleCount; i++) {
            float angle = angleStep * i;
            float vx = (float)Math.cos(angle) * VEL_LEVELUP_BASE;
            float vy = (float)Math.sin(angle) * VEL_LEVELUP_BASE;

            Sprite sprite = sprites[i % sprites.length];
            pool.rentParticle(x, y, vx, vy, DURATION_LEVEL_UP, sprite);
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
