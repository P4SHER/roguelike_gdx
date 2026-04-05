package io.github.example.domain.service;

import io.github.example.datalayer.SaveService;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;
import io.github.example.domain.level.LevelGeneration;
import io.github.example.domain.unittest.Logger;

/**
 * Для удобной генерации GameSession
 */
public class GameSessionGeneration {

    public GameSessionGeneration() {}

    /**
     * Здесь мы создаем все сущности и запускаем все необходимые методы для генерации игрового поля
     * @return GameSession, который хранит в себе заполненные сущности, готовые к игре.
     */
    public GameSession startNewGame() {
        SaveService saveService = new SaveService();
//        if (true) return saveService.loadGame();

        GameSession newSession = new GameSession(1);
        // Заполняем Session

        // Для создания игрока вроде больше ничего не надо
        Player player = new Player();
        newSession.setPlayer(player);

        // Генерируем уровень
        levelGeneration(newSession);

        // Считываем таблицу рекордов из .json
        newSession.setLeaderboard(newSession.getSaveService().loadLeaderboard());

        newSession.setState(GameState.PLAYING);
        return newSession;
    }

    public void levelGeneration(GameSession newSession) {
        LevelGeneration levelGeneration = new LevelGeneration();
        Level level = levelGeneration.generateLevel(newSession);
        newSession.setCurrentLevel(level);
    }

}
