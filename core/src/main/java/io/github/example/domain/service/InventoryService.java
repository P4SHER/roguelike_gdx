package io.github.example.domain.service;

import io.github.example.domain.entities.Item;
import io.github.example.domain.entities.ItemSubType;
import io.github.example.domain.entities.ItemType;
import io.github.example.domain.entities.Stats;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Level;

public class InventoryService {
    // true - drop in map
    // false - add in backpack weapon
    public io.github.example.domain.entities.ItemType useItem(GameSession session, int indexInBackpack, boolean dropWeapon) {
        Item item;
        if (indexInBackpack == -1)
            item = session.getPlayer().getCurrentWeapon();
        else
            item = session.getPlayer().getBackpack().getAllItems().get(indexInBackpack);

        if (item == null) {
            return ItemType.EMPTY;
        }
        ItemType type = item.getType();
        boolean result = false;

        switch (type) {
            case FOOD -> {
                result = true;
                if (item.getCountFoodInBackpack() != 0) {
                    changingCharacteristic(session, item);
                    item.lowerCountFoodInBackpack();
                    session.getRecord().incrementMealsEaten();
                }
                if (item.getCountFoodInBackpack() == 0)
                    session.getPlayer().getBackpack().removeItem(item);
            }
            case SCROLL, ELIXIR -> {
                result = true;
                changingCharacteristic(session, item);

                if (item.getType() == ItemType.ELIXIR) session.getRecord().incrementElixirsConsumed();
                else session.getRecord().incrementScrollsRead();

                if (!item.isPermanent())
                    session.getPlayer().getBackpack().removeItem(item);
            }
            case WEAPON -> {
                result = true;
                Item lastWeapon = session.getPlayer().getCurrentWeapon();
                if (dropWeapon) {
                    if (!session.getPlayer().getBackpack().addItem(lastWeapon)) {
                        Coordinates pos = posDropWeapon(session);
                        session.getCurrentLevel().addItemInLevel(pos, lastWeapon.copyObject());
                    }
                    session.getPlayer().takeWeapon(null);
                } else {
                    Item copyItem = item.copyObject();
                    session.getPlayer().takeWeapon(copyItem);
                    session.getPlayer().getBackpack().removeItem(item);
                    if (lastWeapon != null) {
                        Coordinates pos = posDropWeapon(session);
                        session.getCurrentLevel().addItemInLevel(pos, lastWeapon.copyObject());
                    }
                }
            }
        }

        if (result) return item.getType();
        return ItemType.EMPTY;
    }

    private Coordinates posDropWeapon(GameSession session) {
        Level level = session.getCurrentLevel();
        Coordinates playerPos = session.getPlayer().getCoordinates();
        Coordinates pos1 = playerPos;
        Coordinates pos2 = playerPos;
        Coordinates pos3 = playerPos;
        Coordinates pos4 = playerPos;
        pos1.move(0, -1);
        pos1.move(0, 1);
        pos1.move(-1, 0);
        pos1.move(1, 0);

        if (level.isWalkable(pos1)) {
            return pos1;
        } else if (level.isWalkable(pos2)) {
            return pos2;
        } else if (level.isWalkable(pos3)) {
            return pos3;
        } else if (level.isWalkable(pos4)) {
            return pos4;
        }

        return playerPos;
    }

    private void changingCharacteristic(GameSession session, Item item) {
        Stats stats = session.getPlayer().getStats();

        // Увеличение характеристик
        stats.setCurrentHealth(Math.min(stats.getCurrentHealth() + item.getHealthRestoreFood(), stats.getMaxHealth()));
        stats.setMaxHealth(stats.getMaxHealth() + item.getHealthRestoreElixir(), item.getType() == ItemType.ELIXIR);
        if (item.getSubType() == ItemSubType.STRENGTH)
            stats.setStrength(stats.getStrength() + item.getStatBoost(), item.getType() == ItemType.ELIXIR);
        else if (item.getSubType() == ItemSubType.AGILITY)
            stats.setAgility(stats.getAgility() + item.getStatBoost(), item.getType() == ItemType.ELIXIR);
        stats.setStrength(stats.getStrength() + item.getStrengthBonus(), item.getType() == ItemType.ELIXIR);

    }
}
