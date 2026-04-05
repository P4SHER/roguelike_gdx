package io.github.example.presentation.events;

import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.Item;
import io.github.example.domain.level.Coordinates;
import io.github.example.presentation.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Central event dispatcher for game events from domain.
 * Coordinates callbacks to presentation layer when game actions occur.
 * Events are triggered by GameService actions and relayed to registered listeners.
 */
public class GameEventDispatcher {
    private final List<GameEventListener> listeners = new ArrayList<>();

    /**
     * Register a listener for game events.
     */
    public void addEventListener(GameEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            Logger.debug("GameEventListener registered");
        }
    }

    /**
     * Unregister a listener.
     */
    public void removeEventListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire combat damage event.
     */
    public void onCombatDamage(float x, float y, int damage, boolean isCritical) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onCombatDamage(x, y, damage, isCritical);
            } catch (Exception e) {
                Logger.error("Error in onCombatDamage callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire player heal event.
     */
    public void onPlayerHeal(float x, float y, int amount) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onPlayerHeal(x, y, amount);
            } catch (Exception e) {
                Logger.error("Error in onPlayerHeal callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire experience gain event.
     */
    public void onExperienceGain(float x, float y, int amount) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onExperienceGain(x, y, amount);
            } catch (Exception e) {
                Logger.error("Error in onExperienceGain callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire level up event.
     */
    public void onPlayerLevelUp(float x, float y, int newLevel) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onPlayerLevelUp(x, y, newLevel);
            } catch (Exception e) {
                Logger.error("Error in onPlayerLevelUp callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire item picked up event.
     */
    public void onItemPickedUp(Item item) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onItemPickedUp(item);
            } catch (Exception e) {
                Logger.error("Error in onItemPickedUp callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire item dropped event.
     */
    public void onItemDropped(Item item, float x, float y) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onItemDropped(item, x, y);
            } catch (Exception e) {
                Logger.error("Error in onItemDropped callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire item used event.
     */
    public void onItemUsed(Item item) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onItemUsed(item);
            } catch (Exception e) {
                Logger.error("Error in onItemUsed callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire status effect applied event.
     */
    public void onStatusEffectApplied(String effectName, float x, float y) {
        for (GameEventListener listener : listeners) {
            try {
                listener.onStatusEffectApplied(effectName, x, y);
            } catch (Exception e) {
                Logger.error("Error in onStatusEffectApplied callback: " + e.getMessage());
            }
        }
    }

    /**
     * Fire player moved event.
     */
    public void onPlayerMoved(Coordinates oldPos, Coordinates newPos) {
        // Can extend GameEventListener if needed
        Logger.debug("Player moved from " + oldPos + " to " + newPos);
    }

    /**
     * Fire enemy moved event.
     */
    public void onEnemyMoved(Enemy enemy) {
        // Can extend GameEventListener if needed
        Logger.debug("Enemy moved to " + enemy.getCoordinates());
    }

    /**
     * Fire player attacked event.
     */
    public void onPlayerAttacked(Enemy defender, int damage, boolean hit) {
        float x = defender.getCoordinates().getX();
        float y = defender.getCoordinates().getY();
        if (hit) {
            onCombatDamage(x, y, damage, false);
        }
    }

    /**
     * Fire player died event.
     */
    public void onPlayerDied(Player player) {
        Logger.info("Player died at " + player.getCoordinates());
    }

    /**
     * Clear all listeners (used on screen change).
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Get number of registered listeners.
     */
    public int getListenerCount() {
        return listeners.size();
    }
}
