package io.github.example.presentation.util;

/**
 * Константы для presentation слоя.
 */
public class Constants {
    // Logging
    public static final boolean DEBUG_MODE = true;

    // Размеры экрана
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;

    // Размеры тайла
    public static final int TILE_SIZE = 32;

    // Размер видимой области камеры в тайлах
    public static final int VIEWPORT_WIDTH_TILES = SCREEN_WIDTH / TILE_SIZE;
    public static final int VIEWPORT_HEIGHT_TILES = SCREEN_HEIGHT / TILE_SIZE;

    // UI отступы
    public static final int UI_PADDING = 16;
    public static final int UI_MARGIN = 8;
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_WIDTH = 150;

    // HUD
    public static final int HUD_FONT_SIZE = 20;
    public static final int HUD_LOG_MAX_LINES = 100;
    public static final int HUD_LOG_DISPLAY_LINES = 5;

    // FPS и производительность
    public static final float TARGET_FPS = 60f;
    public static final float DELTA_TIME_MAX = 0.016f; // ~60 FPS

    // Анимация
    public static final float ANIMATION_FRAME_DURATION = 0.1f; // 100ms на кадр

    // Оптимизация
    public static final int MAX_VISIBLE_SPRITES = 500;
    public static final int CULLING_MARGIN = TILE_SIZE * 2;

    private Constants() {
        throw new AssertionError("Cannot instantiate Constants");
    }
}
