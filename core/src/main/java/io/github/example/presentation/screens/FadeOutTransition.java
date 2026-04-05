package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Fade out transition - gradually fades the screen to black.
 * Alpha progresses from 0.0 (transparent) to 1.0 (opaque black).
 */
public class FadeOutTransition extends ScreenTransition {

    public FadeOutTransition() {
        super(0.5f); // Default 0.5 second duration
    }

    public FadeOutTransition(float duration) {
        super(duration);
    }

    @Override
    public void render(SpriteBatch batch, int width, int height) {
        float alpha = getAlpha();
        drawOverlay(batch, width, height, Color.BLACK, alpha);
    }
}
