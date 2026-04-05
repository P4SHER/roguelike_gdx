package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSaveData {
    private final int maxHealth;
    private final int currentHealth;
    private final int strength;
    private final int agility;
    private final int x;
    private final int y;
    private final ItemSaveData currentWeapon;

    // Backpack
    private final List<ItemSaveData> items;
    private final int currentItems;
    private final int totalTreasureValue;

    @JsonCreator
    public PlayerSaveData(
            @JsonProperty("maxHealth") int maxHealth,
            @JsonProperty("currentHealth") int currentHealth,
            @JsonProperty("strength") int strength,
            @JsonProperty("agility") int agility,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("currentWeapon") ItemSaveData currentWeapon,
            @JsonProperty("items") List<ItemSaveData> items,
            @JsonProperty("currentItems") int currentItems,
            @JsonProperty("totalTreasureValue") int totalTreasureValue) {
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.strength = strength;
        this.agility = agility;
        this.x = x;
        this.y = y;
        this.currentWeapon = currentWeapon;
//        this.symbolForRendering = symbolForRendering;
//        this.items = items;
        this.items = (items != null) ? items : new ArrayList<>();
        this.currentItems = currentItems;
        this.totalTreasureValue = totalTreasureValue;
    }

    // Геттеры

    public int getTotalTreasureValue() {
        return totalTreasureValue;
    }
    public int getCurrentItems() {
        return currentItems;
    }
    public List<ItemSaveData> getItems() {
        return items;
    }
//    public char getSymbolForRendering() {
//        return symbolForRendering;
//    }
    public ItemSaveData getCurrentWeapon() {
        return currentWeapon;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

}
