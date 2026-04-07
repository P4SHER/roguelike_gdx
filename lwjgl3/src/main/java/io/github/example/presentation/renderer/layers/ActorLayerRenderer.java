package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.level.Level;
import io.github.example.presentation.util.Logger;

/**
 * Рендерер для слоя персонажей (игрок и враги)
 */
public class ActorLayerRenderer {
    
    private Texture whitePixelTexture;
    
    public ActorLayerRenderer() {
        createWhitePixelTexture();
        Logger.info("ActorLayerRenderer initialized");
    }
    
    /**
     * Рендерит слой персонажей (игрок и враги)
     */
    public void render(SpriteBatch batch, Level level, Player player, float cameraX, float cameraY, 
                       float viewportWidth, float viewportHeight) {
        if (level == null || player == null) {
            return;
        }
        
        // Рендерим игрока (белый квадрат)
        float playerX = player.getCoordinates().getX();
        float playerY = player.getCoordinates().getY();
        
        batch.setColor(1f, 1f, 1f, 1f);  // Белый
        batch.draw(whitePixelTexture, playerX, playerY, 1, 1);
        
        // Рендерим врагов (красные квадраты)
        if (level.getEnemies() != null && !level.getEnemies().isEmpty()) {
            for (Enemy enemy : level.getEnemies().values()) {
                float enemyX = enemy.getCoordinates().getX();
                float enemyY = enemy.getCoordinates().getY();
                
                batch.setColor(1f, 0.2f, 0.2f, 1f);  // Красный
                batch.draw(whitePixelTexture, enemyX, enemyY, 1, 1);
            }
        }
        
        // Сброс цвета
        batch.setColor(1, 1, 1, 1);
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
            Logger.info("ActorLayerRenderer disposed");
        }
    }
}
