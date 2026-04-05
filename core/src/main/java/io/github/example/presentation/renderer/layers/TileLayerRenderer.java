package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.domain.level.Level;
import io.github.example.domain.level.SpaceType;
import io.github.example.domain.level.Tile;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Constants;
import java.util.HashMap;
import java.util.Map;

/**
 * Рендерер слоя тайлов (пол, стены, выходы).
 * Отрисовывает сетку уровня с поддержкой culling.
 */
public class TileLayerRenderer extends AbstractLayerRenderer {
    private final Level level;
    private final AssetManager assetManager;
    private final Map<SpaceType, Sprite> tileSpritesCache = new HashMap<>();
    private int tilesRendered;

    public TileLayerRenderer(Level level, AssetManager assetManager) {
        super("TileLayer");
        this.level = level;
        this.assetManager = assetManager;
    }

    @Override
    public void init() {
        super.init();
        // Кэшируем спрайты для каждого типа тайла
        cacheAllTileSprites();
        debugLog("Инициализирован с " + tileSpritesCache.size() + " типами тайлов");
    }

    /**
     * Кэширует спрайты для всех типов тайлов.
     */
    private void cacheAllTileSprites() {
        tileSpritesCache.put(SpaceType.Room, getTileSpriteForType(SpaceType.Room));
        tileSpritesCache.put(SpaceType.Passage, getTileSpriteForType(SpaceType.Passage));
        tileSpritesCache.put(SpaceType.Wall, getTileSpriteForType(SpaceType.Wall));
        tileSpritesCache.put(SpaceType.Exit, getTileSpriteForType(SpaceType.Exit));
        tileSpritesCache.put(SpaceType.Nothing, getTileSpriteForType(SpaceType.Nothing));
        tileSpritesCache.put(SpaceType.Door, getTileSpriteForType(SpaceType.Door));
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || level == null) {
            return;
        }

        tilesRendered = 0;
        Rectangle visibleBounds = new Rectangle(
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight
        );

        Tile[][] tiles = level.getTiles();
        if (tiles == null) {
            return;
        }

        int height = tiles.length;
        int width = height > 0 ? tiles[0].length : 0;

        // Рендер только видимых тайлов
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];

                float pixelX = x * Constants.TILE_SIZE;
                float pixelY = y * Constants.TILE_SIZE;

                if (!cullingSystem.isVisible(pixelX, pixelY, Constants.TILE_SIZE, Constants.TILE_SIZE, visibleBounds)) {
                    continue;
                }

                renderTile(batch, tile, pixelX, pixelY);
                tilesRendered++;
            }
        }

        debugLog("Отрисовано тайлов: " + tilesRendered);
    }

    /**
     * Отрисовывает один тайл.
     */
    private void renderTile(SpriteBatch batch, Tile tile, float x, float y) {
        if (tile == null || tile.getSpaceType() == null) {
            return;
        }

        Sprite sprite = tileSpritesCache.get(tile.getSpaceType());
        if (sprite == null) {
            sprite = tileSpritesCache.get(SpaceType.Nothing);
        }

        if (sprite != null) {
            sprite.setPosition(x, y);
            sprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
            sprite.draw(batch);
        }
    }

    /**
     * Получает спрайт для типа тайла.
     * Использует асет менеджер для загрузки спрайтов.
     */
    private Sprite getTileSpriteForType(SpaceType type) {
        String spritePath = mapSpaceTypeToSpritePath(type);
        return assetManager.getSprite(spritePath);
    }

    /**
     * Маппирует SpaceType на путь спрайта.
     */
    private String mapSpaceTypeToSpritePath(SpaceType type) {
        switch (type) {
            case Room:
                return "tiles/floor";
            case Passage:
                return "tiles/passage";
            case Wall:
                return "tiles/wall";
            case Exit:
                return "tiles/exit";
            case Door:
                return "tiles/door";
            case Nothing:
            default:
                return "tiles/empty";
        }
    }

    /**
     * Получает количество отрисованных тайлов.
     */
    public int getTilesRendered() {
        return tilesRendered;
    }

    @Override
    public void dispose() {
        tileSpritesCache.clear();
        super.dispose();
    }
}
