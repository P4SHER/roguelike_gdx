package io.github.example.presentation.util;

import com.badlogic.gdx.graphics.Color;

/**
 * Цветовая схема для игры.
 */
public class ColorScheme {
    // Основные цвета UI
    public static final Color BUTTON_IDLE = new Color(0.2f, 0.2f, 0.2f, 1f);
    public static final Color BUTTON_HOVER = new Color(0.3f, 0.3f, 0.3f, 1f);
    public static final Color BUTTON_PRESSED = new Color(0.1f, 0.1f, 0.1f, 1f);

    // Текст
    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = new Color(0.8f, 0.8f, 0.8f, 1f);
    public static final Color TEXT_DISABLED = new Color(0.5f, 0.5f, 0.5f, 1f);

    // Фоны
    public static final Color BACKGROUND_DARK = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    public static final Color BACKGROUND_MENU = new Color(0.05f, 0.05f, 0.05f, 1f);

    // Здоровье и состояние
    public static final Color HEALTH_FULL = Color.GREEN;
    public static final Color HEALTH_LOW = Color.RED;
    public static final Color HEALTH_CRITICAL = new Color(1f, 0f, 0f, 1f);

    // Лог действий
    public static final Color LOG_DAMAGE = Color.RED;
    public static final Color LOG_HEAL = Color.GREEN;
    public static final Color LOG_EXPERIENCE = Color.YELLOW;
    public static final Color LOG_LOOT = new Color(1f, 0.84f, 0f, 1f); // Gold

    // Туман войны
    public static final Color FOG_EXPLORED = new Color(0.3f, 0.3f, 0.3f, 0.5f);
    public static final Color FOG_UNEXPLORED = new Color(0f, 0f, 0f, 1f);

    private ColorScheme() {
        throw new AssertionError("Cannot instantiate ColorScheme");
    }
}
