package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.util.Logger;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

/**
 * Управляет переходами между экранами.
 * Поддерживает стек экранов (для паузы поверх GameScreen).
 * Поддерживает плавные переходы с анимациями затемнения.
 */
public class ScreenManager {
    private final Stack<Screen> screenStack = new Stack<>();
    private Screen currentScreen;
    private ScreenTransition activeTransition;
    private Screen nextScreen;
    private boolean isTransitioning;
    private final Map<String, GameStateSnapshot> stateSnapshots = new HashMap<>();

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
     * Выполняет плавный переход на новый экран с анимацией затемнения.
     * Использует переход FadeOut/FadeIn по умолчанию с продолжительностью 0.5 секунды.
     *
     * @param screen новый экран
     */
    public void transitionToScreen(Screen screen) {
        transitionToScreen(screen, new CrossfadeTransition(0.5f));
    }

    /**
     * Выполняет плавный переход на новый экран с заданной продолжительностью.
     * Использует переход Crossfade.
     *
     * @param screen новый экран
     * @param duration продолжительность переход в секундах
     */
    public void transitionToScreen(Screen screen, float duration) {
        transitionToScreen(screen, new CrossfadeTransition(duration));
    }

    /**
     * Выполняет плавный переход на новый экран с указанной анимацией.
     * Сохраняет состояние игры перед переходом для восстановления после.
     *
     * @param screen новый экран
     * @param transition переход для использования
     */
    public void transitionToScreen(Screen screen, ScreenTransition transition) {
        if (isTransitioning) {
            Logger.warn("Переход уже выполняется, пропускаем");
            return;
        }

        // Сохраняем текущее состояние если нужно
        if (currentScreen != null && currentScreen instanceof GameScreen) {
            stateSnapshots.put("game_state", new GameStateSnapshot(null, currentScreen.getName()));
        }

        nextScreen = screen;
        activeTransition = transition;
        isTransitioning = true;
        Logger.debug("Начало перехода на экран: " + screen.getName());
    }

    /**
     * Проверяет, выполняется ли переход в данный момент.
     */
    public boolean isTransitioning() {
        return isTransitioning;
    }

    /**
     * Получает активный переход, если он выполняется.
     */
    public ScreenTransition getActiveTransition() {
        return activeTransition;
    }

    /**
     * Сохраняет снимок состояния игры для заданного экрана.
     */
    public void saveGameState(String screenName, GameStateSnapshot snapshot) {
        stateSnapshots.put(screenName, snapshot);
    }

    /**
     * Восстанавливает снимок состояния игры для заданного экрана.
     */
    public GameStateSnapshot getGameState(String screenName) {
        return stateSnapshots.get(screenName);
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

        // Обновляем переход если он активен
        if (isTransitioning && activeTransition != null) {
            activeTransition.update(delta);

            // Когда переход достигает середины, переключаемся на новый экран
            if (activeTransition.getProgress() >= 0.5f && nextScreen != null && currentScreen != nextScreen) {
                if (currentScreen != null) {
                    currentScreen.hide();
                    screenStack.clear();
                }
                currentScreen = nextScreen;
                screenStack.push(nextScreen);
                nextScreen.show();
                nextScreen = null;
            }

            // Когда переход завершен, очищаем его
            if (activeTransition.isCompleted()) {
                isTransitioning = false;
                activeTransition = null;
                Logger.debug("Переход завершен");
            }
        }
    }

    /**
     * Отрисовывает активный переход поверх экрана.
     * Вызывается из MainRenderer после отрисовки всех слоев.
     *
     * @param batch SpriteBatch для отрисовки
     * @param screenWidth ширина экрана
     * @param screenHeight высота экрана
     */
    public void renderTransition(SpriteBatch batch, int screenWidth, int screenHeight) {
        if (isTransitioning && activeTransition != null) {
            activeTransition.render(batch, screenWidth, screenHeight);
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
        if (activeTransition != null) {
            activeTransition = null;
        }
        stateSnapshots.clear();
    }
}
