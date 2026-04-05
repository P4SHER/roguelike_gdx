package io.github.example.presentation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.renderer.MainRenderer;
import io.github.example.presentation.screens.Screen;
import io.github.example.presentation.screens.ScreenManager;

/**
 * Главная входная точка для presentation слоя.
 * Координирует взаимодействие между всеми компонентами presentation:
 * - ScreenManager (управление экранами)
 * - MainRenderer (отрисовка)
 * - AssetManager (управление ресурсами)
 *
 * Используется из FirstScreen (LibGDX Application).
 */
public class PresentationLayer {
    private final ScreenManager screenManager;
    private final MainRenderer mainRenderer;
    private final AssetManager assetManager;
    private final SpriteBatch batch;

    // Размеры уровня (передаются при инициализации)
    private final float levelWidth;
    private final float levelHeight;

    public PresentationLayer(float levelWidth, float levelHeight) {
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        // Инициализируем основные компоненты
        this.batch = new SpriteBatch();
        this.assetManager = new AssetManager();
        this.mainRenderer = new MainRenderer(levelWidth, levelHeight);
        this.screenManager = new ScreenManager();

        System.out.println("PresentationLayer инициализирован");
        System.out.println("  Размер уровня: " + levelWidth + "x" + levelHeight);
        System.out.println("  Компоненты готовы к использованию");
    }

    /**
     * Устанавливает начальный экран.
     */
    public void setInitialScreen(Screen screen) {
        screenManager.setScreen(screen);
    }

    /**
     * Переходит на новый экран.
     */
    public void setScreen(Screen screen) {
        screenManager.setScreen(screen);
    }

    /**
     * Добавляет экран поверх текущего (например, пауза).
     */
    public void pushScreen(Screen screen) {
        screenManager.pushScreen(screen);
    }

    /**
     * Возвращает на предыдущий экран.
     */
    public void popScreen() {
        screenManager.popScreen();
    }

    /**
     * Обновляет и отрисовывает текущий экран.
     * Вызывается из FirstScreen.render().
     */
    public void render(float delta) {
        // Обновляем камеру
        mainRenderer.updateCamera();

        // Отрисовываем текущий экран
        screenManager.render(delta, batch);
    }

    /**
     * Уведомляет об изменении размера окна.
     */
    public void resize(int width, int height) {
        screenManager.resize(width, height);
        mainRenderer.resize(width, height);
    }

    /**
     * Получает текущий активный экран.
     */
    public Screen getCurrentScreen() {
        return screenManager.getCurrentScreen();
    }

    /**
     * Получает ScreenManager для получения дополнительного контроля.
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    /**
     * Получает MainRenderer для прямого доступа к рендерингу.
     */
    public MainRenderer getMainRenderer() {
        return mainRenderer;
    }

    /**
     * Получает AssetManager для загрузки спрайтов.
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * Получает SpriteBatch для отрисовки.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Получает информацию для отладки.
     */
    public String getDebugInfo() {
        return String.format(
            "PresentationLayer Debug:\n" +
            "  Текущий экран: %s\n" +
            "  Экранов в стеке: %d\n" +
            "  %s\n" +
            "  %s",
            getCurrentScreen() != null ? getCurrentScreen().getName() : "None",
            screenManager.getScreenCount(),
            mainRenderer.getDebugInfo(),
            assetManager.getCacheInfo()
        );
    }

    /**
     * Очищает все ресурсы при завершении работы.
     */
    public void dispose() {
        System.out.println("PresentationLayer очищается...");
        screenManager.dispose();
        mainRenderer.dispose();
        assetManager.dispose();
        batch.dispose();
        System.out.println("PresentationLayer очищен");
    }
}
