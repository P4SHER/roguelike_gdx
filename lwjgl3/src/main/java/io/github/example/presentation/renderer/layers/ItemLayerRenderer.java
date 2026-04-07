package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.entities.Item;
import io.github.example.domain.level.Level;
import io.github.example.presentation.util.Logger;

/**
 * Рендерер для слоя предметов (на полу)
 */
public class ItemLayerRenderer {
    
    private Texture whitePixelTexture;
    
    public ItemLayerRenderer() {
        createWhitePixelTexture();
        Logger.info("ItemLayerRenderer initialized");
    }
    
    /**
     * Рендерит слой предметов на полу
     */
    public void render(SpriteBatch batch, Level level) {
        if (level == null || level.getItems() == null || level.getItems().isEmpty()) {
            return;
        }
        
        // Рендерим предметы (цветные квадраты)
        for (Item item : level.getItems().values()) {
            float itemX = item.getPos().getX();
            float itemY = item.getPos().getY();
            
            // Цвет в зависимости от типа предмета
            switch (item.getType()) {
                case FOOD -> batch.setColor(0.2f, 1f, 0.2f, 1f);  // Зелёный
                case WEAPON -> batch.setColor(1f, 0.5f, 0f, 1f);  // Оранжевый
                case SCROLL -> batch.setColor(0.5f, 0.5f, 1f, 1f);  // Синий
                case ELIXIR -> batch.setColor(1f, 0.5f, 1f, 1f);  // Магента
                case TREASURE -> batch.setColor(1f, 1f, 0f, 1f);  // Жёлтый
                default -> batch.setColor(1f, 1f, 1f, 1f);  // Белый
            }
            
            batch.draw(whitePixelTexture, itemX, itemY, 1, 1);
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
            Logger.info("ItemLayerRenderer disposed");
        }
    }
}
