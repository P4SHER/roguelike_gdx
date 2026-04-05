package io.github.example.presentation.effects;

/**
 * Types of visual effects that can be rendered.
 */
public enum EffectType {
    DAMAGE,        // Damage number (red text)
    HEAL,          // Heal indicator (green text)
    SPELL,         // Spell effect animation
    WEAPON,        // Weapon hit effect
    STATUS,        // Status effect (poison, fire, etc)
    EXPERIENCE,    // XP gain notification
    LEVEL_UP       // Level up effect
}
