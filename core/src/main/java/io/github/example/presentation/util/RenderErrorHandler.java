package io.github.example.presentation.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.presentation.assets.AssetManager;

/**
 * Handles render errors and provides fallback textures/resources.
 * Ensures graceful degradation when assets are missing or rendering fails.
 */
public class RenderErrorHandler {
    private static Texture fallbackTexture;
    private static final Object lock = new Object();

    /**
     * Get a fallback texture when asset is missing.
     * Returns a 32x32 magenta checkerboard pattern to indicate missing asset.
     */
    public static Texture getFallbackTexture() {
        if (fallbackTexture == null) {
            synchronized (lock) {
                if (fallbackTexture == null) {
                    fallbackTexture = createFallbackTexture();
                }
            }
        }
        return fallbackTexture;
    }

    /**
     * Create a checkerboard fallback texture (32x32, magenta/black).
     */
    private static Texture createFallbackTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        
        // Create magenta/black checkerboard pattern
        for (int y = 0; y < 32; y++) {
            for (int x = 0; x < 32; x++) {
                if ((x / 4 + y / 4) % 2 == 0) {
                    pixmap.setColor(1.0f, 0.0f, 1.0f, 1.0f); // Magenta
                } else {
                    pixmap.setColor(0.0f, 0.0f, 0.0f, 1.0f); // Black
                }
                pixmap.drawPixel(x, y);
            }
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        Logger.warn("Created fallback texture for missing assets");
        return texture;
    }

    /**
     * Handle a missing asset error gracefully.
     * @param assetPath The path of the missing asset
     */
    public static void handleMissingAsset(String assetPath) {
        Logger.error("Missing asset: " + assetPath + " - Using fallback texture");
    }

    /**
     * Handle a render error gracefully.
     * @param layerName The name of the layer that failed
     * @param exception The exception that occurred
     */
    public static void handleRenderError(String layerName, Exception exception) {
        Logger.error("Render error in layer " + layerName + ": " + exception.getMessage());
    }

    /**
     * Handle an asset loading error.
     * @param assetPath The asset that failed to load
     * @param exception The exception that occurred
     */
    public static void handleAssetLoadError(String assetPath, Exception exception) {
        Logger.error("Failed to load asset '" + assetPath + "': " + exception.getMessage());
    }

    /**
     * Validate texture before use.
     * @param texture The texture to validate
     * @param assetPath The asset path for logging
     * @return The texture if valid, or fallback texture if invalid/null
     */
    public static Texture validateTexture(Texture texture, String assetPath) {
        if (texture == null) {
            Logger.warn("Texture is null for asset: " + assetPath);
            return getFallbackTexture();
        }
        
        if (texture.getWidth() <= 0 || texture.getHeight() <= 0) {
            Logger.warn("Texture has invalid dimensions for asset: " + assetPath);
            return getFallbackTexture();
        }
        
        return texture;
    }

    /**
     * Dispose fallback texture when shutting down.
     */
    public static void dispose() {
        synchronized (lock) {
            if (fallbackTexture != null) {
                fallbackTexture.dispose();
                fallbackTexture = null;
                Logger.debug("Fallback texture disposed");
            }
        }
    }

    /**
     * Safe texture dispose that catches exceptions.
     */
    public static void safeDispose(Texture texture) {
        if (texture != null) {
            try {
                texture.dispose();
            } catch (Exception e) {
                Logger.error("Error disposing texture: " + e.getMessage());
            }
        }
    }
}
