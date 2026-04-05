package io.github.example.presentation.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Интерфейс для рендереров отдельных слоев.
 */
public interface LayerRenderer {
    /**
     * Инициализирует рендерер.
     */
    void init();

    /**
     * Отрисовывает слой.
     *
     * @param batch SpriteBatch для отрисовки
     * @param camera камера для проецирования
     * @param delta время прошедшее с последнего кадра
     */
    void render(SpriteBatch batch, OrthographicCamera camera, float delta);

    /**
     * Освобождает ресурсы рендерера.
     */
    void dispose();

    /**
     * Возвращает название слоя.
     */
    String getName();
}
