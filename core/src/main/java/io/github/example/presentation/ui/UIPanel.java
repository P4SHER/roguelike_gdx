package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * UI панель - контейнер для других UI компонентов.
 */
public class UIPanel extends UIComponent {
    private List<UIComponent> children = new ArrayList<>();
    private Color backgroundColor;
    private boolean hasBorder = false;
    private Color borderColor = Color.WHITE;

    public UIPanel(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.backgroundColor = new Color(0.1f, 0.1f, 0.1f, 0.8f);
    }

    /**
     * Добавляет дочерний компонент в панель.
     */
    public void addChild(UIComponent component) {
        children.add(component);
    }

    /**
     * Удаляет дочерний компонент.
     */
    public void removeChild(UIComponent component) {
        children.remove(component);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible) {
            return;
        }

        // Рисуем фон панели
        batch.setColor(backgroundColor);
        batch.draw(createPanelTexture(), x, y, width, height);
        batch.setColor(1, 1, 1, 1); // Reset

        // Рисуем границу если нужна
        if (hasBorder) {
            drawBorder(batch);
        }

        // Рисуем всех детей
        for (UIComponent child : children) {
            child.render(batch);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Обновляем всех детей
        for (UIComponent child : children) {
            child.update(delta);
        }
    }

    private void drawBorder(SpriteBatch batch) {
        batch.setColor(borderColor);
        // Горизонтальные линии
        batch.draw(createLineTexture(), x, y + height, width, 1);
        batch.draw(createLineTexture(), x, y, width, 1);
        // Вертикальные линии
        batch.draw(createLineTexture(), x, y, 1, height);
        batch.draw(createLineTexture(), x + width, y, 1, height);
        batch.setColor(1, 1, 1, 1); // Reset
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setBorder(boolean hasBorder, Color borderColor) {
        this.hasBorder = hasBorder;
        this.borderColor = borderColor;
    }

    public List<UIComponent> getChildren() {
        return children;
    }

    // Временные методы для создания текстур
    private static com.badlogic.gdx.graphics.Texture createPanelTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private static com.badlogic.gdx.graphics.Texture createLineTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
