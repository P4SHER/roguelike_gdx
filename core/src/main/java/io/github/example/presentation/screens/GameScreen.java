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
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.Item;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.events.GameEventListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Основной экран игры.
 * Отрисовывает уровень, персонажа, врагов, предметы и HUD.
 * Обрабатывает ввод игрока и управляет turn-based gameplay loop.
 */
public class GameScreen implements Screen {
    private final GameService gameService;
    private final MainRenderer renderer;
    private final InputHandler inputHandler;
    private final AssetManager assetManager;

    private GameCallback callback;
    private EffectCallback effectCallback;
    private GameEventListener eventListener;
    private boolean layersInitialized;
    private EffectsLayerRenderer effectsLayerRenderer;
    private UILayerRenderer uiLayerRenderer;
    private ParticlePool particlePool;
    private List<String> actionLog;

    // Turn-based gameplay loop
    private final Queue<Direction> inputQueue = new LinkedList<>();
    private boolean turnProcessed = false;

    public interface GameCallback {
        void onPause();
        void onGameOver();
        void onToggleInventory();
        void onQuitToMenu();
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

        setupInputListener();
    }

    public void setCallback(GameCallback callback) {
        this.callback = callback;
    }

    public void setEffectCallback(EffectCallback effectCallback) {
        this.effectCallback = effectCallback;
    }

