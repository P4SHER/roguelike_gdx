package io.github.example.presentation.libgdx;

/**
 * Конфигурация игры для LibGDX приложения.
 * Содержит все основные настройки экрана и производительности.
 */
public class GameConfig {
    // Размеры экрана
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;

    // Производительность
    public static final int TARGET_FPS = 60;
    public static final boolean VSYNC_ENABLED = true;

    // Размеры тайла
    public static final int TILE_SIZE = 32;

    private GameConfig() {
        throw new AssertionError("Cannot instantiate GameConfig");
    }
}
