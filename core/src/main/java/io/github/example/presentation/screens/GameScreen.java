package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.renderer.MainRenderer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.renderer.layers.*;
import io.github.example.domain.service.Direction;
import io.github.example.domain.service.GameService;
import io.github.example.domain.service.GameSession;
import io.github.example.domain.level.Level;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Enemy;
import java.util.ArrayList;
import java.util.List;

/**
 * Основной экран игры.
 * Отрисовывает уровень, персонажа, врагов, предметы и HUD.
 * Обрабатывает ввод игрока.
 */
public class GameScreen implements Screen {
    private final GameService gameService;
    private final MainRenderer renderer;
    private final InputHandler inputHandler;
    private final AssetManager assetManager;

    private GameCallback callback;
    private boolean layersInitialized;

    public interface GameCallback {
        void onPause();
        void onGameOver();
    }

    public GameScreen(GameService gameService, MainRenderer renderer, InputHandler inputHandler, AssetManager assetManager) {
        this.gameService = gameService;
        this.renderer = renderer;
        this.inputHandler = inputHandler;
        this.assetManager = assetManager;
        this.layersInitialized = false;

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
            setupLayers();
        }
    }

    /**
     * Устанавливает все слои рендеринга.
     * Вызывается один раз при show().
     */
    private void setupLayers() {
        if (layersInitialized) {
            return;
        }

        if (gameService == null) {
            System.err.println("GameService не инициализирована");
            return;
        }

        GameSession gameSession = gameService.getSession();
        if (gameSession == null) {
            System.err.println("GameSession не инициализирована");
            return;
        }

        Level level = gameSession.getCurrentLevel();
        Player player = gameSession.getPlayer();
        List<Enemy> enemies = level != null ? level.getAllEnemies() : new ArrayList<>();

        if (level == null || player == null) {
            System.err.println("Level или Player не инициализированы");
            return;
        }

        // Z-order слоев: Tile (0) → Actor (1) → Item (2) → Fog (3)
        renderer.addLayer(new TileLayerRenderer(level, assetManager));
        renderer.addLayer(new ActorLayerRenderer(player, enemies, assetManager));
        renderer.addLayer(new ItemLayerRenderer(level, assetManager));
        renderer.addLayer(new FogLayerRenderer(level, player));

        layersInitialized = true;
        System.out.println("Слои рендеринга инициализированы");
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
        if (direction != Direction.NONE) {
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
