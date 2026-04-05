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
import io.github.example.presentation.events.GameEventDispatcher;
import io.github.example.presentation.input.InputQueue;
import io.github.example.presentation.util.PerformanceMonitor;
import io.github.example.presentation.profiling.GamePerformanceProfiler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Основной экран игры.
 * Отрисовывает уровень, персонажа, врагов, предметы и HUD.
 * Обрабатывает ввод игрока и управляет turn-based gameplay loop.
 * 
 * Turn cycle structure:
 * 1. Input Phase: Collect input and queue actions
 * 2. Update Phase: Process one queued action per turn
 * 3. Render Phase: Update camera and render all layers at 60 FPS
 */
public class GameScreen implements Screen {
    private final GameService gameService;
    private final MainRenderer renderer;
    private final InputHandler inputHandler;
    private final AssetManager assetManager;

    private GameCallback callback;
    private EffectCallback effectCallback;
    private GameEventListener eventListener;
    private GameEventDispatcher eventDispatcher;
    private boolean layersInitialized;
    private EffectsLayerRenderer effectsLayerRenderer;
    private UILayerRenderer uiLayerRenderer;
    private ParticlePool particlePool;
    private List<String> actionLog;

    // Turn-based gameplay loop
    private final InputQueue actionQueue = new InputQueue();
    private final PerformanceMonitor performanceMonitor = new PerformanceMonitor();
    private final GamePerformanceProfiler performanceProfiler = new GamePerformanceProfiler();
    
