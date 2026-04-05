package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * UI компонент для отображения иконок/спрайтов.
 */
public class UIIcon extends UIComponent {
    private Sprite sprite;
    private String label; // Опциональный текст под иконкой

    public UIIcon(float x, float y, float width, float height, Sprite sprite) {
        super(x, y, width, height);
        this.sprite = sprite;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible || sprite == null) {
            return;
        }

        sprite.setBounds(x, y, width, height);
        sprite.draw(batch);

        // Рисуем текст если есть
        if (label != null && !label.isEmpty()) {
            System.out.println("Icon label: " + label);
        }
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
