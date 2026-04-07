package io.github.example.presentation.util;

import com.badlogic.gdx.Gdx;

/**
 * Логгер для LibGDX presentation слоя
 */
public class Logger {
    private static final String TAG = "LibGDX_Roguelike";
    
    public static void info(String message) {
        Gdx.app.log(TAG, message);
    }
    
    public static void error(String message) {
        Gdx.app.error(TAG, message);
    }
    
    public static void error(String message, Exception e) {
        Gdx.app.error(TAG, message, e);
    }
    
    public static void debug(String message) {
        Gdx.app.debug(TAG, message);
    }
}
