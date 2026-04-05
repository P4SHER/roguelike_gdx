package io.github.example.domain.entities;

import io.github.example.domain.level.Coordinates;
import java.util.ArrayList;
import java.util.List;

public abstract class Character {
    protected Stats stats;
    protected Coordinates coordinates;
    protected Item currentWeapon;
    protected char symbolForRendering;
    protected List<StatusEffect> statusEffects;

    public Character (int maxHealth, int strength, int agility, char symbolForRendering) {
        this.stats = new Stats(maxHealth, strength, agility);
        this.symbolForRendering = symbolForRendering;
        this.coordinates = new Coordinates(0, 0);
        this.statusEffects = new ArrayList<>();
    }

    public void move (int dx, int dy) {
        coordinates.move(dx, dy);
    }
    public boolean isAlive () {
        return stats.isAlive();
    }
    public void takeWeapon (Item weapon) {
        this.currentWeapon = weapon;
    }
    public void takeDamage(int damage) {
        stats.setCurrentHealth(stats.getCurrentHealth() - damage);
    }

    // Getters
    public Item getCurrentWeapon () { return currentWeapon; }
    public Coordinates getCoordinates () {
        return coordinates;
    }
    // !!!
    public char getSymbolForRendering () { return symbolForRendering; }
    public Stats getStats() { return stats; }

    // Setters
    public void setPosition(Coordinates newPos) { coordinates = newPos; }
    public int getDamage(){
        int weaponDamage=0;
        if(getCurrentWeapon() != null) {
            weaponDamage=getCurrentWeapon().getStrengthBonus();
        }
        return stats.getStrength() + weaponDamage;
    }

    /**
     * Adds a status effect to the character.
     */
    public void addStatusEffect(StatusEffect effect) {
        if (effect != null) {
            statusEffects.add(effect);
        }
    }

    /**
     * Removes a status effect from the character.
     */
    public void removeStatusEffect(StatusEffect effect) {
        statusEffects.remove(effect);
    }

    /**
     * Gets all active status effects.
     */
    public List<StatusEffect> getStatusEffects() {
        return statusEffects;
    }

    /**
     * Updates all status effects and removes expired ones.
     */
    public void updateStatusEffects(float deltaTime) {
        statusEffects.removeIf(effect -> effect.update(deltaTime));
    }
}
