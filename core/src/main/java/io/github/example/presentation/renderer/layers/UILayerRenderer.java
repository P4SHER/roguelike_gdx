package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import io.github.example.domain.entities.Player;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.Logger;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Renders HUD (Heads-Up Display) elements.
 * Shows player health, level info, status effects, and action log.
 * Layer 5 (topmost - always visible, not culled).
 */
public class UILayerRenderer extends AbstractLayerRenderer {
    private final Player player;
    private final StringBuilder logBuffer = new StringBuilder();
    private final List<String> actionLog;
    private final Queue<ActionMessage> messageQueue = new LinkedList<>();
    private BitmapFont font;
    private BitmapFont smallFont;
    private Texture pixelTexture;
    private boolean fontsInitialized = false;
    
    private static final int MAX_LOG_LINES = 5;
    private static final int MAX_DISPLAY_MESSAGES = 5;
    private static final float MESSAGE_DISPLAY_TIME = 3.0f;
    private static final float MESSAGE_FADE_START = 2.5f;
    private static final int FONT_SIZE = 16;
    
    // HUD dimensions
    private static final int HEALTH_BAR_WIDTH = 100;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int STAT_LINE_HEIGHT = 25;

    /**
     * Inner class to represent an action message with lifetime tracking.
     */
    private static class ActionMessage {
        String text;
        float elapsedTime;
        Color color;

        ActionMessage(String text, Color color) {
            this.text = text;
            this.color = new Color(color);
            this.elapsedTime = 0f;
        }
    }

    public UILayerRenderer(Player player, List<String> actionLog) {
        super("UILayer");
        this.player = player;
        this.actionLog = actionLog;
        createPixelTexture();
    }

