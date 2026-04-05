package io.github.example.domain.service;

import io.github.example.datalayer.SaveData;
import io.github.example.datalayer.SaveService;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;

/**
 * Хранит информацию всю информацию про игру. Что бы удобно было передавать в разные класс и они уже парсили себе нужную информацию.
 */
public class GameSession {
    private Player player;                  // Объект Игрока
    private Level currentLevel;             // Игровое поле, текущий уровень
    private GameState state;                // Статус игры
    private Leaderboard leaderboard;        // Таблица рекордов
    private final SaveService saveService;
    private int difficulty = 1;             // Сложность уровня
    private GameRecord record;              // Класс, с переменными для статистики

    private int numberRound = 0;            // Номер хода

    public GameSession(int currentLevel) {
        this.state = GameState.PLAYING;
        this.record = new GameRecord();
        record.setNumberLevel(currentLevel);
        this.saveService = new SaveService();
    }

    /**
     * Переход на новый уровень.
     */
    public void transitionToNextLevel() {
        if (record.getNumberLevel() >= GameConfig.MAX_LEVEL) {
            state = GameState.WIN;
            saveService.saveGame(this);
            return;
        }

        leaderboard.updateLastRecord(record);
        player.deleteBust();

        record.setNumberTreasures(player.getBackpack().getTotalTreasureValue());
        record.incrementNumberLevel();
        state = GameState.LEVEL_COMPLETE;
    }

    public void addDamageDealt(int damage) {
        record.setDamageDealt(record.getDamageDealt() + damage);
    }
    public void addDamageTaken(int damage) { record.setDamageTaken(record.getDamageTaken() + damage); }

    // Gets and Sets
    public Player getPlayer() { return player; }
    public Level getCurrentLevel() { return currentLevel; }
    public GameState getState() { return state; }
    public Leaderboard getLeaderboard() { return leaderboard; }
    public int getDifficulty() { return difficulty; }
    public int getCurrentLevelNumber() { return record.getNumberLevel(); }
    public GameRecord getRecord() { return record; }
    public int getNumberRound() {
        return numberRound;
    }
    public SaveService getSaveService() { return saveService; }

    public void setPlayer(Player curPlayer) { player = curPlayer;}
    public void setCurrentLevel(Level curLevel) { currentLevel = curLevel; }
    public void setState(GameState curState) { state = curState; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setLeaderboard(Leaderboard leaderboard) { this.leaderboard = leaderboard; }
    public void setRecord(GameRecord record) {
        this.record = record;
    }

    public void incrementRound() { numberRound++; }
}
