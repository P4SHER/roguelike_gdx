package io.github.example.domain.entities;

import io.github.example.domain.service.GameConfig;
import io.github.example.domain.service.GameRecord;
import io.github.example.domain.unittest.Logger;

import java.util.ArrayList;
import java.util.List;

public class Backpack {
    private List<Item> items = new ArrayList<>();
    private int currentItems = 0;
    private int totalTreasureValue;

    // Кладем в рюкзак предмет
    public boolean addItem(Item item) {
        if (item == null) return false;

        if (item.getType() == ItemType.TREASURE) {
            totalTreasureValue += item.getCost();
            return true;
        }

        if (item.getType() == ItemType.FOOD) {
            for (Item existingItem : items) {
                if (existingItem.getType() == ItemType.FOOD) {
                    existingItem.AddCountFoodInBackpack();
                    return true;
                }
            }
        }

        if (currentItems < EntityConfig.MAX_ITEMS_IN_BACKPACK) {
            items.add(item);
            currentItems++;
            return true;
        } else return false;
    }

    public void removeItem(Item item) {
        if (item == null) return;

        if (item.getType() == ItemType.FOOD) {
            if (item.getCountFoodInBackpack() > 2) {
                item.setCountFoodInBackpack(item.getCountFoodInBackpack() - 1);
                return;
            }
        }

        items.remove(item);
        currentItems--;
    }

    public void addTotalTreasureValue(int value) { totalTreasureValue += Math.max(value, 0); }

    // Getters
    public int getCurrentItems() { return currentItems; }
    public int getTotalTreasureValue() {
        return totalTreasureValue;
    }
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    // Setters
    public void setTotalTreasureValue(int value) { totalTreasureValue = value; }
    public void setItems(List<Item> items) { this.items = items != null ? items : new ArrayList<>(); }
    public void setCurrentItems(int value) { currentItems = value;}
}

