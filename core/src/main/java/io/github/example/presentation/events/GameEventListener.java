package io.github.example.presentation.events;

import io.github.example.domain.entities.Item;

/**
 * Listener interface for game events from GameService.
 * Allows presentation layer to react to domain events.
 */
public interface GameEventListener {
    
    /**
     * Called when a combat action occurs.
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param damage Damage amount
     * @param isCritical Whether this was a critical hit
     */
    void onCombatDamage(float x, float y, int damage, boolean isCritical);
    
    /**
     * Called when player heals.
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param amount Healing amount
     */
    void onPlayerHeal(float x, float y, int amount);
    
    /**
     * Called when player gains experience.
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param amount Experience amount
     */
    void onExperienceGain(float x, float y, int amount);
    
    /**
     * Called when player levels up.
     * @param x World X coordinate
     * @param y World Y coordinate
     * @param newLevel The new level
     */
    void onPlayerLevelUp(float x, float y, int newLevel);
    
    /**
     * Called when item is picked up.
     * @param item The item picked up
     */
    void onItemPickedUp(Item item);
    
    /**
     * Called when item is dropped.
     * @param item The item dropped
     * @param x World X coordinate
     * @param y World Y coordinate
     */
    void onItemDropped(Item item, float x, float y);
    
    /**
     * Called when item is used.
     * @param item The item used
     */
    void onItemUsed(Item item);
    
    /**
     * Called when status effect is applied.
     * @param effectName Name of the effect
     * @param x World X coordinate
     * @param y World Y coordinate
     */
    void onStatusEffectApplied(String effectName, float x, float y);
}
