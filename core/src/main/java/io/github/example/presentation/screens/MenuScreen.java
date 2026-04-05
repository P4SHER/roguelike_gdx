package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;

/**
 * Главное меню игры.
 * Кнопки: "Новая игра", "Загрузить", "Выход".
 */
public class MenuScreen implements Screen {
    private int selectedOption = 0; // 0 = Новая игра, 1 = Загрузить, 2 = Выход
    private static final int OPTION_COUNT = 3;

    private MenuCallback callback;

    public interface MenuCallback {
        void onNewGame();
        void onLoadGame();
        void onExit();
    }

    public MenuScreen(MenuCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        System.out.println("MenuScreen показан");
        selectedOption = 0;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Очищаем фон (черный)
        batch.setColor(ColorScheme.BACKGROUND_MENU);
        batch.draw(createFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1); // Reset color

        // Отрисовываем заголовок
        float centerX = Constants.SCREEN_WIDTH / 2f;
        float titleY = Constants.SCREEN_HEIGHT * 0.8f;

        // Отрисовываем кнопки (текстурировано позже, сейчас просто обозначим)
        float buttonWidth = Constants.BUTTON_WIDTH;
        float buttonHeight = Constants.BUTTON_HEIGHT;
        float startY = Constants.SCREEN_HEIGHT * 0.5f;
        float spacing = 80;

        String[] options = {"Новая игра", "Загрузить", "Выход"};

        for (int i = 0; i < OPTION_COUNT; i++) {
            float y = startY - (i * spacing);
            float x = centerX - buttonWidth / 2;

            // Выбранная опция подсвечена
            if (i == selectedOption) {
                // Рисуем прямоугольник выделения (позже будет UI компонент)
                System.out.println("[> " + options[i] + " <]");
            } else {
                System.out.println("   " + options[i]);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Меню адаптируется к размеру окна
    }

    @Override
    public void hide() {
        System.out.println("MenuScreen скрыт");
    }

    @Override
    public void dispose() {
        System.out.println("MenuScreen очищен");
    }

    @Override
    public String getName() {
        return "MenuScreen";
    }

    /**
     * Обрабатывает ввод меню.
     */
    public void handleMenuInput(MenuInput input) {
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

            default:
                break;
        }
    }

    private void selectOption() {
        if (callback != null) {
            switch (selectedOption) {
                case 0:
                    System.out.println("Новая игра выбрана");
                    callback.onNewGame();
                    break;
                case 1:
                    System.out.println("Загрузить выбрано");
                    callback.onLoadGame();
                    break;
                case 2:
                    System.out.println("Выход выбран");
                    callback.onExit();
                    break;
            }
        }
    }

    public enum MenuInput {
        UP, DOWN, LEFT, RIGHT, SELECT, CANCEL
    }

    // Временный метод для создания простой текстуры (позже будут спрайты)
    private static com.badlogic.gdx.graphics.Texture createFilledTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.05f, 0.05f, 0.05f, 1f);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