    private static final float FRAME_TIME_TARGET = 1f / 60f; // 60 FPS target
    private float frameAccumulator = 0f;
    private boolean turnReady = false;

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
        this.eventDispatcher = new GameEventDispatcher();

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
        if (listener != null) {
            eventDispatcher.addEventListener(listener);
        }
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
            actionQueue.enqueueMove(direction, () -> {
                gameService.processPlayerAction(direction);
            });
        }
    }

    private void handleItemUse(int slotIndex) {
        try {
            if (gameService == null || gameService.getSession() == null) {
                Logger.warn("Cannot use item: GameService or session is null");
                return;
            }
            
            // Validate slot index
            if (slotIndex < 0 || slotIndex >= 9) {
                Logger.warn("Invalid item slot: " + slotIndex);
                addActionLog("Invalid slot!");
                return;
            }
            
            actionQueue.enqueueItemUse(slotIndex, () -> {
                try {
                    boolean success = gameService.useItemFromBackpack(slotIndex, false);
                    if (success) {
                        Logger.info("Used item from slot " + slotIndex);
                        addActionLog("Used item");
                    } else {
                        Logger.warn("Could not use item from slot " + slotIndex);
                        addActionLog("Item slot empty!");
                    }
                } catch (IndexOutOfBoundsException e) {
                    Logger.error("Invalid inventory slot " + slotIndex + ": " + e.getMessage());
                    addActionLog("Inventory error!");
                } catch (Exception e) {
                    Logger.error("Error using item: " + e.getMessage());
                    addActionLog("Item use failed!");
                }
            });
        } catch (Exception e) {
            Logger.error("Error queueing item use: " + e.getMessage());
        }
    }

    private void handleWaitAction() {
        if (gameService == null || gameService.getSession() == null) {
            return;
        }
        actionQueue.enqueueWait(() -> {
            try {
                gameService.processPlayerAction(Direction.NONE);
                Logger.info("Player waited");
                addActionLog("Waited");
            } catch (Exception e) {
                Logger.error("Error during wait action: " + e.getMessage());
            }
        });
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
            performanceProfiler.startFrame();
            
            // Validate game state before proceeding
            if (!validateGameState()) {
                if (callback != null) {
                    callback.onGameOver();
                }
                return;
            }
            
            performanceMonitor.startInputPhase();
            performanceProfiler.startInputMeasure();
            
            // 1. INPUT PHASE: Collect keyboard input and queue actions
            processInput();
            
            performanceProfiler.endInputMeasure();
            performanceMonitor.endInputPhase();
            performanceMonitor.startUpdatePhase();
            performanceProfiler.startUpdateMeasure();
            
            // 2. UPDATE PHASE: Process one queued action per turn
            frameAccumulator += delta;
            if (frameAccumulator >= FRAME_TIME_TARGET) {
                frameAccumulator -= FRAME_TIME_TARGET;
                
                // Process next action from queue if available
                if (actionQueue.hasActions()) {
                    InputQueue.GameAction nextAction = actionQueue.getNextAction();
                    if (nextAction != null) {
                        try {
                            nextAction.execute();
                            fireGameEventCallbacks();
                            Logger.debug("Executed: " + nextAction.getDescription());
                        } catch (Exception actionError) {
                            Logger.error("Action execution failed: " + actionError.getMessage());
                            addActionLog("Action failed!");
                        }
                    }
                }
            }
            
            performanceProfiler.endUpdateMeasure();
            performanceMonitor.endUpdatePhase();
            performanceMonitor.startRenderPhase();
            performanceProfiler.startRenderMeasure();
            
            // 3. RENDER PHASE: Update camera and render all layers (60 FPS continuous)
            updateCameraPosition(delta);
            renderer.updateCamera();
            renderer.render(delta);
            
            performanceProfiler.endRenderMeasure();
            performanceMonitor.endRenderPhase();
            performanceMonitor.recordFrameTime();
            performanceProfiler.endFrame();
            
        } catch (Exception e) {
            Logger.error("Critical error in GameScreen.render(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate game state for safety.
     */
    private boolean validateGameState() {
        if (gameService == null || gameService.getSession() == null) {
            Logger.error("GameService or GameSession is null");
            return false;
        }
        
        GameSession session = gameService.getSession();
        if (session.getPlayer() == null) {
            Logger.error("Player is null");
            return false;
        }
        
        // Check if player is alive
        if (!session.getPlayer().isAlive()) {
            Logger.info("Player died - game over");
            return false;
        }
        
        return true;
    }

    /**
     * INPUT PHASE: Polls keyboard input each frame and queues actions.
     * This runs every frame, not just on turn boundaries.
     * Includes validation and error handling for all input types.
     */
    private void processInput() {
        try {
            if (inputHandler == null) {
                Logger.warn("InputHandler is null");
                return;
            }
            
            Direction currentDirection = inputHandler.getCurrentDirection();
            
            // Only queue movement if not already queued this frame
            if (currentDirection != Direction.NONE) {
                if (actionQueue.peekNextAction() == null || 
                    !(actionQueue.peekNextAction() instanceof InputQueue.MoveAction)) {
                    actionQueue.enqueueMove(currentDirection, () -> {
                        try {
                            gameService.processPlayerAction(currentDirection);
                        } catch (Exception e) {
                            Logger.error("Movement failed: " + e.getMessage());
                        }
                    });
                }
            }
        } catch (Exception e) {
            Logger.error("Error in processInput: " + e.getMessage());
        }
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
                
                // Dispatch combat and status events
                if (player.isAlive()) {
                    float playerX = player.getCoordinates().getX();
                    float playerY = player.getCoordinates().getY();
                    
                    // Check player health changes (from combat)
                    for (Enemy enemy : level.getAllEnemies()) {
                        if (enemy.isAlive()) {
                            Coordinates enemyCoords = enemy.getCoordinates();
                            float enemyX = enemyCoords.getX();
                            float enemyY = enemyCoords.getY();
                            
                            // Adjacent to player = combat occurred
                            if (isAdjacent(playerX, playerY, enemyX, enemyY)) {
                                eventDispatcher.onCombatDamage(enemyX, enemyY, 1, false);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isAdjacent(float x1, float y1, float x2, float y2) {
        return Math.abs(x1 - x2) <= 1 && Math.abs(y1 - y2) <= 1 && 
               !(x1 == x2 && y1 == y2);
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
                    // Fire event through dispatcher
                    eventDispatcher.onItemPickedUp(itemAtPos);
                    
                    // Also call listener if set
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
        if (eventDispatcher != null) {
            eventDispatcher.clearListeners();
        }
    }

    @Override
    public void dispose() {
        Logger.debug("GameScreen disposed");
        if (eventDispatcher != null) {
            eventDispatcher.clearListeners();
        }
        
        // Print performance report on dispose
        performanceProfiler.printReport();
        
        renderer.dispose();
    }

    @Override
    public String getName() {
        return "GameScreen";
    }
    
    /**
     * Get performance report for external analysis.
     */
    public String getPerformanceReport() {
        return performanceProfiler.getPerformanceReport();
    }
    
    /**
     * Check if gameplay meets performance targets.
     */
    public boolean meetsPerformanceTargets() {
        return performanceProfiler.meetsPerformanceTargets();
    }
}
