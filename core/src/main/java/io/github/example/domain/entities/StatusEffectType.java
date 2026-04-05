package io.github.example.domain.entities;

/**
 * Enumeration of all status effect types in the game.
 * Each effect has a name, display icon, and color for HUD rendering.
 */
public enum StatusEffectType {
    POISON("Poison", "P", "poison"),
    FIRE("Fire", "F", "fire"),
    COLD("Cold", "C", "cold"),
    BLEEDING("Bleeding", "B", "bleeding"),
    CURSE("Curse", "X", "curse"),
    SHIELD("Shield", "S", "shield"),
    REGENERATION("Regeneration", "R", "regeneration"),
    SLOW("Slow", "↓", "slow"),
    HASTE("Haste", "↑", "haste"),
    STUN("Stun", "!", "stun");

    private final String displayName;
    private final String iconSymbol;
    private final String effectKey;

    StatusEffectType(String displayName, String iconSymbol, String effectKey) {
        this.displayName = displayName;
        this.iconSymbol = iconSymbol;
        this.effectKey = effectKey;
    }

    public String getName() {
        return displayName;
    }

    public String getIconSymbol() {
        return iconSymbol;
    }

    public String getEffectKey() {
        return effectKey;
    }
}
