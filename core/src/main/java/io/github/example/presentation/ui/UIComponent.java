package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Базовый компонент UI.
 * Все UI элементы (кнопки, текст, панели) наследуют этот класс.
 */
public abstract class UIComponent {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected boolean visible = true;
    protected boolean enabled = true;

    // Якоря для позиционирования
    public enum Anchor {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT,
        MIDDLE_LEFT, CENTER, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    protected Anchor anchor = Anchor.TOP_LEFT;

    public UIComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Отрисовывает компонент.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Обновляет логику компонента.
     */
    public void update(float delta) {
        // По умолчанию ничего не обновляется
    }

    /**
     * Обрабатывает клик по компоненту.
     * Возвращает true, если клик обработан.
     */
    public boolean handleClick(float screenX, float screenY) {
        if (!visible || !enabled) {
            return false;
        }

        // Проверяем, попадает ли клик в границы компонента
        Rectangle bounds = new Rectangle(x, y, width, height);
        return bounds.contains(screenX, screenY);
    }

    /**
     * Обрабатывает наведение курсора.
     */
    public void handleHover(float screenX, float screenY) {
        // По умолчанию ничего не делает
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Anchor getAnchor() { return anchor; }
    public void setAnchor(Anchor anchor) { this.anchor = anchor; }

    /**
     * Получает информацию компонента для отладки.
     */
    public String getDebugInfo() {
        return String.format("%s @ (%.0f, %.0f) [%.0fx%.0f] visible=%b enabled=%b",
            getClass().getSimpleName(), x, y, width, height, visible, enabled);
    }
}
