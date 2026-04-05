package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.domain.entities.Player;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import java.util.List;

/**
 * Renders HUD (Heads-Up Display) elements.
 * Shows player health, level info, status effects, and action log.
 * Layer 5 (topmost - always visible, not culled).
 */
public class UILayerRenderer extends AbstractLayerRenderer {
    private final Player player;
    private final StringBuilder logBuffer = new StringBuilder();
    private final List<String> actionLog;
    private static final int MAX_LOG_LINES = 5;

    public UILayerRenderer(Player player, List<String> actionLog) {
        super("UILayer");
        this.player = player;
        this.actionLog = actionLog;
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || player == null) {
            return;
        }

        // Note: In a real implementation, we would switch to UI viewport
        // For now, we render in world coordinates

        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        int padding = Constants.UI_PADDING;

        // Render health bar (top-left)
        renderHealthBar(batch, padding, screenHeight - padding - 30);

        // Render level/floor info (top-left, below health)
        renderLevelInfo(batch, padding, screenHeight - padding - 80);

        // Render status effects (top-right)
        renderStatusEffects(batch, screenWidth - padding - 200, screenHeight - padding - 30);

        // Render action log (bottom-left)
        renderActionLog(batch, padding, padding + 120);
    }

    /**
     * Renders player health bar.
     */
    private void renderHealthBar(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null) {
            return;
        }

        int currentHealth = Math.max(0, player.getStats().getCurrentHealth());
        int maxHealth = player.getStats().getMaxHealth();
        float healthPercent = (float) currentHealth / Math.max(maxHealth, 1);

        // Health bar background (black)
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        drawRectangle(batch, x, y, 150, 20);

        // Health bar fill (red to green gradient)
        float fillColor = healthPercent > 0.5f ? 1f - (1f - healthPercent) * 2f : 1f;
        batch.setColor(1f - healthPercent, healthPercent, 0, 1f);
        drawRectangle(batch, x, y, 150 * healthPercent, 20);

        batch.setColor(1, 1, 1, 1); // Reset
        Logger.debug("Health: " + currentHealth + "/" + maxHealth);
    }

    /**
     * Renders level and floor information.
     */
    private void renderLevelInfo(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null) {
            return;
        }

        batch.setColor(0.9f, 0.9f, 0.9f, 1f);
        // Placeholder for level info text rendering
        // In a real implementation, use a BitmapFont for text rendering
        Logger.debug("Player Level: " + player.getStats().getMaxHealth());
    }

    /**
     * Renders active status effects (buffs/debuffs) at top-right.
     */
    private void renderStatusEffects(SpriteBatch batch, float x, float y) {
        // Placeholder for status effect rendering
        // Would show icons for buffs/debuffs if implemented
        batch.setColor(1, 1, 1, 1);
        Logger.debug("Status effects rendered");
    }

    /**
     * Renders action log at bottom-left.
     */
    private void renderActionLog(SpriteBatch batch, float x, float y) {
        if (actionLog == null || actionLog.isEmpty()) {
            return;
        }

        batch.setColor(0.7f, 0.7f, 0.7f, 1f);
        float lineHeight = 15;
        int displayLines = Math.min(MAX_LOG_LINES, actionLog.size());

        for (int i = 0; i < displayLines; i++) {
            // Render recent log entries
            // In a real implementation, use BitmapFont for text
        }

        Logger.debug("Action log rendered (" + displayLines + " lines)");
    }

    /**
     * Helper to draw a colored rectangle.
     */
    private void drawRectangle(SpriteBatch batch, float x, float y, float width, float height) {
        // LibGDX doesn't have built-in rectangle drawing, would need shape renderer
        // For now, this is a placeholder
    }

    /**
     * Adds a message to the action log.
     */
    public void addLogMessage(String message) {
        if (message != null && !message.isEmpty()) {
            actionLog.add(0, message); // Add to front
            if (actionLog.size() > 100) {
                actionLog.remove(actionLog.size() - 1); // Keep buffer limited
            }
            Logger.debug("Log: " + message);
        }
    }

    @Override
    public void dispose() {
        actionLog.clear();
        logBuffer.setLength(0);
        super.dispose();
    }
}