    /**
     * Creates a reusable 1x1 pixel texture for drawing rectangles.
     */
    private void createPixelTexture() {
        try {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 1, 1, 1);
            pixmap.fill();
            this.pixelTexture = new Texture(pixmap);
            pixmap.dispose();
            Logger.debug("Pixel texture created for UI rendering");
        } catch (Exception e) {
            Logger.debug("Failed to create pixel texture: " + e.getMessage());
        }
    }

    @Override
    public void init() {
        super.init();
        initializeFonts();
    }

    /**
     * Initializes BitmapFonts for HUD rendering.
     */
    private void initializeFonts() {
        if (fontsInitialized) {
            return;
        }
        try {
            // Create main font for stats display
            font = new BitmapFont();
            font.getData().setScale(1.2f);
            
            // Create smaller font for secondary info
            smallFont = new BitmapFont();
            smallFont.getData().setScale(0.8f);
            
            fontsInitialized = true;
            Logger.debug("UILayerRenderer fonts initialized successfully");
        } catch (Exception e) {
            Logger.debug("Failed to initialize fonts in UILayerRenderer: " + e.getMessage());
            font = new BitmapFont();
            smallFont = new BitmapFont();
            fontsInitialized = true;
        }
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || player == null) {
            return;
        }

        // Initialize fonts on first render
        if (!fontsInitialized) {
            initializeFonts();
        }

        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        int padding = Constants.UI_PADDING;

        batch.begin();

        // Render player stats (top-left)
        renderPlayerStats(batch, padding, screenHeight - padding);

        // Render health bar (below stats)
        renderHealthBar(batch, padding, screenHeight - padding - STAT_LINE_HEIGHT * 4);

        // Render status effects (top-right)
        renderStatusEffects(batch, screenWidth - padding - 200, screenHeight - padding - 30);

        // Update and render action log (bottom-left)
        updateMessageQueue(delta);
        renderActionLog(batch, padding, padding + 120);

        batch.end();
    }

    /**
     * Renders player statistics (HP, Level, XP, Coins).
     * Position: Top-left corner with padding
     */
    private void renderPlayerStats(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null || font == null) {
            return;
        }

        batch.setColor(ColorScheme.TEXT_PRIMARY);

        int currentHealth = Math.max(0, player.getStats().getCurrentHealth());
        int maxHealth = player.getStats().getMaxHealth();

        // HP: 45/100
        String hpText = String.format("HP: %d/%d", currentHealth, maxHealth);
        font.draw(batch, hpText, x, y);

        // LV: 5 (placeholder - would need level property in Player)
        String levelText = "LV: 1";
        font.draw(batch, levelText, x, y - STAT_LINE_HEIGHT);

        // XP: 850/1000 (placeholder - would need XP property in Player)
        String xpText = "XP: 0/1000";
        font.draw(batch, xpText, x, y - STAT_LINE_HEIGHT * 2);

        batch.setColor(ColorScheme.LOG_LOOT);
        // Coins: 250 (placeholder - would need coins property in Player)
        String coinsText = "Gold: 0";
        font.draw(batch, coinsText, x, y - STAT_LINE_HEIGHT * 3);

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders player health bar.
     * Position: Below HP text
     * Colors based on health percentage:
     *   - Green: 100%-50% health
     *   - Yellow: 50%-25% health
     *   - Red: 0%-25% health
     */
    private void renderHealthBar(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null || pixelTexture == null) {
            return;
        }

        int currentHealth = Math.max(0, player.getStats().getCurrentHealth());
        int maxHealth = player.getStats().getMaxHealth();
        float healthPercent = (float) currentHealth / Math.max(maxHealth, 1);

        // Draw health bar background
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(pixelTexture, x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Determine fill color based on health percentage
        Color fillColor = determineHealthBarColor(healthPercent);
        batch.setColor(fillColor);

        // Draw filled portion
        float filledWidth = HEALTH_BAR_WIDTH * healthPercent;
        batch.draw(pixelTexture, x, y, filledWidth, HEALTH_BAR_HEIGHT);

        // Draw border (gray outline)
        batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        drawRectangleBorder(batch, x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, 1);

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Determines health bar color based on health percentage.
     */
    private Color determineHealthBarColor(float healthPercent) {
        if (healthPercent > 0.5f) {
            return new Color(0f, 1f, 0f, 1f);
        } else if (healthPercent > 0.25f) {
            return new Color(1f, 1f, 0f, 1f);
        } else {
            return new Color(1f, 0f, 0f, 1f);
        }
    }

    /**
     * Draws a rectangle border (outline).
     */
    private void drawRectangleBorder(SpriteBatch batch, float x, float y, float width, float height, float thickness) {
        batch.draw(pixelTexture, x, y + height, width, thickness);
        batch.draw(pixelTexture, x, y, width, thickness);
        batch.draw(pixelTexture, x, y, thickness, height);
        batch.draw(pixelTexture, x + width - thickness, y, thickness, height);
    }

    /**
     * Renders active status effects (buffs/debuffs) at top-right.
     * Shows up to 3 active status effects as text.
     * Format: "[POISON] [FIRE] [SLOW]" with duration
     */
    private void renderStatusEffects(SpriteBatch batch, float x, float y) {
        if (smallFont == null) {
            return;
        }

        batch.setColor(ColorScheme.TEXT_SECONDARY);

        // Placeholder status effects for demonstration
        String[] statusEffects = new String[3];
        int effectCount = 0;

        // TODO: Replace with actual status effects from player entity
        // Example placeholder effects:
        // statusEffects[0] = "[POISON] 3s";
        // statusEffects[1] = "[FIRE] 5s";
        // statusEffects[2] = "[SLOW] 2s";

        // Render up to 3 status effects
        float currentY = y;
        for (int i = 0; i < Math.min(3, effectCount); i++) {
            if (statusEffects[i] != null && !statusEffects[i].isEmpty()) {
                smallFont.draw(batch, statusEffects[i], x, currentY);
                currentY -= 20;
            }
        }

        if (effectCount == 0) {
            batch.setColor(ColorScheme.TEXT_DISABLED);
            smallFont.draw(batch, "No Effects", x, currentY);
        }

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Updates message lifetimes and removes expired messages.
     */
    private void updateMessageQueue(float delta) {
        Queue<ActionMessage> toRemove = new LinkedList<>();
        for (ActionMessage msg : messageQueue) {
            msg.elapsedTime += delta;
            if (msg.elapsedTime >= MESSAGE_DISPLAY_TIME) {
                toRemove.add(msg);
            }
        }
        messageQueue.removeAll(toRemove);
    }

    /**
     * Renders action log at bottom-left with time-based fading.
     */
    private void renderActionLog(SpriteBatch batch, float x, float y) {
        if (smallFont == null || messageQueue.isEmpty()) {
            return;
        }

        float lineHeight = 20;
        int displayCount = 0;

        for (ActionMessage msg : messageQueue) {
            if (displayCount >= MAX_DISPLAY_MESSAGES) {
                break;
            }

            float alpha = calculateAlpha(msg.elapsedTime);
            Color msgColor = new Color(msg.color);
            msgColor.a = alpha;

            smallFont.setColor(msgColor);
            smallFont.draw(batch, msg.text, x, y - (displayCount * lineHeight));

            displayCount++;
        }

        batch.setColor(1, 1, 1, 1);
        Logger.debug("Action log rendered (" + displayCount + " messages)");
    }

    /**
     * Calculates alpha value based on message elapsed time for fading effect.
     */
    private float calculateAlpha(float elapsedTime) {
        if (elapsedTime < MESSAGE_FADE_START) {
            return 1.0f;
        }
        float fadeTime = MESSAGE_DISPLAY_TIME - MESSAGE_FADE_START;
        float fadedTime = elapsedTime - MESSAGE_FADE_START;
        return Math.max(0f, 1.0f - (fadedTime / fadeTime));
    }

    /**
     * Determines the color for a message based on its content.
     */
    private Color getMessageColor(String message) {
        if (message == null) {
            return Color.WHITE;
        }

        // Color coding based on message content
        if (message.contains("damage") || message.contains("takes") || message.toLowerCase().contains("hit")) {
            return ColorScheme.LOG_DAMAGE;
        } else if (message.contains("heal") || message.contains("potion") || message.toLowerCase().contains("+")) {
            return ColorScheme.LOG_HEAL;
        } else if (message.contains("experience") || message.contains("XP") || message.contains("level")) {
            return ColorScheme.LOG_EXPERIENCE;
        } else if (message.contains("loot") || message.contains("item") || message.contains("gold")) {
            return ColorScheme.LOG_LOOT;
        }

        return Color.WHITE;
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
            Color color = getMessageColor(message);
            ActionMessage actionMsg = new ActionMessage(message, color);
            messageQueue.add(actionMsg);
            
            // Keep queue size limited
            if (messageQueue.size() > MAX_DISPLAY_MESSAGES * 2) {
                messageQueue.poll(); // Remove oldest
            }
            
            // Also add to the legacy actionLog for backward compatibility
            actionLog.add(0, message);
            if (actionLog.size() > 100) {
                actionLog.remove(actionLog.size() - 1);
            }
            
            Logger.debug("Log: " + message);
        }
    }

    @Override
    public void dispose() {
        messageQueue.clear();
        actionLog.clear();
        logBuffer.setLength(0);
        
        if (font != null) {
            font.dispose();
        }
        if (smallFont != null) {
            smallFont.dispose();
        }
        if (pixelTexture != null) {
            pixelTexture.dispose();
        }
        
        super.dispose();
    }
}
