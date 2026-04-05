package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.entities.EnemyType;
import io.github.example.domain.entities.Item;
import io.github.example.domain.level.Coordinates;;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnemySaveData {
    private final EnemyType type;
    private final int hostilityRange;
    private final boolean isVisible;
    private final boolean canAttack;
    private final int maxHealth;
    private final int currentHealth;
    private final int strength;
    private final int agility;
    private final int x;
    private final int y;
    private final ItemSaveData currentWeapon;
    private final char symbolForRendering;

    @JsonCreator
    public EnemySaveData(
            @JsonProperty("type") EnemyType type,
            @JsonProperty("hostilityRange") int hostilityRange,
            @JsonProperty("isVisible") boolean isVisible,
            @JsonProperty("canAttack") boolean canAttack,
            @JsonProperty("maxHealth") int maxHealth,
            @JsonProperty("currentHealth") int currentHealth,
            @JsonProperty("strength") int strength,
            @JsonProperty("agility") int agility,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("currentWeapon") ItemSaveData currentWeapon,
            @JsonProperty("symbolForRendering") char symbolForRendering){
        this.type = type;
        this.hostilityRange = hostilityRange;
        this.isVisible = isVisible;
        this.canAttack = canAttack;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.strength = strength;
        this.agility = agility;
        this.x = x;
        this.y = y;
        this.currentWeapon = currentWeapon;
        this.symbolForRendering = symbolForRendering;
    }

    public EnemyType getType() {
        return type;
    }
    public int getHostilityRange() {
        return hostilityRange;
    }
    public char getSymbolForRendering() {
        return symbolForRendering;
    }
    public ItemSaveData getCurrentWeapon() {
        return currentWeapon;
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
    public int getAgility() {
        return agility;
    }
    public int getStrength() {
        return strength;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public boolean isCanAttack() {
        return canAttack;
    }
    public boolean isVisible() {
        return isVisible;
    }
}
