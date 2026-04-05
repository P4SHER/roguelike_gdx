package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.entities.ItemType;
import io.github.example.domain.entities.ItemSubType;
import io.github.example.domain.level.Coordinates;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemSaveData {
    private final String name;
    private final ItemType type;
    private final ItemSubType subType;
    private final int healthRestoreFood;
    private final int healthRestoreElixir;
    private final int statBoost;
    private final int strengthBonus;
    private final int cost;
    private final int countFoodInBackpack;
    private final int x;
    private final int y;

    @JsonCreator
    public ItemSaveData(
            @JsonProperty("name") String name,
            @JsonProperty("type") ItemType type,
            @JsonProperty("subType") ItemSubType subType,
            @JsonProperty("healthRestoreFood") int healthRestoreFood,
            @JsonProperty("healthRestoreElixir") int healthRestoreElixir,
            @JsonProperty("statBoost") int statBoost,
            @JsonProperty("strengthBonus") int strengthBonus,
            @JsonProperty("cost") int cost,
            @JsonProperty("countFoodInBackpack") int countFoodInBackpack,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y){
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.healthRestoreFood = healthRestoreFood;
        this.healthRestoreElixir = healthRestoreElixir;
        this.statBoost = statBoost;
        this.strengthBonus = strengthBonus;
        this.cost = cost;
        this.countFoodInBackpack = countFoodInBackpack;
        this.x = x;
        this.y = y;
    }

    // Геттеры...
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public ItemSubType getSubType() { return subType; }
    public int getHealthRestoreFood() {
        return healthRestoreFood;
    }
    public int getCountFoodInBackpack() { return countFoodInBackpack; }
    public int getCost() { return cost; }
    public int getStrengthBonus() { return strengthBonus; }
    public int getStatBoost() { return statBoost; }
    public int getHealthRestoreElixir() { return healthRestoreElixir; }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
}
