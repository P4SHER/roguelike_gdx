package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Fade in transition - gradually fades from black to the screen.
 * Alpha progresses from 1.0 (opaque black) to 0.0 (transparent).
 */
public class FadeInTransition extends ScreenTransition {

    public FadeInTransition() {
        super(0.5f); // Default 0.5 second duration
    }

    public FadeInTransition(float duration) {
        super(duration);
    }

    @Override
    public void render(SpriteBatch batch, int width, int height) {
        float alpha = 1.0f - getAlpha(); // Reverse: start at 1.0, end at 0.0
        drawOverlay(batch, width, height, Color.BLACK, alpha);
    }
}
