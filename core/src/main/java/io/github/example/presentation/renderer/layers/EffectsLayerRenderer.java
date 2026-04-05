package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Renders particle effects and temporary visual effects on the map.
 * Manages effect lifecycle with time-to-live (TTL) tracking.
 * Layer 4 (after fog, before UI).
 */
public class EffectsLayerRenderer extends AbstractLayerRenderer {
    private static class ActiveEffect {
        final float x;
        final float y;
        final Sprite sprite;
        final float duration;
        float ttl; // Time remaining

        ActiveEffect(float x, float y, Sprite sprite, float duration) {
            this.x = x;
            this.y = y;
            this.sprite = sprite;
            this.duration = duration;
            this.ttl = duration;
        }
    }

    private final List<ActiveEffect> activeEffects = new ArrayList<>();
    private int effectsRendered;

    public EffectsLayerRenderer() {
        super("EffectsLayer");
    }

    /**
     * Adds a new visual effect at the specified position.
     * @param x World X coordinate (in pixels)
     * @param y World Y coordinate (in pixels)
     * @param sprite The sprite to render
     * @param duration How long the effect should persist (in seconds)
     */
    public void addEffect(float x, float y, Sprite sprite, float duration) {
        if (sprite == null || duration <= 0) {
            return;
        }
        Sprite newSprite = new Sprite(sprite);
        newSprite.setPosition(x, y);
        newSprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
        activeEffects.add(new ActiveEffect(x, y, newSprite, duration));
        Logger.debug("Added effect at (" + x + ", " + y + ") with duration " + duration + "s");
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || activeEffects.isEmpty()) {
            return;
        }

        Rectangle visibleBounds = new Rectangle(
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight
        );

        effectsRendered = 0;

        // Update TTL and render visible effects
        Iterator<ActiveEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            ActiveEffect effect = iterator.next();
            effect.ttl -= delta;

            // Remove expired effects
            if (effect.ttl <= 0) {
                iterator.remove();
                continue;
            }

            // Cull invisible effects
            if (!cullingSystem.isVisible(effect.x, effect.y, Constants.TILE_SIZE, Constants.TILE_SIZE, visibleBounds)) {
                continue;
            }

            // Apply alpha fade-out effect
            float alpha = effect.ttl / effect.duration;
            batch.setColor(1, 1, 1, alpha);
            effect.sprite.draw(batch);
            batch.setColor(1, 1, 1, 1); // Reset color

            effectsRendered++;
        }

        if (effectsRendered > 0) {
            Logger.debug("Rendered " + effectsRendered + " effects");
        }
    }

    @Override
    public void dispose() {
        for (ActiveEffect effect : activeEffects) {
            if (effect.sprite != null && effect.sprite.getTexture() != null) {
                effect.sprite.getTexture().dispose();
            }
        }
        activeEffects.clear();
        super.dispose();
    }

    /**
     * Removes all active effects.
     */
    public void clearEffects() {
        activeEffects.clear();
    }

    /**
     * Returns the count of currently active effects.
     */
    public int getActiveEffectCount() {
        return activeEffects.size();
    }
}
