package io.github.example.presentation.screens;

import io.github.example.domain.service.GameSession;

/**
 * Snapshot of game state for preservation during transitions.
 * Allows saving and restoring game state during screen transitions.
 */
public class GameStateSnapshot {
    private GameSession gameSession;
    private String screenName;
    private long timestamp;

    public GameStateSnapshot(GameSession gameSession, String screenName) {
        this.gameSession = gameSession;
        this.screenName = screenName;
        this.timestamp = System.currentTimeMillis();
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired(long maxAgeMs) {
        return System.currentTimeMillis() - timestamp > maxAgeMs;
    }
}
