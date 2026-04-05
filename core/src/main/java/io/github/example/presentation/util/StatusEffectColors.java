package io.github.example.presentation.util;

import com.badlogic.gdx.graphics.Color;
import io.github.example.domain.entities.StatusEffectType;

/**
 * Defines colors for status effect icons in the HUD.
 * Each effect type has a unique color for visual identification.
 */
public class StatusEffectColors {
    // Status effect colors (RGB)
    public static final Color POISON_COLOR = new Color(0.2f, 0.8f, 0.2f, 1f);          // Green
    public static final Color FIRE_COLOR = new Color(1f, 0.4f, 0f, 1f);                // Red-Orange
    public static final Color COLD_COLOR = new Color(0.2f, 0.8f, 1f, 1f);             // Cyan
    public static final Color BLEEDING_COLOR = new Color(0.6f, 0f, 0f, 1f);           // Dark Red
    public static final Color CURSE_COLOR = new Color(0.7f, 0.2f, 0.9f, 1f);          // Purple
    public static final Color SHIELD_COLOR = new Color(0.4f, 0.8f, 1f, 1f);           // Light Blue
    public static final Color REGENERATION_COLOR = new Color(0.4f, 1f, 0.4f, 1f);     // Light Green
    public static final Color SLOW_COLOR = new Color(0.5f, 0.5f, 0.8f, 1f);           // Slate Blue
    public static final Color HASTE_COLOR = new Color(1f, 0.7f, 0.2f, 1f);            // Gold
    public static final Color STUN_COLOR = new Color(1f, 1f, 0.2f, 1f);               // Yellow

    private StatusEffectColors() {
        throw new AssertionError("Cannot instantiate StatusEffectColors");
    }

    /**
     * Returns the color associated with a given status effect type.
     * 
     * @param type The status effect type
     * @return The color for rendering the effect icon
     */
    public static Color getColorForEffect(StatusEffectType type) {
        if (type == null) {
            return Color.WHITE;
        }

        switch (type) {
            case POISON:
                return POISON_COLOR;
            case FIRE:
                return FIRE_COLOR;
            case COLD:
                return COLD_COLOR;
            case BLEEDING:
                return BLEEDING_COLOR;
            case CURSE:
                return CURSE_COLOR;
            case SHIELD:
                return SHIELD_COLOR;
            case REGENERATION:
                return REGENERATION_COLOR;
            case SLOW:
                return SLOW_COLOR;
            case HASTE:
                return HASTE_COLOR;
            case STUN:
                return STUN_COLOR;
            default:
                return Color.WHITE;
        }
    }
}
