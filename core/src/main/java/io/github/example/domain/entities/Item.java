package io.github.example.domain.entities;

import io.github.example.domain.level.Coordinates;

/**
 * Пример использования: Item sword = new Item("Ржавый меч", ItemType.WEAPON, ItemSubType.NONE, 15);
 */
public class Item {
    private final String name;
    private final ItemType type;
    private final ItemSubType subType;      // подтип, необходим для некоторых предметов

    private int healthRestoreFood = 0;      // Для еды
    private int healthRestoreElixir = 0;
    private int statBoost;                  // Для эликсиров/свитков (на сколько повышает)
    private int strengthBonus;              // Для оружия
    private int cost;
    private Coordinates pos = new Coordinates(0, 0);

    private int countFoodInBackpack;       // Что бы еда могла стаковаться

    private final boolean isPermanent; // Показатель - теряется ли предмет после применения или нет. True - теряет
    // False - остается в рюкзаке

    public Item (String name, ItemType type, ItemSubType subType) {
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.isPermanent = (type == ItemType.WEAPON);
        this.countFoodInBackpack = type == ItemType.FOOD ? 1 : 0;

    }

    public void AddCountFoodInBackpack() { countFoodInBackpack++; }
    public void lowerCountFoodInBackpack() { countFoodInBackpack--; }

    public Item copyObject() {
        Item copy = new Item(name, type, subType);

        copy.setHealthRestoreFood(healthRestoreFood);
        copy.setHealthRestoreElixir(healthRestoreElixir);
        copy.setStatBoost(statBoost);
        copy.setStrengthBonus(strengthBonus);
        copy.setCost(cost);
        copy.setCountFoodInBackpack(countFoodInBackpack);

        return copy;
    }

    // Getters
    public boolean isPermanent() { return isPermanent; }
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public ItemSubType getSubType() { return subType; }
    public int getHealthRestoreFood() { return healthRestoreFood; }
    public int getHealthRestoreElixir() { return healthRestoreElixir; }
    public int getStatBoost() { return statBoost; }
    public int getStrengthBonus() { return strengthBonus; }
    public int getCost() { return cost; }
    public int getCountFoodInBackpack() { return countFoodInBackpack; }
    public Coordinates getPos() { return pos; }

    // Setters
    public void setHealthRestoreFood(int amount) { this.healthRestoreFood = amount; }
    public void setHealthRestoreElixir (int amount) { this.healthRestoreElixir = amount; }
    public void setStatBoost(int amount) { this.statBoost = amount; }
    public void setStrengthBonus(int bonus) { this.strengthBonus = bonus; }
    public void setCost(int cost) { this.cost = cost; }
    public void setCountFoodInBackpack(int currentFoodInBackpack) {this.countFoodInBackpack = currentFoodInBackpack; }
    public void setPos(Coordinates newPos) { this.pos = newPos; }
}
