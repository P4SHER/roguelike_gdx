package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.presentation.effects.ParticlePool;
import io.github.example.presentation.effects.EffectType;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;

/**
 * Renders particle effects and temporary visual effects on the map.
 * Uses ParticlePool for efficient object reuse and lifecycle management.
 * Layer 4 (after fog, before UI).
 */
public class EffectsLayerRenderer extends AbstractLayerRenderer {
    private final ParticlePool particlePool;
    private int effectsRendered;

    public EffectsLayerRenderer() {
        super("EffectsLayer");
        this.particlePool = new ParticlePool(300);
    }

    /**
     * Adds a new visual effect at the specified position.
     * @param x World X coordinate (in pixels)
     * @param y World Y coordinate (in pixels)
     * @param sprite The sprite to render
     * @param duration How long the effect should persist (in seconds)
     */
    public void addEffect(float x, float y, Sprite sprite, float duration) {
        addEffect(x, y, sprite, duration, 0, 0);
    }

    /**
     * Adds a new visual effect with velocity.
     * @param x World X coordinate (in pixels)
     * @param y World Y coordinate (in pixels)
     * @param sprite The sprite to render
     * @param duration How long the effect should persist (in seconds)
     * @param vx X velocity
     * @param vy Y velocity
     */
    public void addEffect(float x, float y, Sprite sprite, float duration, float vx, float vy) {
        if (sprite == null || duration <= 0) {
            return;
        }
        Sprite newSprite = new Sprite(sprite);
        newSprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
        particlePool.rentParticle(x, y, vx, vy, duration, newSprite);
        Logger.debug("Added effect at (" + x + ", " + y + ") with duration " + duration + "s");
    }

    /**
     * Adds a new visual effect based on EffectType.
     * @param x World X coordinate (in pixels)
     * @param y World Y coordinate (in pixels)
     * @param type The type of effect
     * @param sprite The sprite to render
     */
    public void addEffect(float x, float y, EffectType type, Sprite sprite) {
        if (sprite == null || type == null) {
            return;
        }
        
        float vx = 0;
        float vy = 0;
        float duration = 0.5f;
        
        switch (type) {
            case DAMAGE:
                duration = 0.8f;
                vy = 50f; // Float upward
                break;
            case HEAL:
                duration = 0.8f;
                vy = 50f; // Float upward
                break;
            case SPELL:
                duration = 1.0f;
                break;
            case WEAPON:
                duration = 0.6f;
                break;
            case STATUS:
                duration = 1.2f;
                break;
            case EXPERIENCE:
                duration = 1.0f;
                vy = 30f; // Float upward
                break;
            case LEVEL_UP:
                duration = 1.5f;
                vy = 40f; // Float upward
                break;
        }
        
        addEffect(x, y, sprite, duration, vx, vy);
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible) {
            return;
        }

        particlePool.update(delta);
        particlePool.render(batch, camera);

        effectsRendered = particlePool.getActiveCount();
        if (effectsRendered > 0) {
            Logger.debug("Rendered " + effectsRendered + " effects");
        }
    }

    @Override
    public void dispose() {
        particlePool.clear();
        super.dispose();
    }

    /**
     * Removes all active effects.
     */
    public void clearEffects() {
        particlePool.clear();
    }

    /**
     * Returns the count of currently active effects.
     */
    public int getActiveEffectCount() {
        return particlePool.getActiveCount();
    }
}
