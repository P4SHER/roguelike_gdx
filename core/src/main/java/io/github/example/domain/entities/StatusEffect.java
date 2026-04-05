package io.github.example.domain.entities;

/**
 * Represents an active status effect (buff or debuff) on a character.
 * Tracks effect type, duration, and elapsed time.
 */
public class StatusEffect {
    private final StatusEffectType type;
    private final float duration;
    private float elapsedTime;
    private final String name;

    /**
     * Creates a status effect with given type and duration.
     * 
     * @param type The type of status effect
     * @param duration Duration in seconds
     */
    public StatusEffect(StatusEffectType type, float duration) {
        this.type = type;
        this.duration = duration;
        this.elapsedTime = 0f;
        this.name = type.getName();
    }

    /**
     * Updates elapsed time and returns true if effect has expired.
     * 
     * @param deltaTime Time elapsed in seconds
     * @return true if effect has expired (elapsedTime >= duration)
     */
    public boolean update(float deltaTime) {
        elapsedTime += deltaTime;
        return isExpired();
    }

    /**
     * Returns remaining duration in seconds.
     */
    public float getRemainingDuration() {
        return Math.max(0f, duration - elapsedTime);
    }

    /**
     * Checks if effect has expired.
     */
    public boolean isExpired() {
        return elapsedTime >= duration;
    }

    // Getters
    public StatusEffectType getType() {
        return type;
    }

    public float getDuration() {
        return duration;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns true if duration is critical (<5 seconds remaining).
     */
    public boolean isCritical() {
        return getRemainingDuration() < 5f;
    }
}
