package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Базовый интерфейс для всех экранов (сцен) в игре.
 */
public interface Screen {
    /**
     * Вызывается при переходе на этот экран.
     */
    void show();

    /**
     * Вызывается каждый кадр для обновления и отрисовки экрана.
     *
     * @param delta время прошедшее с последнего кадра в секундах
     * @param batch SpriteBatch для отрисовки
     */
    void render(float delta, SpriteBatch batch);

    /**
     * Вызывается при изменении размера окна.
     *
     * @param width новая ширина окна
     * @param height новая высота окна
     */
    void resize(int width, int height);

    /**
     * Вызывается при скрытии экрана (переход на другой экран).
     */
    void hide();

    /**
     * Вызывается для освобождения ресурсов экрана.
     */
    void dispose();

    /**
     * Возвращает название экрана для отладки.
     */
    String getName();
}