    public void setEventListener(GameEventListener listener) {
        this.eventListener = listener;
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

            @Override
            public void onItemUse(int slotIndex) {
                handleItemUse(slotIndex);
            }

            @Override
            public void onWait() {
                handleWaitAction();
            }

            @Override
            public void onToggleInventory() {
                handleToggleInventory();
            }

            @Override
            public void onTogglePause() {
                handleTogglePause();
            }

            @Override
            public void onQuitToMenu() {
                handleQuitToMenu();
            }
        });
    }

    private void handlePlayerMove(Direction direction) {
        if (direction != Direction.NONE) {
            inputQueue.offer(direction);
        }
    }

    private void handleItemUse(int slotIndex) {
        if (gameService == null || gameService.getSession() == null) {
            return;
        }
        try {
            boolean success = gameService.useItemFromBackpack(slotIndex, false);
            if (success) {
                Logger.info("Used item from slot " + slotIndex);
                addActionLog("Used item");
            } else {
                Logger.warn("Could not use item from slot " + slotIndex);
            }
        } catch (Exception e) {
            Logger.error("Error using item: " + e.getMessage());
        }
    }

    private void handleWaitAction() {
        if (gameService == null || gameService.getSession() == null) {
            return;
        }
        try {
            // Wait is a no-op action for the player, but enemies still act
            gameService.processPlayerAction(Direction.NONE);
            Logger.info("Player waited");
            addActionLog("Waited");
        } catch (Exception e) {
            Logger.error("Error during wait action: " + e.getMessage());
        }
    }

    private void handleToggleInventory() {
        if (callback != null) {
            callback.onToggleInventory();
        }
    }

    private void handleTogglePause() {
        if (callback != null) {
            callback.onPause();
        }
    }

    private void handleQuitToMenu() {
        if (callback != null) {
            callback.onQuitToMenu();
        }
    }

    private void addActionLog(String action) {
        actionLog.add(action);
        if (actionLog.size() > 10) {
            actionLog.remove(0);
        }
    }

    @Override
    public void show() {
        Logger.info("GameScreen shown");
        if (gameService != null) {
            Logger.debug("GameService initialized");
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
            Logger.error("GameService not initialized");
            return;
        }

        GameSession gameSession = gameService.getSession();
        if (gameSession == null) {
            Logger.error("GameSession not initialized");
            return;
        }

        Level level = gameSession.getCurrentLevel();
        Player player = gameSession.getPlayer();
        List<Enemy> enemies = level != null ? level.getAllEnemies() : new ArrayList<>();

        if (level == null || player == null) {
            Logger.error("Level or Player not initialized");
            return;
        }

        renderer.addLayer(new TileLayerRenderer(level, assetManager));
        renderer.addLayer(new ActorLayerRenderer(player, enemies, assetManager));
        renderer.addLayer(new ItemLayerRenderer(level, assetManager));
        renderer.addLayer(new FogLayerRenderer(level, player));
        
        effectsLayerRenderer = new EffectsLayerRenderer();
        renderer.addLayer(effectsLayerRenderer);
        
        particlePool = new ParticlePool(256);
        
        uiLayerRenderer = new UILayerRenderer(player, actionLog);
        renderer.addLayer(uiLayerRenderer);
        
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
        Logger.info("Rendering layers initialized (6 layers)");
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        try {
            // 1. Process input for this frame
            handleInputQueue();
            
            // 2. Update game logic (turn-based)
            if (shouldProcessTurn()) {
                Direction direction = getNextDirection();
                if (direction != Direction.NONE) {
                    gameService.processPlayerAction(direction);
                    fireGameEventCallbacks();
                } else if (!inputQueue.isEmpty()) {
                    // Consume queued direction
                    inputQueue.poll();
                }
                turnProcessed = false;
            }
            
            // 3. Update camera to follow player
            updateCameraPosition(delta);
            
            // 4. Update camera and render all layers
            renderer.updateCamera();
            renderer.render(delta);
        } catch (Exception e) {
            Logger.error("Error in GameScreen.render(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleInputQueue() {
        if (!inputQueue.isEmpty() && !turnProcessed) {
            turnProcessed = true;
        }
    }

    private boolean shouldProcessTurn() {
        return turnProcessed;
    }

    private Direction getNextDirection() {
        Direction currentDir = inputHandler.getCurrentDirection();
        if (currentDir != Direction.NONE && !inputQueue.isEmpty()) {
            inputQueue.poll();
        }
        return currentDir;
    }

    private void updateCameraPosition(float delta) {
        Player player = null;
        if (gameService != null && gameService.getSession() != null) {
            player = gameService.getSession().getPlayer();
        }
        
        if (player != null) {
            renderer.followPlayer(player);
        }
    }

    private void fireGameEventCallbacks() {
        if (effectCallback == null) {
            return;
        }

        GameSession session = gameService.getSession();
        if (session != null) {
            Player player = session.getPlayer();
            Level level = session.getCurrentLevel();
            
            if (player != null && level != null) {
                checkAndPickupItems(player, level);
            }
        }
    }

    /**
     * Automatically picks up items at player's current position.
     */
    private void checkAndPickupItems(Player player, Level level) {
        if (player == null || level == null) {
            return;
        }

        Coordinates playerPos = player.getCoordinates();
        Item itemAtPos = level.getItems().get(playerPos);
        
        if (itemAtPos != null) {
            try {
                if (player.getBackpack().addItem(itemAtPos.copyObject())) {
                    if (eventListener != null) {
                        eventListener.onItemPickedUp(itemAtPos);
                    }
                    
                    level.removeItem(playerPos);
                    Logger.info("Picked up: " + itemAtPos.getName());
                    addActionLog("Picked up: " + itemAtPos.getName());
                } else {
                    Logger.warn("Inventory full, could not pick up: " + itemAtPos.getName());
                }
            } catch (Exception e) {
                Logger.error("Error picking up item: " + e.getMessage());
            }
        }
    }

    private void handleAction(InputHandler.Action action) {
        if (gameService == null) {
            return;
        }

        switch (action) {
            case ATTACK:
                Logger.info("Attack!");
                break;

            case INTERACT:
                Logger.info("Interact!");
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
        Logger.debug("GameScreen hidden");
    }

    @Override
    public void dispose() {
        Logger.debug("GameScreen disposed");
        renderer.dispose();
    }

    @Override
    public String getName() {
        return "GameScreen";
    }
}
