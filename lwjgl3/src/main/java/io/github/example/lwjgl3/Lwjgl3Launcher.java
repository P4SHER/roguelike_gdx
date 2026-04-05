package io.github.example.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.example.presentation.libgdx.GameConfig;
import io.github.example.presentation.libgdx.LibGdxGameApplicationListener;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new LibGdxGameApplicationListener(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Roguelike");

        // Window size (1920x1080 HD)
        configuration.setWindowedMode(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // V-Sync
        if (GameConfig.VSYNC_ENABLED) {
            configuration.useVsync(true);
            configuration.setForegroundFPS(GameConfig.TARGET_FPS);
        } else {
            configuration.useVsync(false);
            configuration.setForegroundFPS(GameConfig.TARGET_FPS);
        }

        // Window icons
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        return configuration;
    }
}
