package io.github.example.domain.entities;

import io.github.example.domain.service.GameConfig;

public class Enemy extends Character {
    private final EnemyType type;
    private int hostilityRange;             // Дистанция для атаки
    private boolean isVisible;              // Для призраков

    // Надо сделать усложнение, пока заглушено как true
    private boolean canAttack = true;

    public Enemy(EnemyType type, int levelDifficulty) {
        super(EntityConfig.ENEMY_START_HP, EntityConfig.ENEMY_START_STERNGHT, EntityConfig.ENEMY_START_AGILITY, type.symbol);
        this.type = type;
        // Надо переместить в configureStats
        this.isVisible = true;

        // Настройка статов в зависимости от типа и сложности уровня
        configureStats(levelDifficulty);
    }

    private void configureStats(int level) {
        int multiplier = 1 + (level / 5);

        switch (type) {
            case ZOMBIE:
                stats.setMaxHealth(50 * multiplier, false);
                stats.setStrength(8 * multiplier, false);
                stats.setAgility(2 * multiplier, false);
                hostilityRange = 3;
                break;
            case VAMPIRE:
                canAttack = false;
                stats.setMaxHealth(40 * multiplier, false);
                stats.setStrength(10 * multiplier, false);
                stats.setAgility(12 * multiplier, false);
                hostilityRange = 5;
                break;
            case GHOST:
                stats.setMaxHealth(20 * multiplier, false);
                stats.setStrength(4 * multiplier, false);
                stats.setAgility(15 * multiplier, false);
                hostilityRange = 4;
                isVisible = false; // Изначально невидим
                break;
            case OGRE:
                stats.setMaxHealth(80 * multiplier, false);
                stats.setStrength(20 * multiplier, false);
                stats.setAgility(multiplier, false);
                hostilityRange = 2;
                break;
            case SNAKE_MAGE:
                stats.setMaxHealth(30 * multiplier, false);
                stats.setStrength(6 * multiplier, false);
                stats.setAgility(18 * multiplier, false);
                hostilityRange = 6;
                break;
        }
    }

    public boolean isVisible() { return isVisible; }

    // Специфичная логика атаки (например, вампир крадет макс здоровье)
    public boolean hasSpecialAbility() {
        return type == EnemyType.VAMPIRE || type == EnemyType.SNAKE_MAGE;
    }

    // Getters
    public boolean getCanAttack() { return canAttack; }
    public EnemyType getType() { return type; }
    public int getHostilityRange() { return hostilityRange; }

    // Setters
    public void setVisible(boolean visible) { isVisible = visible; }
    public void setCanAttack(boolean value) { canAttack = value; }
    public void setCanAttackBase(boolean value) { canAttack = value; }
    public void setHostilityRange(int value) { hostilityRange = value; }
}
