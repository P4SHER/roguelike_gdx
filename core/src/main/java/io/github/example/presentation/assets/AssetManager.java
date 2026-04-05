package io.github.example.presentation.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import io.github.example.presentation.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер загрузки и кэширования спрайтов.
 * Организует спрайты по категориям (player, enemies, items, tiles, ui).
 */
public class AssetManager implements Disposable {
    private final Map<String, Sprite> spriteCache = new HashMap<>();
    private final Map<String, Texture> textureCache = new HashMap<>();

    private static final String BASE_PATH = "sprites/";
    private Sprite missingSprite;

    public AssetManager() {
        // Создаем fallback спрайт для ошибок
        createMissingSprite();
    }

    private void createMissingSprite() {
        // Создаем 32x32 пустой текстуру (пурпурная = ошибка)
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0f, 1f, 1f); // Пурпурный (ошибка)
        pixmap.fill();
        Texture missingTexture = new Texture(pixmap);
        pixmap.dispose();
        this.missingSprite = new Sprite(missingTexture);
    }

    /**
     * Загружает спрайт из файла.
     * Если спрайт уже в кэше, возвращает из кэша.
     *
     * @param path относительный путь без расширения (например: "player/idle_down")
     * @return Sprite или fallback спрайт если файл не найден
     */
    public Sprite getSprite(String path) {
        String key = path;
        if (spriteCache.containsKey(key)) {
            return spriteCache.get(key);
        }

        String fullPath = BASE_PATH + path + ".png";

        try {
            if (!Gdx.files.internal(fullPath).exists()) {
                Logger.warn("Спрайт не найден: " + fullPath);
                return new Sprite(missingSprite);
            }

            Texture texture = new Texture(Gdx.files.internal(fullPath));
            Sprite sprite = new Sprite(texture);

            spriteCache.put(key, sprite);
            textureCache.putIfAbsent(fullPath, texture);

            Logger.debug("Loading sprite: " + fullPath);
            return sprite;
        } catch (Exception e) {
            Logger.error("Ошибка загрузки спрайта: " + fullPath, e);
            return new Sprite(missingSprite);
        }
    }

    /**
     * Загружает спрайт-лист (анимированный спрайт).
     * Спрайт-лист расположен горизонтально.
     *
     * @param path путь без расширения
     * @param frameWidth ширина одного кадра
     * @param frameHeight высота одного кадра
     * @return массив спрайтов (кадры анимации)
     */
    public Sprite[] getSpriteSheet(String path, int frameWidth, int frameHeight) {
        String fullPath = BASE_PATH + path + ".png";

        try {
            if (!Gdx.files.internal(fullPath).exists()) {
                Logger.warn("Спрайт-лист не найден: " + fullPath);
                Sprite[] empty = new Sprite[1];
                empty[0] = new Sprite(missingSprite);
                return empty;
            }

            Texture texture = new Texture(Gdx.files.internal(fullPath));
            int cols = texture.getWidth() / frameWidth;
            int rows = texture.getHeight() / frameHeight;
            int frames = cols * rows;

            Sprite[] sprites = new Sprite[frames];
            int index = 0;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    Sprite sprite = new Sprite(texture,
                        col * frameWidth,
                        row * frameHeight,
                        frameWidth,
                        frameHeight
                    );
                    sprites[index++] = sprite;
                }
            }

            textureCache.putIfAbsent(fullPath, texture);
            Logger.debug("Loading sprite sheet: " + fullPath + " (" + frames + " frames)");
            return sprites;
        } catch (Exception e) {
            Logger.error("Ошибка загрузки спрайт-листа: " + fullPath, e);
            Sprite[] empty = new Sprite[1];
            empty[0] = new Sprite(missingSprite);
            return empty;
        }
    }

    /**
     * Выгружает спрайт из кэша и текстуру.
     */
    public void unloadSprite(String path) {
        if (spriteCache.containsKey(path)) {
            spriteCache.remove(path);
        }
    }

    /**
     * Очищает кэш спрайтов (не текстур).
     * Используется при смене уровня.
     */
    public void clearSpriteCache() {
        spriteCache.clear();
    }

    /**
     * Получает информацию о кэше для отладки.
     */
    public String getCacheInfo() {
        return String.format("Спрайты в кэше: %d, Текстуры: %d", 
            spriteCache.size(), textureCache.size());
    }

    @Override
    public void dispose() {
        for (Texture texture : textureCache.values()) {
            if (texture != null && !texture.isManaged()) {
                texture.dispose();
            }
        }
        spriteCache.clear();
        textureCache.clear();

        if (missingSprite != null && missingSprite.getTexture() != null) {
            missingSprite.getTexture().dispose();
        }
    }
}
