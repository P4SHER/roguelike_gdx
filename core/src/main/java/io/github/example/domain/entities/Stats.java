package io.github.example.domain.entities;

/**
 * Класс с характеристиками персонажей(игрок и враги).
 * Нужен для удобства, что бы не было куча переменных в одном классе.
 */
public class Stats {
    private int maxHealth;
    private int currentHealth;
    private int strength;
    private int agility;

    private int deleteMaxHealth = 0;
    private int deleteStrength = 0;
    private int deleteAgility = 0;

    public Stats(int maxHealth, int strength, int agility) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.strength = strength;
        this.agility = agility;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public void heal(int amount) {
        this.currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    // Getters
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public Integer getDeleteAgility() {
        return deleteAgility;
    }
    public Integer getDeleteMaxHealth() {
        return deleteMaxHealth;
    }
    public Integer getDeleteStrength() {
        return deleteStrength;
    }

    // Setters
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, currentHealth); // Не меньше 0
    }
    public void setDeleteAgility(int deleteAgility) {
        this.deleteAgility = deleteAgility;
    }
    public void setDeleteMaxHealth(int deleteMaxHealth) {
        this.deleteMaxHealth = deleteMaxHealth;
    }
    public void setDeleteStrength(int deleteStrength) {
        this.deleteStrength = deleteStrength;
    }

    public void setStrength(int newStrength, boolean delete) {
        if (delete) this.deleteStrength += (newStrength - strength);
        this.strength = Math.max(newStrength, 0);
    }
    public void setAgility(int newAgility, boolean delete) {
        if (delete) this.deleteAgility += (newAgility - agility);
        this.agility = Math.max(newAgility, 0);
    }
    public void setMaxHealth(int newMaxHealth, boolean delete) {
        if (delete) this.deleteMaxHealth += (newMaxHealth - maxHealth);
        if (currentHealth < newMaxHealth) {
            currentHealth += newMaxHealth - this.maxHealth;
        }
        this.maxHealth = newMaxHealth;
    }
}
