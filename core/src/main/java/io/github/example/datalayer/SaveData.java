package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Level;
import io.github.example.domain.service.GameRecord;
import io.github.example.domain.service.GameState;
import io.github.example.domain.service.Leaderboard;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveData {
    private final PlayerSaveData player;
    private final LevelSaveData currentLevel;
    private final GameState state;
    private final int difficulty;
    private final GameRecord leaderboard;
    private final int currentLevelNumber;
    private final GameRecordSaveData record;
    private final int numberRound;

    @JsonCreator
    public SaveData(
            @JsonProperty("player") PlayerSaveData player,
            @JsonProperty("currentLevel") LevelSaveData currentLevel,
            @JsonProperty("state") GameState state,
            @JsonProperty("difficulty") int difficulty,
            @JsonProperty("leaderboard") GameRecord leaderboard,
            @JsonProperty("currentLevelNumber") int currentLevelNumber,
            @JsonProperty("totalDamageDealt") GameRecordSaveData record,
            @JsonProperty("numberRound") int numberRound ){
        this.player = player;
        this.currentLevel = currentLevel;
        this.state = state;
        this.difficulty = difficulty;
        this.leaderboard = leaderboard;
        this.currentLevelNumber = currentLevelNumber;
        this.record = record;
        this.numberRound = numberRound;
    }

    // Геттеры...
    public PlayerSaveData getPlayer() { return player; }
    public LevelSaveData getCurrentLevel() { return currentLevel; }
    public int getCurrentLevelNumber() { return currentLevelNumber; }
    public int getNumberRound() {
        return numberRound;
    }
    public GameRecordSaveData getRecord() {
        return record;
    }
    public int getDifficulty() {
        return difficulty;
    }
    public GameState getState() {
        return state;
    }
    public GameRecord getLeaderboard() {
        return leaderboard;
    }
}
