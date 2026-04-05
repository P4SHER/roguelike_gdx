package io.github.example.presentation.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.PresentationLayer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.screens.MenuScreen;
import io.github.example.presentation.util.Logger;

/**
 * Главное приложение LibGDX для игры Roguelike.
 * Реализует ApplicationListener интерфейс и управляет жизненным циклом приложения.
 */
public class LibGdxGameApplicationListener implements ApplicationListener {
    private SpriteBatch batch;
    private PresentationLayer presentationLayer;
    private InputHandler inputHandler;

    @Override
    public void create() {
        Logger.info("Приложение запускается...");

        // Инициализируем основные компоненты
        batch = new SpriteBatch();
        inputHandler = new InputHandler();
        presentationLayer = new PresentationLayer(1920, 1080);

        // Устанавливаем обработчик ввода
        Gdx.input.setInputProcessor(inputHandler);

        // Создаем и устанавливаем главное меню как начальный экран
        MenuScreen menuScreen = new MenuScreen(new MenuScreen.MenuCallback() {
            @Override
            public void onNewGame() {
                Logger.info("Начало новой игры");
                // TODO: Создать и запустить новую игру
            }

            @Override
            public void onLoadGame() {
                Logger.info("Загрузка игры");
                // TODO: Загрузить сохраненную игру
            }

            @Override
            public void onLeaderboard() {
                Logger.info("Открытие таблицы лидеров");
                // TODO: Открыть экран таблицы лидеров
            }

            @Override
            public void onExit() {
                Logger.info("Выход из игры");
                Gdx.app.exit();
            }
        });
        presentationLayer.setInitialScreen(menuScreen);

        Logger.info("Приложение готово!");
    }

    @Override
    public void render() {
        // Очищаем экран черным цветом
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Вычисляем delta time
        float delta = Gdx.graphics.getDeltaTime();

        // Обновляем и отрисовываем presentation слой
        presentationLayer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Logger.debug("Окно изменило размер: " + width + "x" + height);
        presentationLayer.resize(width, height);
    }

    @Override
    public void pause() {
        Logger.debug("Приложение паузировано");
    }

    @Override
    public void resume() {
        Logger.debug("Приложение возобновлено");
    }

    @Override
    public void dispose() {
        Logger.info("Приложение завершается...");
        presentationLayer.dispose();
        batch.dispose();
        Logger.info("Приложение закрыто");
    }
}
