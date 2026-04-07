package io.github.example.presentation.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

/**
 * Управляет загрузкой текстур с правильными фильтрами для пиксель-арта
 */
public class TextureManager {
    
    /**
     * Загружает текстуру с фильтром Nearest (для пиксель-арта)
     * 
     * @param path путь к файлу текстуры
     * @return загруженная текстура с фильтром Nearest
     */
    public static Texture loadPixelArtTexture(String path) {
        Texture texture = new Texture(path);
        // Используем Nearest фильтр для чёткого пиксель-арта
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        Logger.info("Loaded texture: " + path + " with Nearest filter");
        return texture;
    }
    
    /**
     * Загружает текстуру с фильтром Linear (для гладких графических элементов)
     * 
     * @param path путь к файлу текстуры
     * @return загруженная текстура с фильтром Linear
     */
    public static Texture loadSmoothTexture(String path) {
        Texture texture = new Texture(path);
        // Используем Linear фильтр для гладких текстур
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        Logger.info("Loaded texture: " + path + " with Linear filter");
        return texture;
    }
    
    /**
     * Настраивает фильтр текстуры на Nearest (пиксель-арт)
     * 
     * @param texture текстура
     */
    public static void setPixelArtFilter(Texture texture) {
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }
    
    /**
     * Настраивает фильтр текстуры на Linear (гладкая)
     * 
     * @param texture текстура
     */
    public static void setSmoothFilter(Texture texture) {
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }
}
