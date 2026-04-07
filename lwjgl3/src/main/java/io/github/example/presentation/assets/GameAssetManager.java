package io.github.example.presentation.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер ассетов для загрузки и управления текстурами
 */
public class GameAssetManager {
    private final AssetManager assetManager;
    private final Map<String, TextureRegion> textureRegions = new HashMap<>();

    private static final String ASSETS_PATH = "sprites/";
    private static final int TILE_SIZE = 32;

    public GameAssetManager() {
        this.assetManager = new AssetManager();
    }

    /**
     * Загрузить все ассеты
     */
    public void load() {
        // Загружаем спрайты
        loadCharacterSprites();
        loadItemSprites();
        loadTileSprites();

        // Блокируем до полной загрузки
        assetManager.finishLoading();
    }

    /**
     * Загрузить спрайты персонажей
     */
    private void loadCharacterSprites() {
        String path = ASSETS_PATH + "characters/";
        assetManager.load(path + "player.png", Texture.class);
        assetManager.load(path + "enemy_zombie.png", Texture.class);
        assetManager.load(path + "enemy_ogre.png", Texture.class);
        assetManager.load(path + "enemy_snake.png", Texture.class);
        assetManager.load(path + "enemy_ghost.png", Texture.class);
        assetManager.load(path + "enemy_vampire.png", Texture.class);
    }

    /**
     * Загрузить спрайты предметов
     */
    private void loadItemSprites() {
        String path = ASSETS_PATH + "items/";
        assetManager.load(path + "weapon.png", Texture.class);
        assetManager.load(path + "scroll.png", Texture.class);
        assetManager.load(path + "potion.png", Texture.class);
        assetManager.load(path + "food.png", Texture.class);
        assetManager.load(path + "treasure.png", Texture.class);
    }

    /**
     * Загрузить спрайты тайлов
     */
    private void loadTileSprites() {
        String path = ASSETS_PATH + "tiles/";
        assetManager.load(path + "tileset.png", Texture.class);
        assetManager.load(path + "tileset_mono.png", Texture.class);
    }

    /**
     * Получить TextureRegion по пути
     */
    public TextureRegion getTextureRegion(String path, int x, int y) {
        String key = path + "_" + x + "_" + y;

        if (!textureRegions.containsKey(key)) {
            Texture texture = assetManager.get(path, Texture.class);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            TextureRegion region = new TextureRegion(texture, x, y, TILE_SIZE, TILE_SIZE);
            textureRegions.put(key, region);
        }

        return textureRegions.get(key);
    }

    /**
     * Получить всю текстуру
     */
    public Texture getTexture(String path) {
        return assetManager.get(path, Texture.class);
    }

    /**
     * Очистить все ассеты
     */
    public void dispose() {
        assetManager.dispose();
        textureRegions.clear();
    }

    /**
     * Проверить, загружены ли все ассеты
     */
    public boolean isLoaded() {
        return assetManager.isFinished();
    }

    /**
     * Получить прогресс загрузки (0-1)
     */
    public float getProgress() {
        return assetManager.getProgress();
    }
}
