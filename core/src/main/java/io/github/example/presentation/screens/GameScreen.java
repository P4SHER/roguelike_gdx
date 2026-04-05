package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.renderer.MainRenderer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.input.Direction;
import io.github.example.domain.service.GameService;

/**
 * Основной экран игры.
 * Отрисовывает уровень, персонажа, врагов, предметы и HUD.
 * Обрабатывает ввод игрока.
 */
public class GameScreen implements Screen {
    private final GameService gameService;
    private final MainRenderer renderer;
    private final InputHandler inputHandler;

    private GameCallback callback;

    public interface GameCallback {
        void onPause();
        void onGameOver();
    }

    public GameScreen(GameService gameService, MainRenderer renderer, InputHandler inputHandler) {
        this.gameService = gameService;
        this.renderer = renderer;
        this.inputHandler = inputHandler;

        // Устанавливаем слушателя ввода для этого экрана
        setupInputListener();
    }

    public void setCallback(GameCallback callback) {
        this.callback = callback;
    }

    private void setupInputListener() {
        inputHandler.setInputListener(new InputHandler.InputListener() {
            @Override
            public void onMove(Direction direction) {
                handlePlayerMove(direction);
            }

            @Override
            public void onAction(InputHandler.Action action) {
                handleAction(action);
            }

            @Override
            public void onMenuInput(InputHandler.MenuInput input) {
                handleMenuInput(input);
            }
        });
    }

    @Override
    public void show() {
        System.out.println("GameScreen показан");
        // Инициализируем уровень, персонажа и т.д.
        if (gameService != null) {
            System.out.println("GameService инициализирован");
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Обновляем камеру для отслеживания персонажа
        if (gameService != null) {
            // renderer.setCameraTarget(player.getX(), player.getY());
        }

        // Отрисовываем игровой мир через renderer
        renderer.render(delta);

        // Обновляем состояние игры
        updateGame(delta);
    }

    private void updateGame(float delta) {
        if (gameService == null) {
            return;
        }

        // Получаем текущее направление из InputHandler
        Direction direction = inputHandler.getCurrentDirection();
        if (direction.isMovement()) {
            // Отправляем команду движения в domain слой
            System.out.println("Движение: " + direction);
            // gameService.movePlayer(direction);
        }
    }

    private void handlePlayerMove(Direction direction) {
        // Это вызывается только для discrete движения (не continuous)
        // В нашем случае движение обрабатывается в updateGame()
    }

    private void handleAction(InputHandler.Action action) {
        if (gameService == null) {
            return;
        }

        switch (action) {
            case ATTACK:
                System.out.println("Атака!");
                // gameService.attackNearestEnemy();
                break;

            case INTERACT:
                System.out.println("Взаимодействие!");
                // gameService.interactWithNearby();
                break;

            default:
                break;
        }
    }

    private void handleMenuInput(InputHandler.MenuInput input) {
        switch (input) {
            case MENU_TOGGLE:
                if (callback != null) {
                    callback.onPause();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void hide() {
        System.out.println("GameScreen скрыт");
    }

    @Override
    public void dispose() {
        System.out.println("GameScreen очищен");
        renderer.dispose();
    }

    @Override
    public String getName() {
        return "GameScreen";
    }
}
