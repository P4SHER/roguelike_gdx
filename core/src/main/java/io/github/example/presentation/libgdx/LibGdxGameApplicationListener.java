package io.github.example.presentation.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.PresentationLayer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.screens.MenuScreen;

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
        System.out.println("Приложение запускается...");

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
                System.out.println("Начало новой игры");
                // TODO: Создать и запустить новую игру
            }

            @Override
            public void onLoadGame() {
                System.out.println("Загрузка игры");
                // TODO: Загрузить сохраненную игру
            }

            @Override
            public void onExit() {
                System.out.println("Выход из игры");
                Gdx.app.exit();
            }
        });
        presentationLayer.setInitialScreen(menuScreen);

        System.out.println("Приложение готово!");
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
        System.out.println("Окно изменило размер: " + width + "x" + height);
        presentationLayer.resize(width, height);
    }

    @Override
    public void pause() {
        System.out.println("Приложение паузировано");
    }

    @Override
    public void resume() {
        System.out.println("Приложение возобновлено");
    }

    @Override
    public void dispose() {
        System.out.println("Приложение завершается...");
        presentationLayer.dispose();
        batch.dispose();
        System.out.println("Приложение закрыто");
    }
}
