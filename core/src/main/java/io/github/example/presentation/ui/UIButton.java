package io.github.example.presentation.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.Logger;

/**
 * UI кнопка с состояниями (idle, hover, pressed).
 * Uses a static texture to avoid memory leaks from repeated texture creation.
 */
public class UIButton extends UIComponent {
    private String label;
    private ButtonListener listener;
    private ButtonState state = ButtonState.IDLE;

    // Static texture shared by all buttons
    private static Texture buttonTexture;
    private static final Object textureSync = new Object();

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
        initializeButtonTexture();
    }

    /**
     * Initializes the static button texture on first use.
     */
    public static void initializeButtonTexture() {
        if (buttonTexture != null) {
            return;
        }
        synchronized (textureSync) {
            if (buttonTexture == null) {
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(0.2f, 0.2f, 0.2f, 1f);
                pixmap.fill();
                buttonTexture = new Texture(pixmap);
                pixmap.dispose();
                Logger.debug("Button texture initialized");
            }
        }
    }

    /**
     * Disposes the static button texture when no longer needed.
     */
    public static void disposeButtonTexture() {
        synchronized (textureSync) {
            if (buttonTexture != null) {
                buttonTexture.dispose();
                buttonTexture = null;
                Logger.debug("Button texture disposed");
            }
        }
    }

    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!visible) {
            return;
        }

        // Determine color based on state
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

        // Draw button rectangle
        if (buttonTexture != null) {
            batch.setColor(buttonColor);
            batch.draw(buttonTexture, x, y, width, height);
            batch.setColor(1, 1, 1, 1); // Reset
        }

        // Debug logging
        Logger.debug("Button: " + label + " [" + state + "]");
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
}
