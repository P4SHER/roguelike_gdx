package io.github.example.domain.entities;

import io.github.example.domain.level.Coordinates;

public abstract class Character {
    protected Stats stats;
    protected Coordinates coordinates;
    protected Item currentWeapon;
    protected char symbolForRendering;

    public Character (int maxHealth, int strength, int agility, char symbolForRendering) {
        this.stats = new Stats(maxHealth, strength, agility);
        this.symbolForRendering = symbolForRendering;
        this.coordinates = new Coordinates(0, 0);
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
}
