package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;

/**
 * Экран паузы.
 * Отображается поверх GameScreen с полупрозрачным оверлеем.
 * Меню: "Продолжить", "Инвентарь", "Выход в меню".
 */
public class PauseScreen implements Screen {
    private int selectedOption = 0; // 0 = Продолжить, 1 = Инвентарь, 2 = Выход
    private static final int OPTION_COUNT = 3;

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
        System.out.println("PauseScreen показан");
        selectedOption = 0; // Первый элемент = Продолжить
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Рисуем полупрозрачный оверлей
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(createFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1); // Reset color

        // Рисуем меню в центре экрана
        float centerX = Constants.SCREEN_WIDTH / 2f;
        float centerY = Constants.SCREEN_HEIGHT / 2f;

        // Заголовок
        System.out.println("=== ПАУЗА ===");

        String[] options = {"Продолжить", "Инвентарь", "Выход в меню"};

        for (int i = 0; i < OPTION_COUNT; i++) {
            if (i == selectedOption) {
                System.out.println("[> " + options[i] + " <]");
            } else {
                System.out.println("   " + options[i]);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Адаптируется к размеру окна
    }

    @Override
    public void hide() {
        System.out.println("PauseScreen скрыт");
    }

    @Override
    public void dispose() {
        System.out.println("PauseScreen очищен");
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
                    System.out.println("Продолжить выбрано");
                    callback.onResume();
                    break;

                case 1:
                    System.out.println("Инвентарь выбран");
                    callback.onInventory();
                    break;

                case 2:
                    System.out.println("Выход в меню выбран");
                    callback.onMainMenu();
                    break;
            }
        }
    }

    public enum MenuInput {
        UP, DOWN, LEFT, RIGHT, SELECT, CANCEL
    }

    // Временный метод для создания простой текстуры
    private static com.badlogic.gdx.graphics.Texture createFilledTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
