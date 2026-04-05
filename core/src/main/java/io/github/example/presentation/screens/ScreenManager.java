package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.util.Logger;
import java.util.Stack;

/**
 * Управляет переходами между экранами.
 * Поддерживает стек экранов (для паузы поверх GameScreen).
 */
public class ScreenManager {
    private final Stack<Screen> screenStack = new Stack<>();
    private Screen currentScreen;

    /**
     * Переходит на новый экран, заменяя текущий.
     *
     * @param screen новый экран
     */
    public void setScreen(Screen screen) {
        if (currentScreen != null) {
            currentScreen.hide();
            screenStack.clear(); // Очищаем стек при переходе
        }
        currentScreen = screen;
        screenStack.push(screen);
        screen.show();
    }

    /**
     * Добавляет экран поверх текущего (например, пауза поверх игры).
     * Предыдущий экран не скрывается, только не обновляется.
     *
     * @param screen новый экран
     */
    public void pushScreen(Screen screen) {
        screenStack.push(screen);
        currentScreen = screen;
        screen.show();
    }

    /**
     * Возвращает на предыдущий экран из стека.
     */
    public void popScreen() {
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
        }

        if (!screenStack.isEmpty()) {
            screenStack.pop();
        }

        if (!screenStack.isEmpty()) {
            currentScreen = screenStack.peek();
            currentScreen.show();
        } else {
            currentScreen = null;
        }
    }

    /**
     * Обновляет и отрисовывает текущий экран.
     *
     * @param delta время прошедшее с последнего кадра
     * @param batch SpriteBatch для отрисовки
     */
    public void render(float delta, SpriteBatch batch) {
        if (currentScreen != null) {
            currentScreen.render(delta, batch);
        }
    }

    /**
     * Уведомляет текущий экран об изменении размера окна.
     */
    public void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
    }

    /**
     * Получает текущий активный экран.
     */
    public Screen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Получает количество экранов в стеке.
     */
    public int getScreenCount() {
        return screenStack.size();
    }

    /**
     * Проверяет, есть ли экраны в стеке.
     */
    public boolean hasScreens() {
        return !screenStack.isEmpty();
    }

    /**
     * Освобождает все ресурсы всех экранов.
     */
    public void dispose() {
        for (Screen screen : screenStack) {
            try {
                screen.dispose();
            } catch (Exception e) {
                Logger.error("Ошибка при очистке экрана: " + screen.getName(), e);
            }
        }
        screenStack.clear();
        currentScreen = null;
    }
}
