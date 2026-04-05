package io.github.example.domain.service;

import io.github.example.domain.entities.ItemType;

public class GameRecord {
    private int numberTreasures = 0;             // количество сокровищ
    private int numberLevel = 0;                 // достигнутый уровень
    private int numberDefeatedEnemies = 0;       // количество побежденных противников
    private int numberMealsEaten = 0;            // количество съеденной еды
    private int numberElixirsConsumed = 0;       // количество выпитых эликсиров
    private int numberScrollsRead = 0;           // количество прочитанных свитков
    private int damageDealt = 0;                 // количество нанесенных
    private int damageTaken = 0;                 // количество пропущенных ударов
    private int incrementMoves = 0;              // количество пройденных клеток

    public void incrementItemsUsed(ItemType type) {
        switch (type) {
            case FOOD:
                numberMealsEaten++;
                break;
            case ELIXIR:
                numberElixirsConsumed++;
                break;
            case SCROLL:
                numberScrollsRead++;
                break;
        }
    }

    public void incrementNumberDefeatedEnemies() { numberDefeatedEnemies++; }
    public void incrementNumberLevel() { numberLevel++; }
    public void incrementMealsEaten() { numberMealsEaten++; }
    public void incrementElixirsConsumed() { numberElixirsConsumed++; }
    public void incrementScrollsRead() { numberScrollsRead++; }

    // Getters
    public int getDamageDealt() {
        return damageDealt;
    }
    public int getDamageTaken() {
        return damageTaken;
    }
    public int getIncrementMoves() {
        return incrementMoves;
    }
    public int getNumberDefeatedEnemies() {
        return numberDefeatedEnemies;
    }
    public int getNumberElixirsConsumed() {
        return numberElixirsConsumed;
    }
    public int getNumberLevel() {
        return numberLevel;
    }
    public int getNumberMealsEaten() {
        return numberMealsEaten;
    }
    public int getNumberScrollsRead() {
        return numberScrollsRead;
    }
    public int getNumberTreasures() {
        return numberTreasures;
    }

    // Setters
    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }
    public void setDamageTaken(int damageTaken) {
        this.damageTaken = damageTaken;
    }
    public void incrementMoves() {
        this.incrementMoves++;
    }
    public void setNumberDefeatedEnemies(int numberDefeatedEnemies) {
        this.numberDefeatedEnemies = numberDefeatedEnemies;
    }
    public void setNumberElixirsConsumed(int numberElixirsConsumed) {
        this.numberElixirsConsumed = numberElixirsConsumed;
    }
    public void setNumberLevel(int numberLevel) {
        this.numberLevel = numberLevel;
    }
    public void setNumberMealsEaten(int numberMealsEaten) {
        this.numberMealsEaten = numberMealsEaten;
    }
    public void setNumberScrollsRead(int numberScrollsRead) {
        this.numberScrollsRead = numberScrollsRead;
    }
    public void setIncrementMoves(int value) { incrementMoves = value;}
    public void setNumberTreasures(int numberTreasures) {
        this.numberTreasures = numberTreasures;
    }

}
