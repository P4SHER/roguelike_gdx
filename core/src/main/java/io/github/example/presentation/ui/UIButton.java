package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.util.ColorScheme;

/**
 * UI кнопка с состояниями (idle, hover, pressed).
 */
public class UIButton extends UIComponent {
    private String label;
    private ButtonListener listener;
    private ButtonState state = ButtonState.IDLE;

    public enum ButtonState {
        IDLE,
        HOVER,
        PRESSED
    }

    public interface ButtonListener {
        void onClicked();
    }

    public UIButton(float x, float y, float width, float height, String label) {
        super(x, y, width, height);
        this.label = label;
    }

    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible) {
            return;
        }

        // Определяем цвет в зависимости от состояния
        com.badlogic.gdx.graphics.Color buttonColor;
        switch (state) {
            case HOVER:
                buttonColor = ColorScheme.BUTTON_HOVER;
                break;
            case PRESSED:
                buttonColor = ColorScheme.BUTTON_PRESSED;
                break;
            default:
                buttonColor = ColorScheme.BUTTON_IDLE;
        }

        // Рисуем прямоугольник кнопки (позже будут спрайты)
        batch.setColor(buttonColor);
        batch.draw(createButtonTexture(), x, y, width, height);
        batch.setColor(1, 1, 1, 1); // Reset

        // Рисуем текст (упрощенно)
        System.out.println("Button: " + label + " [" + state + "]");
    }

    @Override
    public boolean handleClick(float screenX, float screenY) {
        if (!super.handleClick(screenX, screenY)) {
            return false;
        }

        if (listener != null) {
            listener.onClicked();
        }
        return true;
    }

    @Override
    public void handleHover(float screenX, float screenY) {
        if (!visible || !enabled) {
            state = ButtonState.IDLE;
            return;
        }

        com.badlogic.gdx.math.Rectangle bounds = new com.badlogic.gdx.math.Rectangle(x, y, width, height);
        if (bounds.contains(screenX, screenY)) {
            state = ButtonState.HOVER;
        } else {
            state = ButtonState.IDLE;
        }
    }

    public void setState(ButtonState state) {
        this.state = state;
    }

    public ButtonState getState() {
        return state;
    }

    // Временный метод для создания текстуры кнопки
    private static com.badlogic.gdx.graphics.Texture createButtonTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1f);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
