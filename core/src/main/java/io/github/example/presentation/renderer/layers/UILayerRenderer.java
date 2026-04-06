package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.StatusEffect;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.StatusEffectColors;
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
    
    // Status effect HUD layout
    private static final int STATUS_EFFECT_ICON_SIZE = 24;
    private static final int STATUS_EFFECT_SPACING = 4;
    private static final int STATUS_EFFECTS_PER_ROW = 5;
    private static final int STATUS_EFFECTS_MAX = 10;

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

        // Render HUD elements with proper layout
        // Top-left: Player stats block (HP, Level, XP, Gold)
        renderPlayerStats(batch, padding, screenHeight - padding);

        // Below stats: Health bar with clear separation
        int statsBlockHeight = STAT_LINE_HEIGHT * 4 + HEALTH_BAR_TOP_MARGIN;
        renderHealthBar(batch, padding, screenHeight - padding - statsBlockHeight);

        // Top-right: Status effects
        renderStatusEffects(batch, screenWidth - padding - 280, screenHeight - padding);

        // Bottom-left: Action log with time-based fading
        updateMessageQueue(delta);
        renderActionLog(batch, padding, padding + 140);
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
     * Shows effect icons (24×24px) in a grid layout with duration timers.
     * Layout: Up to 5 effects per row, max 2 rows (10 effects total)
     * Each effect displays as a colored circle/square with duration in seconds.
     */
    private void renderStatusEffects(SpriteBatch batch, float x, float y) {
        if (player == null || pixelTexture == null || smallFont == null) {
            return;
        }

        List<StatusEffect> effects = player.getStatusEffects();
        if (effects == null || effects.isEmpty()) {
            // Show placeholder when no effects
            batch.setColor(ColorScheme.TEXT_DISABLED);
            smallFont.draw(batch, "No Effects", x, y);
            batch.setColor(1, 1, 1, 1);
            return;
        }

        // Limit to max displayable effects
        int effectCount = Math.min(effects.size(), STATUS_EFFECTS_MAX);
        float currentX = x;
        float currentY = y;
        int effectsInRow = 0;

        for (int i = 0; i < effectCount; i++) {
            StatusEffect effect = effects.get(i);
            if (effect == null) {
                continue;
            }

            // Wrap to next row after 5 effects
            if (effectsInRow >= STATUS_EFFECTS_PER_ROW) {
                currentX = x;
                currentY -= STATUS_EFFECT_ICON_SIZE + STATUS_EFFECT_SPACING + 20;
                effectsInRow = 0;
            }

            // Render effect icon and duration
            renderStatusEffectIcon(batch, currentX, currentY, effect);

            currentX += STATUS_EFFECT_ICON_SIZE + STATUS_EFFECT_SPACING;
            effectsInRow++;
        }

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders a single status effect icon with duration timer.
     * Icon: 24×24px colored square
     * Duration: Text below icon (e.g., "3.2s")
     * 
     * @param batch Sprite batch for rendering
     * @param x Icon x position (left edge)
     * @param y Icon y position (top edge)
     * @param effect The status effect to render
     */
    private void renderStatusEffectIcon(SpriteBatch batch, float x, float y, StatusEffect effect) {
        if (effect == null) {
            return;
        }

        // Get effect color
        Color effectColor = StatusEffectColors.getColorForEffect(effect.getType());
        batch.setColor(effectColor);

        // Draw icon background (24×24px square)
        batch.draw(pixelTexture, x, y - STATUS_EFFECT_ICON_SIZE, STATUS_EFFECT_ICON_SIZE, STATUS_EFFECT_ICON_SIZE);

        // Draw icon border
        batch.setColor(1, 1, 1, 0.6f);
        drawRectangleBorder(batch, x, y - STATUS_EFFECT_ICON_SIZE, STATUS_EFFECT_ICON_SIZE, STATUS_EFFECT_ICON_SIZE, 1);

        // Calculate duration text and color
        float remaining = effect.getRemainingDuration();
        String durationText = String.format("%.1f", remaining);
        Color durationColor = determineDurationColor(remaining);

        // Draw duration text below icon
        if (smallFont != null && glyphLayout != null) {
            glyphLayout.setText(smallFont, durationText);
            float textWidth = glyphLayout.width;
            float textX = x + (STATUS_EFFECT_ICON_SIZE - textWidth) / 2f;
            float textY = y - STATUS_EFFECT_ICON_SIZE - 8;

            batch.setColor(durationColor);
            smallFont.draw(batch, durationText, textX, textY);
        }

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Determines the color for duration text based on remaining time.
     * - Green: >5s remaining
     * - Yellow: <5s remaining (warning)
     * - Red: <2s remaining (critical)
     * 
     * @param remaining Remaining duration in seconds
     * @return Color for the duration text
     */
    private Color determineDurationColor(float remaining) {
        if (remaining < 2f) {
            return new Color(1f, 0.2f, 0.2f, 1f);  // Red (critical)
        } else if (remaining < 5f) {
            return new Color(1f, 1f, 0.2f, 1f);    // Yellow (warning)
        } else {
            return new Color(0.8f, 0.8f, 0.8f, 1f); // Gray (normal)
        }
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
