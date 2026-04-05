package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import io.github.example.presentation.util.ColorScheme;

/**
 * UI полоса прогресса (для здоровья, маны и т.д.).
 */
public class UIProgressBar extends UIComponent {
    private float value = 100f; // 0-100
    private float maxValue = 100f;
    private Color fillColor = ColorScheme.HEALTH_FULL;
    private Color backgroundColor = new Color(0.3f, 0.3f, 0.3f, 1f);

    public UIProgressBar(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible) {
            return;
        }

        // Рисуем фон
        batch.setColor(backgroundColor);
        batch.draw(createTexture(), x, y, width, height);

        // Рисуем заполненную часть
        float filledWidth = (value / maxValue) * width;
        batch.setColor(fillColor);
        batch.draw(createTexture(), x, y, filledWidth, height);

        batch.setColor(1, 1, 1, 1); // Reset
    }

    public void setValue(float value) {
        this.value = Math.max(0, Math.min(value, maxValue));

        // Меняем цвет в зависимости от здоровья
        float percentage = this.value / maxValue;
        if (percentage > 0.5f) {
            fillColor = ColorScheme.HEALTH_FULL;
        } else if (percentage > 0.2f) {
            fillColor = ColorScheme.HEALTH_LOW;
        } else {
            fillColor = ColorScheme.HEALTH_CRITICAL;
        }
    }

    public float getValue() {
        return value;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setFillColor(Color color) {
        this.fillColor = color;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    private static com.badlogic.gdx.graphics.Texture createTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
