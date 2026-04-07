package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;

/**
 * Рендер тумана войны - скрывает невидимые области уровня
 */
public class FogLayerRenderer implements LayerRenderer {
    private final SpriteBatch batch;
    private static final float FOG_VISIBILITY_RANGE = 10f;  // Дальность видения в тайлах

    public FogLayerRenderer(SpriteBatch batch) {
        this.batch = batch;
    }

    /**
     * Рендер тумана войны с видимостью вокруг игрока
     */
    public void render(SpriteBatch batch, Level level, Player player, float cameraX, float cameraY, float viewportWidth, float viewportHeight) {
        // Рендер тумана - полупрозрачные чёрные квадраты над невидимыми областями
        Texture whitePixel = createWhitePixelTexture();

        float playerX = player.getCoordinates().getX();
        float playerY = player.getCoordinates().getY();

        int startX = Math.max(0, (int)cameraX - (int)viewportWidth);
        int startY = Math.max(0, (int)cameraY - (int)viewportHeight);
        int endX = Math.min(200, (int)cameraX + (int)viewportWidth + 1);
        int endY = Math.min(200, (int)cameraY + (int)viewportHeight + 1);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                float distance = (float) Math.sqrt(
                    Math.pow(x - playerX, 2) + Math.pow(y - playerY, 2)
                );

                // Если вне дальности видения - рисуем туман
                if (distance > FOG_VISIBILITY_RANGE) {
                    batch.setColor(0, 0, 0, 0.7f);  // Чёрный с прозрачностью
                    batch.draw(whitePixel, x, y, 1, 1);
                }
            }
        }

        batch.setColor(1, 1, 1, 1);  // Сброс цвета
        whitePixel.dispose();
    }

    /**
     * Создание белой текстуры 1x1 для рисования прямоугольников
     */
    private Texture createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    @Override
    public void dispose() {
        // Текстуры создаются и удаляются в методе render
    }
}


