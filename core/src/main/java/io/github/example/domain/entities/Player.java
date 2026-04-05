package io.github.example.domain.entities;

import io.github.example.domain.service.GameConfig;
import io.github.example.domain.unittest.Logger;

public class Player extends Character {
    private final Backpack backpack = new Backpack();

    public Player () {
        super(EntityConfig.PLAYER_START_HP, EntityConfig.PLAYER_START_STRENGTH, EntityConfig.PLAYER_START_AGILITY, EntityConfig.PLAYER_SYMBOL_FOR_RENDER);
    }

    public void deleteBust() {
        Logger.info(stats.getDeleteStrength().toString());
        Logger.info(stats.getDeleteAgility().toString());
        Logger.info(stats.getDeleteMaxHealth().toString());
        stats.setStrength(Math.max(stats.getStrength() - stats.getDeleteStrength(), 0), false);
        stats.setAgility(Math.max(stats.getAgility() - stats.getDeleteAgility(), 0), false);
        stats.setMaxHealth(Math.max(stats.getMaxHealth() - stats.getDeleteMaxHealth(), 0), false);

        stats.setDeleteAgility(0);
        stats.setDeleteStrength(0);
        stats.setDeleteMaxHealth(0);
    }
    public Backpack getBackpack() { return backpack; }
}
