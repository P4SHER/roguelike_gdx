package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameRecordSaveData {
    private final int numberTreasures;             // количество сокровищ
    private final int numberLevel;                 // достигнутый уровень
    private final int numberDefeatedEnemies;       // количество побежденных противников
    private final int numberMealsEaten;            // количество съеденной еды
    private final int numberElixirsConsumed;       // количество выпитых эликсиров
    private final int numberScrollsRead;           // количество прочитанных свитков
    private final int damageDealt;                 // количество нанесенных
    private final int damageTaken;                 // количество пропущенных ударов
    private final int incrementMoves;

    @JsonCreator
    public GameRecordSaveData(
            @JsonProperty("numberTreasures") int numberTreasures,
            @JsonProperty("numberLevel") int numberLevel,
            @JsonProperty("numberDefeatedEnemies") int numberDefeatedEnemies,
            @JsonProperty("numberMealsEaten") int numberMealsEaten,
            @JsonProperty("numberElixirsConsumed") int numberElixirsConsumed,
            @JsonProperty("numberScrollsRead") int numberScrollsRead,
            @JsonProperty("damageDealt") int damageDealt,
            @JsonProperty("damageTaken") int damageTaken,
            @JsonProperty("incrementMoves") int incrementMoves) {
        this.numberTreasures = numberTreasures;
        this.numberLevel = numberLevel;
        this.numberDefeatedEnemies = numberDefeatedEnemies;
        this.numberMealsEaten = numberMealsEaten;
        this.numberElixirsConsumed = numberElixirsConsumed;
        this.numberScrollsRead = numberScrollsRead;
        this.damageDealt = damageDealt;
        this.damageTaken = damageTaken;
        this.incrementMoves = incrementMoves;
    }

    public int getIncrementMoves() {
        return incrementMoves;
    }
    public int getDamageTaken() { return damageTaken; }
    public int getDamageDealt() { return damageDealt; }
    public int getNumberScrollsRead() { return numberScrollsRead; }
    public int getNumberElixirsConsumed() { return numberElixirsConsumed; }
    public int getNumberMealsEaten() { return numberMealsEaten; }
    public int getNumberDefeatedEnemies() { return numberDefeatedEnemies; }
    public int getNumberLevel() { return numberLevel; }
    public int getNumberTreasures() { return numberTreasures; }
}
