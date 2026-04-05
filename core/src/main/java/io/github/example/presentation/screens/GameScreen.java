package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.renderer.MainRenderer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.renderer.layers.*;
import io.github.example.presentation.util.Logger;
import io.github.example.presentation.effects.EffectFactory;
import io.github.example.presentation.effects.ParticlePool;
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
    private EffectCallback effectCallback;
    private boolean layersInitialized;
    private EffectsLayerRenderer effectsLayerRenderer;
    private UILayerRenderer uiLayerRenderer;
    private ParticlePool particlePool;
    private List<String> actionLog;

    public interface GameCallback {
        void onPause();
        void onGameOver();
    }

    public interface EffectCallback {
        void onPlayerAttack(float x, float y, int damage);
        void onEnemyDamage(float x, float y, int damage);
        void onPlayerHeal(float x, float y, int heal);
        void onPlayerLevelUp(float x, float y);
    }

    public GameScreen(GameService gameService, MainRenderer renderer, InputHandler inputHandler, AssetManager assetManager) {
        this.gameService = gameService;
        this.renderer = renderer;
        this.inputHandler = inputHandler;
        this.assetManager = assetManager;
        this.layersInitialized = false;
        this.actionLog = new ArrayList<>();

        // Устанавливаем слушателя ввода для этого экрана
        setupInputListener();
    }

    public void setCallback(GameCallback callback) {
        this.callback = callback;
    }

    public void setEffectCallback(EffectCallback effectCallback) {
        this.effectCallback = effectCallback;
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
        Logger.info("GameScreen показан");
        // Инициализируем уровень, персонажа и т.д.
        if (gameService != null) {
            Logger.debug("GameService инициализирован");
            setupLayers();
        }
    }

    /**
     * Устанавливает все слои рендеринга.
     * Вызывается один раз при show().
     * Z-order: Tile (0) → Actor (1) → Item (2) → Fog (3) → Effects (4) → UI (5)
     */
    private void setupLayers() {
        if (layersInitialized) {
            return;
        }

        if (gameService == null) {
            Logger.error("GameService не инициализирована");
            return;
        }

        GameSession gameSession = gameService.getSession();
        if (gameSession == null) {
            Logger.error("GameSession не инициализирована");
            return;
        }

        Level level = gameSession.getCurrentLevel();
        Player player = gameSession.getPlayer();
        List<Enemy> enemies = level != null ? level.getAllEnemies() : new ArrayList<>();

        if (level == null || player == null) {
            Logger.error("Level или Player не инициализированы");
            return;
        }

        // Z-order слоев: Tile (0) → Actor (1) → Item (2) → Fog (3) → Effects (4) → UI (5)
        renderer.addLayer(new TileLayerRenderer(level, assetManager));
        renderer.addLayer(new ActorLayerRenderer(player, enemies, assetManager));
        renderer.addLayer(new ItemLayerRenderer(level, assetManager));
        renderer.addLayer(new FogLayerRenderer(level, player));
        
        // Add Effects and UI layers
        effectsLayerRenderer = new EffectsLayerRenderer();
        renderer.addLayer(effectsLayerRenderer);
        
        // Initialize particle system (EffectFactory uses static methods)
        particlePool = new ParticlePool(256);
        
        uiLayerRenderer = new UILayerRenderer(player, actionLog);
        renderer.addLayer(uiLayerRenderer);
        
        // Create default EffectCallback implementation
        if (effectCallback == null) {
            effectCallback = new EffectCallback() {
                @Override
                public void onPlayerAttack(float x, float y, int damage) {
                    EffectFactory.createWeaponHit(particlePool, x, y, assetManager);
                }

                @Override
                public void onEnemyDamage(float x, float y, int damage) {
                    EffectFactory.createDamageNumber(particlePool, x, y, damage, assetManager);
                }

                @Override
                public void onPlayerHeal(float x, float y, int heal) {
                    EffectFactory.createHealNumber(particlePool, x, y, heal, assetManager);
                }

                @Override
                public void onPlayerLevelUp(float x, float y) {
                    EffectFactory.createLevelUp(particlePool, x, y, assetManager);
                }
            };
        }

        layersInitialized = true;
        Logger.info("Слои рендеринга инициализированы (6 layers)");
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
            Logger.debug("Движение: " + direction);
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
                Logger.info("Атака!");
                // gameService.attackNearestEnemy();
                break;

            case INTERACT:
                Logger.info("Взаимодействие!");
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
        Logger.debug("GameScreen скрыт");
    }

    @Override
    public void dispose() {
        Logger.debug("GameScreen очищен");
        renderer.dispose();
    }

    @Override
    public String getName() {
        return "GameScreen";
    }
}
