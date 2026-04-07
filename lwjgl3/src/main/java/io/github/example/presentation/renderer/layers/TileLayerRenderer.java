package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.level.Level;
import io.github.example.domain.level.SpaceType;
import io.github.example.presentation.util.Logger;

/**
 * Рендерер для слоя тайлов (стены, полы, коридоры)
 */
public class TileLayerRenderer {

    private Texture whitePixelTexture;

    public TileLayerRenderer() {
        createWhitePixelTexture();
        Logger.info("TileLayerRenderer initialized");
    }

    /**
     * Рендерит слой тайлов
     */
    public void render(SpriteBatch batch, Level level, float cameraX, float cameraY,
                       float viewportWidth, float viewportHeight) {
        if (level == null || level.getTiles() == null) {
            return;
        }

        int startX = Math.max(0, (int)cameraX - (int)viewportWidth);
        int startY = Math.max(0, (int)cameraY - (int)viewportHeight);
        int endX = Math.min(200, (int)cameraX + (int)viewportWidth + 1);
        int endY = Math.min(200, (int)cameraY + (int)viewportHeight + 1);

        var tiles = level.getTiles();

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                var tile = tiles[y][x];
                float[] color = getColorForTileType(tile.getSpaceType());

                batch.setColor(color[0], color[1], color[2], 1f);
                batch.draw(whitePixelTexture, x, y, 1, 1);
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    private float[] getColorForTileType(SpaceType spaceType) {
        return switch (spaceType) {
            case Wall -> new float[]{0.2f, 0.2f, 0.2f};
            case Room -> new float[]{0.8f, 0.8f, 0.8f};
            case Passage -> new float[]{0.5f, 0.5f, 0.5f};
            default -> new float[]{0.1f, 0.1f, 0.1f};
        };
    }

    private void createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();

        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();

        whitePixelTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        Logger.info("White pixel texture created with Nearest filter");
    }

    public void dispose() {
        if (whitePixelTexture != null) {
            whitePixelTexture.dispose();
            Logger.info("TileLayerRenderer disposed");
        }
    }
}
