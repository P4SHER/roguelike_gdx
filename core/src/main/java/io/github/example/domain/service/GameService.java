package io.github.example.domain.service;

import io.github.example.datalayer.SaveService;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.*;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.LevelGeneration;
import io.github.example.domain.unittest.Logger;

/**
 * Управление ссесией, ходами
 */
public class GameService {
    private GameSession session;
    private final GameSessionGeneration sessionGeneration;

    // Класы, где реализованы методы для игры
    private final MovementService movementService;
    private final CombatService combatService;
    private final EnemyAiService enemyAiService;
    private final InventoryService inventoryService;
    private final SaveService saveService;


    public GameService() {
        this.sessionGeneration = new GameSessionGeneration();
        this.combatService = new CombatService();
        this.movementService = new MovementService();
        this.enemyAiService = new EnemyAiService();
        this.inventoryService = new InventoryService();
        this.saveService = new SaveService();
    }

    public void startNewGame() {
        this.session = sessionGeneration.startNewGame();
    }

    public void transitionToNextLevel() {
        Logger.info("Начало перехода на новый уровень");
        LevelGeneration levelGeneration = new LevelGeneration();
        session.setCurrentLevel(levelGeneration.generateLevel(session));
        session.setState(GameState.PLAYING);
        saveService.saveGame(session);
        Logger.info("Transition to next level");
    }

    /**
     * Обрабатывает действие игрока и обрабатывает ход всех противников
     * @param direction - направление движения игрока(LEFT, UP, RIGHT, DOWN)
     */
    public void processPlayerAction(Direction direction) {
        if (session.getState() != GameState.PLAYING) {
            return;
        }
        MoveResult actionSuccess = movementService.movePlayer(session, direction, combatService);
        if (actionSuccess == MoveResult.SUCCESS) session.getRecord().incrementMoves();

        if (session.getState() == GameState.PLAYING) {
            // Ход всех врагов
            enemyAiService.processAllEnemies(session, combatService, movementService);
            session.getRecord().incrementMoves();
        }

    }

    /**
     * Логика для использования предмета который храниться в рюкзаке
     * @param indexInBackpack - порядковый номер позиции в рюкзаке 0-9
     */
    public boolean useItemFromBackpack(int indexInBackpack, boolean dropWeapon) {
        if (session.getState() != GameState.PLAYING) {
            return false;
        }

      ItemType itemType = inventoryService.useItem(session, indexInBackpack, dropWeapon);


        if (itemType != ItemType.EMPTY) {
            session.getRecord().incrementItemsUsed(itemType);
            enemyAiService.processAllEnemies(session, combatService, movementService);
            session.getRecord().incrementMoves();
        }

        return itemType != ItemType.EMPTY;
    }

    /**
     * Метод для загрузки сохраненной игры из файла.
     * @return  true/false - получилось ли считать данный и продолжить игру.
     * Если false - то игра начинается автоматически с 1 уровня
     */
    public boolean loadSaveGame() {
        GameSession loadSession = saveService.loadGame();
        if (loadSession != null) {
            session = loadSession;
            return true;
        }

        return false;
    }

    /**
     * Проверка не закончилась ли игра, из-за смерти игрока
     */
//    private void checkGameStatus() {
//        if (!session.getPlayer().isAlive()) {
//            session.setState(GameState.GAME_OVER);
//            session.getLeaderboard().addRecord(session.getRecord());
//        }
//    }


    // Getters
    public GameSession getSession() {
        return session;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
}
    public SaveService getSaveService()
    {
        return saveService;
    }
}
