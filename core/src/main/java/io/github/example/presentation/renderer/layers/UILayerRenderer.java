package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private GlyphLayout glyphLayout;
    private Texture pixelTexture;
    private boolean fontsInitialized = false;
    
    private static final int MAX_LOG_LINES = 5;
    private static final int MAX_DISPLAY_MESSAGES = 5;
    private static final float MESSAGE_DISPLAY_TIME = 3.0f;
    private static final float MESSAGE_FADE_START = 2.5f;
    
    // Font scales optimized for 1920x1080 display
    private static final float MAIN_FONT_SCALE = 1.5f;    // Large text for stats
    private static final float SMALL_FONT_SCALE = 1.0f;   // Medium text for secondary info
    
    // HUD dimensions and layout
    private static final int HEALTH_BAR_WIDTH = 140;
    private static final int HEALTH_BAR_HEIGHT = 24;
    private static final int STAT_PADDING = 8;            // Spacing between stat lines
    private static final int STAT_LINE_HEIGHT = 32;       // Total height including padding
    private static final int STATS_BLOCK_WIDTH = 220;
    private static final int HEALTH_BAR_TOP_MARGIN = 12;  // Space above health bar

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
     * Initializes BitmapFonts for HUD rendering with scales optimized for 1920x1080.
     * Fonts are loaded once and reused throughout the game session.
     */
    private void initializeFonts() {
        if (fontsInitialized) {
            return;
        }
        try {
            // Create main font for stats display (large, readable)
            font = new BitmapFont();
            font.getData().setScale(MAIN_FONT_SCALE);
            font.setUseIntegerPositions(true);
            
            // Create smaller font for secondary info (action log, status effects)
            smallFont = new BitmapFont();
            smallFont.getData().setScale(SMALL_FONT_SCALE);
            smallFont.setUseIntegerPositions(true);
            
            // Initialize GlyphLayout for text measurement
            glyphLayout = new GlyphLayout();
            
            fontsInitialized = true;
        } catch (Exception e) {
            Logger.error("Failed to initialize fonts in UILayerRenderer", e);
            // Fallback: create default fonts
            font = new BitmapFont();
            smallFont = new BitmapFont();
            glyphLayout = new GlyphLayout();
            fontsInitialized = true;
        }
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || player == null) {
            return;
        }

        // Initialize fonts on first render (lazy initialization)
        if (!fontsInitialized) {
            initializeFonts();
        }

        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        int padding = Constants.UI_PADDING;

        batch.begin();

        // Render HUD elements with proper layout
        // Top-left: Player stats block (HP, Level, XP, Gold)
        renderPlayerStats(batch, padding, screenHeight - padding);

        // Below stats: Health bar with clear separation
        int statsBlockHeight = STAT_LINE_HEIGHT * 4 + HEALTH_BAR_TOP_MARGIN;
        renderHealthBar(batch, padding, screenHeight - padding - statsBlockHeight);

        // Top-right: Status effects
        renderStatusEffects(batch, screenWidth - padding - 220, screenHeight - padding);

        // Bottom-left: Action log with time-based fading
        updateMessageQueue(delta);
        renderActionLog(batch, padding, padding + 140);

        batch.end();
    }

    /**
     * Renders player statistics block (HP, Level, XP, Gold).
     * Position: Top-left corner with professional layout and spacing.
     * Layout: Each stat on its own line with consistent formatting.
     */
    private void renderPlayerStats(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null || font == null) {
            return;
        }

        batch.setColor(ColorScheme.TEXT_PRIMARY);
        float currentY = y;

        int currentHealth = Math.max(0, player.getStats().getCurrentHealth());
        int maxHealth = player.getStats().getMaxHealth();

        // Health: Large, prominent
        String hpText = String.format("HP: %d/%d", currentHealth, maxHealth);
        font.draw(batch, hpText, x, currentY);
        currentY -= STAT_LINE_HEIGHT;

        // Level (placeholder until level system is added)
        batch.setColor(ColorScheme.TEXT_SECONDARY);
        String levelText = "LV: 1";
        font.draw(batch, levelText, x, currentY);
        currentY -= STAT_LINE_HEIGHT;

        // XP (placeholder until XP system is added)
        String xpText = "XP: 0/1000";
        font.draw(batch, xpText, x, currentY);
        currentY -= STAT_LINE_HEIGHT;

        // Gold (loot-colored for emphasis)
        batch.setColor(ColorScheme.LOG_LOOT);
        String coinsText = "Gold: 0";
        font.draw(batch, coinsText, x, currentY);

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders player health bar with gradient color based on health percentage.
     * Position: Below stats block with clear separation
     * 
     * Color gradient:
     *   - Green (1.0, 1.0, 0.0): 100%-50% health
     *   - Yellow (1.0, 1.0, 0.0): 50%-25% health
     *   - Red (1.0, 0.0, 0.0): 25%-0% health
     */
    private void renderHealthBar(SpriteBatch batch, float x, float y) {
        if (player == null || player.getStats() == null || pixelTexture == null) {
            return;
        }

        int currentHealth = Math.max(0, player.getStats().getCurrentHealth());
        int maxHealth = player.getStats().getMaxHealth();
        float healthPercent = (float) currentHealth / Math.max(maxHealth, 1);

        // Draw background (dark gray)
        batch.setColor(0.15f, 0.15f, 0.15f, 1f);
        batch.draw(pixelTexture, x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Determine fill color based on health percentage gradient
        Color fillColor = determineHealthBarColor(healthPercent);
        batch.setColor(fillColor);

        // Draw filled portion proportional to health
        float filledWidth = HEALTH_BAR_WIDTH * healthPercent;
        batch.draw(pixelTexture, x, y, filledWidth, HEALTH_BAR_HEIGHT);

        // Draw border (light gray for contrast)
        batch.setColor(0.6f, 0.6f, 0.6f, 1f);
        drawRectangleBorder(batch, x, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT, 2);

        // Draw health value text centered on bar
        batch.setColor(ColorScheme.TEXT_PRIMARY);
        if (smallFont != null && glyphLayout != null) {
            String healthText = String.format("%d/%d", currentHealth, maxHealth);
            glyphLayout.setText(smallFont, healthText);
            float textWidth = glyphLayout.width;
            float textX = x + (HEALTH_BAR_WIDTH - textWidth) / 2f;
            float textY = y + (HEALTH_BAR_HEIGHT / 2f) + 4;
            smallFont.draw(batch, healthText, textX, textY);
        }

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Calculates smooth gradient color for health bar.
     * Uses linear interpolation between three key colors:
     * - Green (0, 1, 0) at 100% HP
     * - Yellow (1, 1, 0) at 50% HP
     * - Red (1, 0, 0) at 0% HP
     * 
     * @param healthPercent Current health as percentage [0, 1]
     * @return Interpolated color
     */
    private Color determineHealthBarColor(float healthPercent) {
        // Clamp to valid range to handle edge cases (overheal, negative HP)
        healthPercent = Math.max(0f, Math.min(1f, healthPercent));
        
        Color green = new Color(0f, 1f, 0f, 1f);
        Color yellow = new Color(1f, 1f, 0f, 1f);
        Color red = new Color(1f, 0f, 0f, 1f);
        
        if (healthPercent >= 0.5f) {
            // Interpolate from Green to Yellow (50%-100% health)
            float t = (healthPercent - 0.5f) / 0.5f;
            return interpolateColor(green, yellow, t);
        } else {
            // Interpolate from Yellow to Red (0%-50% health)
            float t = (0.5f - healthPercent) / 0.5f;
            return interpolateColor(yellow, red, t);
        }
    }

    /**
     * Linear color interpolation between two colors.
     * Useful for smooth transitions in UI elements.
     * 
     * @param from Start color
     * @param to End color
     * @param t Interpolation parameter [0, 1] where 0 = from, 1 = to
     * @return Interpolated color
     */
    private Color interpolateColor(Color from, Color to, float t) {
        // Clamp t to valid range
        t = Math.max(0f, Math.min(1f, t));
        
        return new Color(
            from.r + (to.r - from.r) * t,
            from.g + (to.g - from.g) * t,
            from.b + (to.b - from.b) * t,
            1f
        );
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
     * Shows up to 3 active status effects in a column format.
     * Layout: Each effect on its own line with duration.
     * Placeholder implementation - will be connected to status effect system.
     */
    private void renderStatusEffects(SpriteBatch batch, float x, float y) {
        if (smallFont == null) {
            return;
        }

        batch.setColor(ColorScheme.TEXT_SECONDARY);
        float currentY = y;

        // TODO: Replace with actual status effects from player entity when system is available
        String[] statusEffects = new String[3];
        int effectCount = 0;

        // Example placeholder effects (commented out for production):
        // statusEffects[0] = "[POISON] 3s";
        // statusEffects[1] = "[FIRE] 5s";
        // statusEffects[2] = "[SLOW] 2s";
        // effectCount = 2;

        // Render up to 3 status effects
        for (int i = 0; i < Math.min(3, effectCount); i++) {
            if (statusEffects[i] != null && !statusEffects[i].isEmpty()) {
                smallFont.draw(batch, statusEffects[i], x, currentY);
                currentY -= STAT_LINE_HEIGHT;
            }
        }

        // Show placeholder when no effects active
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
     * Shows most recent messages fading out as they age.
     * Layout: Up to 5 messages stacked vertically.
     */
    private void renderActionLog(SpriteBatch batch, float x, float y) {
        if (smallFont == null || messageQueue.isEmpty()) {
            return;
        }

        float lineHeight = 24;
        int displayCount = 0;

        for (ActionMessage msg : messageQueue) {
            if (displayCount >= MAX_DISPLAY_MESSAGES) {
                break;
            }

            // Calculate alpha for fade effect
            float alpha = calculateAlpha(msg.elapsedTime);
            Color msgColor = new Color(msg.color);
            msgColor.a = alpha;

            smallFont.setColor(msgColor);
            smallFont.draw(batch, msg.text, x, y - (displayCount * lineHeight));

            displayCount++;
        }

        batch.setColor(1, 1, 1, 1);
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
     * Adds a message to the action log with automatic color detection.
     * Message colors are determined by content keywords.
     * 
     * @param message The message to add to the log
     */
    public void addLogMessage(String message) {
        if (message != null && !message.isEmpty()) {
            Color color = getMessageColor(message);
            ActionMessage actionMsg = new ActionMessage(message, color);
            messageQueue.add(actionMsg);
            
            // Keep queue size limited to prevent memory buildup
            if (messageQueue.size() > MAX_DISPLAY_MESSAGES * 2) {
                messageQueue.poll();
            }
            
            // Also add to the legacy actionLog for backward compatibility
            actionLog.add(0, message);
            if (actionLog.size() > 100) {
                actionLog.remove(actionLog.size() - 1);
            }
        }
    }

    @Override
    public void dispose() {
        // Clear message and log data
        messageQueue.clear();
        actionLog.clear();
        logBuffer.setLength(0);
        
        // Dispose fonts (cached, loaded once per session)
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (smallFont != null) {
            smallFont.dispose();
            smallFont = null;
        }
        
        // Dispose textures
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
        
        fontsInitialized = false;
        super.dispose();
    }
}
