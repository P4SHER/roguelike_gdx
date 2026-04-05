package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * UI компонент для отображения текста.
 */
public class UIText extends UIComponent {
    private String text;
    private BitmapFont font;
    private com.badlogic.gdx.graphics.Color color;

    public UIText(float x, float y, String text, BitmapFont font) {
        super(x, y, 100, 20); // Примерные размеры
        this.text = text;
        this.font = font;
        this.color = com.badlogic.gdx.graphics.Color.WHITE;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible || font == null) {
            return;
        }

        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setColor(com.badlogic.gdx.graphics.Color color) {
        this.color = color;
    }

    public com.badlogic.gdx.graphics.Color getColor() {
        return color;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public BitmapFont getFont() {
        return font;
    }
}
