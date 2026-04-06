package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.Logger;

/**
 * Главное меню игры.
 * Кнопки: "Новая игра", "Загрузить", "Выход", "Leaderboard".
 */
public class MenuScreen implements Screen {
    private int selectedOption = 0; // 0 = Новая игра, 1 = Загрузить, 2 = Leaderboard, 3 = Выход
    private static final int OPTION_COUNT = 4;
    private MenuCallback callback;
    private BitmapFont font;
    private static final String[] OPTIONS = {
        "New Game",
        "Load Game", 
        "Leaderboard",
        "Exit"
    };

    public interface MenuCallback {
        void onNewGame();
        void onLoadGame();
        void onLeaderboard();
        void onExit();
    }

    public MenuScreen(MenuCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        Logger.info("MenuScreen показан");
        selectedOption = 0;
        
        // Создаём шрифт для меню
        if (font == null) {
            font = new BitmapFont();
            font.setColor(Color.WHITE);
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        batch.begin();
        
        // Создаем или переиспользуем шрифт
        if (font == null) {
            font = new BitmapFont();
        }
        
        // Выводим заголовок
        int startX = 100;
        int startY = Constants.SCREEN_HEIGHT - 100;
        int lineHeight = 60;
        
        // Рисуем фон меню (черный прямоугольник)
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(400, 300, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.8f);
        pixmap.fill();
        com.badlogic.gdx.graphics.Texture bgTexture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
        
        batch.draw(bgTexture, startX - 10, startY - 280, 420, 300);
        bgTexture.dispose();
        
        // Выводим заголовок
        font.setColor(Color.YELLOW);
        font.draw(batch, "ROGUELIKE", startX, startY);
        
        // Выводим опции
        font.setColor(Color.WHITE);
        for (int i = 0; i < OPTIONS.length; i++) {
            String text = (i == selectedOption ? "> " : "  ") + OPTIONS[i];
            
            if (i == selectedOption) {
                font.setColor(Color.CYAN);
            } else {
                font.setColor(Color.WHITE);
            }
            
            font.draw(batch, text, startX, startY - 50 - (i * lineHeight));
        }
        
        // Выводим информацию о контролах
        font.setColor(Color.GRAY);
        font.draw(batch, "UP/DOWN - Navigate", startX, 50);
        font.draw(batch, "ENTER - Select", startX, 25);
        
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Меню адаптируется к размеру окна
    }

    @Override
    public void hide() {
        Logger.debug("MenuScreen скрыт");
    }

    @Override
    public void dispose() {
        Logger.debug("MenuScreen очищен");
        if (font != null) {
            font.dispose();
            font = null;
        }
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
                    Logger.info("Новая игра выбрана");
                    callback.onNewGame();
                    break;
                case 1:
                    Logger.info("Загрузить выбрано");
                    callback.onLoadGame();
                    break;
                case 2:
                    Logger.info("Leaderboard выбран");
                    callback.onLeaderboard();
                    break;
                case 3:
                    Logger.info("Выход выбран");
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
