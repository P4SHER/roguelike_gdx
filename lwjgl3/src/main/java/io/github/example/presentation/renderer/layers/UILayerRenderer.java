package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;

/**
 * Рендер пользовательского интерфейса (HUD) - здоровье, голод, уровень
 */
public class UILayerRenderer implements LayerRenderer {
    private final BitmapFont font;
    private final SpriteBatch batch;
    private static final float UI_PADDING = 10f;
    private static final float LINE_HEIGHT = 20f;

    public UILayerRenderer(SpriteBatch batch, BitmapFont font) {
        this.batch = batch;
        this.font = font;
    }

    /**
     * Специальный метод рендеринга UI - принимает нужные параметры
     */
    public void render(SpriteBatch batch, Level level, Player player, float screenWidth, float screenHeight) {
        // Рендер HUD в верхнем левом углу экрана
        renderPlayerStats(level, player, screenWidth, screenHeight);
        renderLevelInfo(level, screenWidth, screenHeight);
    }

    /**
     * Рендер статистики игрока (здоровье, голод, опыт)
     */
    private void renderPlayerStats(Level level, Player player, float screenWidth, float screenHeight) {
        font.setColor(Color.WHITE);

        float y = screenHeight - UI_PADDING - LINE_HEIGHT;

        // Здоровье
        String healthText = String.format("HP: %d/%d",
            player.getStats().getCurrentHealth(),
            player.getStats().getMaxHealth());
        font.draw(batch, healthText, UI_PADDING, y);
        y -= LINE_HEIGHT;

        // Атака
        String damageText = String.format("ATK: %d", player.getDamage());
        font.draw(batch, damageText, UI_PADDING, y);
        y -= LINE_HEIGHT;

        // Уровень (берём из количества врагов, убитых)
        String statsText = String.format("STR: %d, AGI: %d",
            player.getStats().getStrength(),
            player.getStats().getAgility());
        font.draw(batch, statsText, UI_PADDING, y);
    }

    /**
     * Рендер информации о уровне
     */
    private void renderLevelInfo(Level level, float screenWidth, float screenHeight) {
        font.setColor(Color.LIGHT_GRAY);

        float y = screenHeight - UI_PADDING - LINE_HEIGHT;
        float x = screenWidth - 200f; // Правый верхний угол

        String enemiesText = String.format("Enemies: %d", level.getEnemies().size());
        font.draw(batch, enemiesText, x, y);
        y -= LINE_HEIGHT;

        String itemsText = String.format("Items: %d", level.getItems().size());
        font.draw(batch, itemsText, x, y);
    }

    @Override
    public void dispose() {
        // BitmapFont управляется главным рендером
    }
}


