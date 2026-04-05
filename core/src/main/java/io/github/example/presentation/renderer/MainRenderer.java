package io.github.example.presentation.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.github.example.presentation.camera.CameraController;
import io.github.example.presentation.util.ColorScheme;
import io.github.example.presentation.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Главный рендерер, координирующий отрисовку всех слоев.
 * Управляет камерой, SpriteBatch и всеми LayerRenderers.
 */
public class MainRenderer {
    private final SpriteBatch batch;
    private final CameraController cameraController;
    private final List<LayerRenderer> layers = new ArrayList<>();

    public MainRenderer(float levelWidth, float levelHeight) {
        this.batch = new SpriteBatch();
        this.cameraController = new CameraController(levelWidth, levelHeight);
    }

    /**
     * Добавляет слой рендеринга в правильном порядке (z-order).
     * Слои добавляются в порядке отрисовки.
     *
     * @param layer слой для добавления
     */
    public void addLayer(LayerRenderer layer) {
        layers.add(layer);
        layer.init();
        Logger.debug("Добавлен слой рендеринга: " + layer.getName());
    }

    /**
     * Устанавливает целевую позицию для камеры (обычно персонаж).
     */
    public void setCameraTarget(float x, float y) {
        cameraController.setTarget(x, y);
    }

    /**
     * Обновляет камеру. Вызывается перед render().
     */
    public void updateCamera() {
        cameraController.update();
    }

    /**
     * Отрисовывает все слои.
     *
     * @param delta время прошедшее с последнего кадра
     */
    public void render(float delta) {
        // Очищаем экран
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Устанавливаем матрицу проекции камеры
        OrthographicCamera camera = cameraController.getCamera();
        batch.setProjectionMatrix(camera.combined);

        // Начинаем batch
        batch.begin();

        // Отрисовываем все слои в порядке
        for (LayerRenderer layer : layers) {
            layer.render(batch, camera, delta);
        }

        // Завершаем batch
        batch.end();
    }

    /**
     * Получает контроллер камеры для доступа к методам камеры.
     */
    public CameraController getCameraController() {
        return cameraController;
    }

    /**
     * Получает видимые границы камеры для оптимизации (culling).
     */
    public com.badlogic.gdx.math.Rectangle getVisibleBounds() {
        return cameraController.getVisibleBounds();
    }

    /**
     * Изменяет размер viewport при изменении окна.
     */
    public void resize(int width, int height) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    /**
     * Очищает все ресурсы.
     */
    public void dispose() {
        for (LayerRenderer layer : layers) {
            try {
                layer.dispose();
            } catch (Exception e) {
                Logger.error("Ошибка при очистке слоя: " + layer.getName(), e);
            }
        }
        cameraController.dispose();
        batch.dispose();
    }

    /**
     * Получает информацию о рендерере для отладки.
     */
    public String getDebugInfo() {
        return String.format("Слои рендеринга: %d, Камера: (%.0f, %.0f), Зум: %.2f",
            layers.size(),
            cameraController.getCamera().position.x,
            cameraController.getCamera().position.y,
            cameraController.getZoom()
        );
    }
}
