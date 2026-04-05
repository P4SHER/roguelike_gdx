package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Level;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import java.util.HashSet;
import java.util.Set;

/**
 * Рендерер слоя тумана войны (видимость).
 * Отрисовывает два слоя: видимое (прозрачно) и неисследованное (темно).
 * Optimization: Caches visibility calculations and only recalculates when player moves to new tile.
 */
public class FogLayerRenderer extends AbstractLayerRenderer {
    private final Level level;
    private final Player player;
    private final int visionRange;
    private final boolean[][] explored;
    private final boolean[][] visible;
    private Sprite fogSprite;
    private Sprite exploredSprite;

    // Cache optimization
    private int lastCachedPlayerX = -1;
    private int lastCachedPlayerY = -1;
    private final Set<String> cachedVisibleTiles = new HashSet<>();
    private boolean cacheValid = false;

    public FogLayerRenderer(Level level, Player player) {
        super("FogLayer");
        this.level = level;
        this.player = player;
        this.visionRange = 10; // Радиус видения в тайлах

        int height = level.getTiles().length;
        int width = level.getTiles()[0].length;
        this.explored = new boolean[height][width];
        this.visible = new boolean[height][width];

        initializeFogSprites();
    }

    /**
     * Инициализирует спрайты тумана.
     */
    private void initializeFogSprites() {
        // Создаем спрайт полной темноты (неисследованное)
        Pixmap pixmapFog = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapFog.setColor(0, 0, 0, 0.8f); // Черный с прозрачностью
        pixmapFog.fill();
        Texture fogTexture = new Texture(pixmapFog);
        fogSprite = new Sprite(fogTexture);
        pixmapFog.dispose();

        // Создаем спрайт для исследованного (полутень)
        Pixmap pixmapExplored = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapExplored.setColor(0.3f, 0.3f, 0.3f, 0.4f); // Серый с прозрачностью
        pixmapExplored.fill();
        Texture exploredTexture = new Texture(pixmapExplored);
        exploredSprite = new Sprite(exploredTexture);
        pixmapExplored.dispose();
    }

    @Override
    public void init() {
        super.init();
        Logger.debug("FogLayer инициализирован с видением " + visionRange + " тайлов");
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || level == null || player == null) {
            return;
        }

        Coordinates playerCoord = player.getCoordinates();
        if (playerCoord == null) {
            return;
        }

        int playerTileX = playerCoord.getX();
        int playerTileY = playerCoord.getY();

        // Check if player moved to a new tile
        boolean playerMoved = playerTileX != lastCachedPlayerX || playerTileY != lastCachedPlayerY;

        // Recalculate visibility only when player moves to new tile
        if (playerMoved) {
            updateFogOfWar();
            lastCachedPlayerX = playerTileX;
            lastCachedPlayerY = playerTileY;
            cacheValid = true;
            Logger.debug("FOW cache invalidated at (" + playerTileX + ", " + playerTileY + ")");
        }

        // Render fog layer
        renderFogLayer(batch, camera);
    }

    /**
     * Renders the fog of war layer using cached visibility data.
     */
    private void renderFogLayer(SpriteBatch batch, OrthographicCamera camera) {
        boolean[][] tiles = visible;
        int height = tiles.length;
        int width = height > 0 ? tiles[0].length : 0;

        // Отрисовываем слой тумана
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float pixelX = x * Constants.TILE_SIZE;
                float pixelY = y * Constants.TILE_SIZE;

                if (tiles[y][x]) {
                    // Видимая область - ничего не рисуем (прозрачно)
                    continue;
                } else if (explored[y][x]) {
                    // Исследованная область - рисуем полутень
                    renderFogTile(batch, exploredSprite, pixelX, pixelY);
                } else {
                    // Неисследованная - рисуем полную темноту
                    renderFogTile(batch, fogSprite, pixelX, pixelY);
                }
            }
        }
    }

    /**
     * Отрисовывает один тайл тумана.
     */
    private void renderFogTile(SpriteBatch batch, Sprite sprite, float x, float y) {
        sprite.setPosition(x, y);
        sprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
        sprite.draw(batch);
    }

    /**
     * Обновляет туман войны на основе позиции игрока.
     * Called only when player moves to a new tile.
     */
    private void updateFogOfWar() {
        Coordinates playerCoord = player.getCoordinates();
        if (playerCoord == null) {
            return;
        }

        int playerX = playerCoord.getX();
        int playerY = playerCoord.getY();

        // Очищаем видимость (но не исследованность)
        for (int y = 0; y < visible.length; y++) {
            for (int x = 0; x < visible[y].length; x++) {
                visible[y][x] = false;
            }
        }

        // Обновляем видимость в зависимости от видения игрока
        for (int y = 0; y < visible.length; y++) {
            for (int x = 0; x < visible[y].length; x++) {
                int distance = cullingSystem.getDistance(playerX, playerY, x, y);
                if (distance <= visionRange) {
                    visible[y][x] = true;
                    explored[y][x] = true; // Видимое всегда исследовано
                }
            }
        }
    }

    /**
     * Получает видение игрока.
     */
    public int getVisionRange() {
        return visionRange;
    }

    /**
     * Получает статус видимости тайла.
     */
    public boolean isVisible(int x, int y) {
        if (y >= 0 && y < visible.length && x >= 0 && x < visible[0].length) {
            return visible[y][x];
        }
        return false;
    }

    /**
     * Получает статус исследованности тайла.
     */
    public boolean isExplored(int x, int y) {
        if (y >= 0 && y < explored.length && x >= 0 && x < explored[0].length) {
            return explored[y][x];
        }
        return false;
    }

    @Override
    public void dispose() {
        if (fogSprite != null && fogSprite.getTexture() != null) {
            fogSprite.getTexture().dispose();
        }
        if (exploredSprite != null && exploredSprite.getTexture() != null) {
            exploredSprite.getTexture().dispose();
        }
        cachedVisibleTiles.clear();
        super.dispose();
    }
}
