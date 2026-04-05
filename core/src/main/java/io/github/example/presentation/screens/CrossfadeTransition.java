package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Crossfade transition - fades from one screen to another through black.
 * Combines fade out and fade in in sequence.
 * Progress 0.0-0.5: Fade out (to black)
 * Progress 0.5-1.0: Fade in (from black)
 */
public class CrossfadeTransition extends ScreenTransition {

    public CrossfadeTransition() {
        super(1.0f); // Default 1.0 second duration (0.5s out + 0.5s in)
    }

    public CrossfadeTransition(float duration) {
        super(duration);
    }

    @Override
    public void render(SpriteBatch batch, int width, int height) {
        float progress = getProgress();
        float alpha;

        if (progress < 0.5f) {
            // First half: fade out
            alpha = progress * 2.0f; // 0.0 -> 1.0 over first half
        } else {
            // Second half: fade in
            alpha = (1.0f - progress) * 2.0f; // 1.0 -> 0.0 over second half
        }

        drawOverlay(batch, width, height, Color.BLACK, alpha);
    }
}
