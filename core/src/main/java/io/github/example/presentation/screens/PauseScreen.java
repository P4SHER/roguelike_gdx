package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.Logger;

/**
 * Экран паузы.
 * Отображается поверх GameScreen с полупрозрачным оверлеем.
 * Меню: "Продолжить", "Инвентарь", "Выход в меню".
 */
public class PauseScreen implements Screen {
    private int selectedOption = 0; // 0 = Продолжить, 1 = Инвентарь, 2 = Выход
    private static final int OPTION_COUNT = 3;
    private static Texture filledTexture;
    private static final Object textureLock = new Object();

    private PauseCallback callback;

    public interface PauseCallback {
        void onResume();
        void onInventory();
        void onMainMenu();
    }

    public PauseScreen(PauseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        Logger.info("PauseScreen показан");
        selectedOption = 0; // Первый элемент = Продолжить
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Рисуем полупрозрачный оверлей
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(getFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1); // Reset color

        // Рисуем меню в центре экрана
        float centerX = Constants.SCREEN_WIDTH / 2f;
        float centerY = Constants.SCREEN_HEIGHT / 2f;

        // Заголовок
        Logger.info("=== ПАУЗА ===");

        String[] options = {"Продолжить", "Инвентарь", "Выход в меню"};

        for (int i = 0; i < OPTION_COUNT; i++) {
            if (i == selectedOption) {
                Logger.info("[> " + options[i] + " <]");
            } else {
                Logger.info("   " + options[i]);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Адаптируется к размеру окна
    }

    @Override
    public void hide() {
        Logger.debug("PauseScreen скрыт");
    }

    @Override
    public void dispose() {
        disposeFilledTexture();
        Logger.debug("PauseScreen очищен");
    }

    @Override
    public String getName() {
        return "PauseScreen";
    }

    /**
     * Обрабатывает ввод паузы.
     */
    public void handleInput(MenuInput input) {
        switch (input) {
            case UP:
                selectedOption = (selectedOption - 1 + OPTION_COUNT) % OPTION_COUNT;
                break;

            case DOWN:
                selectedOption = (selectedOption + 1) % OPTION_COUNT;
                break;

            case SELECT:
                selectOption();
                break;

            case CANCEL:
                // Esc закрывает паузу (продолжить)
                if (callback != null) {
                    callback.onResume();
                }
                break;

            default:
                break;
        }
    }

    private void selectOption() {
        if (callback != null) {
            switch (selectedOption) {
                case 0:
                    Logger.debug("Продолжить выбрано");
                    callback.onResume();
                    break;

                case 1:
                    Logger.debug("Инвентарь выбран");
                    callback.onInventory();
                    break;

                case 2:
                    Logger.debug("Выход в меню выбран");
                    callback.onMainMenu();
                    break;
            }
        }
    }

    public enum MenuInput {
        UP, DOWN, LEFT, RIGHT, SELECT, CANCEL
    }

    /**
     * Gets or creates a static filled texture.
     */
    private static Texture getFilledTexture() {
        if (filledTexture == null) {
            synchronized (textureLock) {
                if (filledTexture == null) {
                    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                    pixmap.setColor(0, 0, 0, 0.7f);
                    pixmap.fill();
                    filledTexture = new Texture(pixmap);
                    pixmap.dispose();
                    Logger.debug("PauseScreen filledTexture created");
                }
            }
        }
        return filledTexture;
    }

    /**
     * Disposes the static texture.
     */
    public static void disposeFilledTexture() {
        synchronized (textureLock) {
            if (filledTexture != null) {
                filledTexture.dispose();
                filledTexture = null;
                Logger.debug("PauseScreen filledTexture disposed");
            }
        }
    }
}
