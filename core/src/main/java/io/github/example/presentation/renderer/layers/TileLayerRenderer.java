package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.domain.level.Level;
import io.github.example.domain.level.SpaceType;
import io.github.example.domain.level.Tile;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Constants;

/**
 * Рендерер слоя тайлов (пол, стены, выходы).
 * Отрисовывает сетку уровня с поддержкой culling.
 */
public class TileLayerRenderer extends AbstractLayerRenderer {
    private final Level level;
    private final AssetManager assetManager;
    private int tilesRendered;

    public TileLayerRenderer(Level level, AssetManager assetManager) {
        super("TileLayer");
        this.level = level;
        this.assetManager = assetManager;
    }

    @Override
    public void init() {
        super.init();
        debugLog("Инициализирован для рендеринга тайлов из атласа");
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

        SpaceType type = tile.getSpaceType();
        
        // Маппируем тип тайла на координаты в атласе
        int tileX = 0;
        int tileY = 0;
        
        switch (type) {
            case Room:
                tileX = 0;
                tileY = 0;
                break;
            case Wall:
                tileX = 1;
                tileY = 0;
                break;
            case Passage:
                tileX = 2;
                tileY = 0;
                break;
            case Door:
                tileX = 3;
                tileY = 0;
                break;
            case Exit:
                tileX = 4;
                tileY = 0;
                break;
            case Nothing:
            default:
                tileX = 5;
                tileY = 0;
                break;
        }

        // Получаем TextureRegion
        TextureRegion region = assetManager.getTileRegion(tileX, tileY);
        if (region == null) {
            // Fallback к простому тайлу
            region = assetManager.getFloorTile();
        }

        if (region != null) {
            batch.draw(region, x, y, Constants.TILE_SIZE, Constants.TILE_SIZE);
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
        super.dispose();
    }
}
