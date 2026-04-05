package io.github.example.domain.level;

import io.github.example.domain.entities.*;
import io.github.example.domain.service.GameConfig;
import io.github.example.domain.service.GameSession;

import java.util.*;

/**
 * Класс отвечающий за работу с картой
 */
public class Level {
    private final Tile[][] tiles;

//    private final int difficulty; // сложность уровня
    private final Room[] rooms;
    private final ArrayList<Passage> passages = new ArrayList<>();
    private int level_num = 0; // номер уровня
    private Coordinates exitCoordinates; // координаты выхода из уровня
    private int startRoomIndex;
    private int endRoomIndex;

    private final Map<Coordinates, Item> items = new HashMap<>();
    private final Map<Coordinates, Enemy> enemies = new HashMap<>();

    /**
     *
     * @param session Из него берем данные об игре
     */
    public Level(GameSession session) {
        this.level_num = session.getCurrentLevelNumber();
        this.tiles = new Tile[GameConfig.REGION_HEIGHT][GameConfig.REGION_WIDTH];
        this.rooms = new Room[GameConfig.COUNT_OF_ROOMS];
    }

    /**
     * Делает проверку, свободна ли клетка для хода врага(без учета положения игрока)
     * @param pos - координата на которую хотим переместиться
     * @return true/false
     */
    public boolean isWalkable(Coordinates pos) {
        Tile tileAtPos = tiles[pos.getY()][pos.getX()];
        return !enemies.containsKey(pos) && !isNotRoomAndPassage(pos) && !items.containsKey(pos) && tileAtPos.getSpaceType() != SpaceType.Exit;
    }
    public boolean isExit(Coordinates pos) {
        return tiles[pos.getY()][pos.getX()].getSpaceType() == SpaceType.Exit;
    }
    public boolean isNotRoomAndPassage(Coordinates pos) {
        SpaceType type = tiles[pos.getY()][pos.getX()].getSpaceType();
        return type == SpaceType.Wall || type == SpaceType.Nothing;
    }
    public void addEnemy(Coordinates cords, Enemy enemy) {
        enemy.move(cords.getX(), cords.getY());
        enemies.put(cords, enemy);
    }
    public void addItemInLevel(Coordinates pos, Item item) {
        items.put(pos, item);
//        item.setPos(pos);
    }
    public void removeItem(Coordinates cords) {
        items.remove(cords);
    }
    public void removeEnemy(Coordinates cords) {
        enemies.remove(cords);
    }



    // Getters
    public int getStartRoomIndex() {
        return startRoomIndex;
    }
    public int getLevel_num() {
        return level_num;
    }
    public ArrayList<Passage> getPassages() {
        return passages;
    }
    public int getEndRoomIndex() {
        return endRoomIndex;
    }
    public List<Enemy> getAllEnemies() {
        return new ArrayList<>(enemies.values());
    }
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
    public Map<Coordinates, Enemy> getEnemies() { return enemies; }
    public Map<Coordinates, Item> getItems() { return items; }
    public Room[] getRooms() {
        return rooms;
    }
    public Coordinates getExitCoordinates() {
        return exitCoordinates;
    }
    public Tile[][] getTiles() {
        return tiles;
    }
    public Item getItemCopyAtPos(Coordinates cords) {
        return items.get(cords).copyObject();
    }
    public Enemy getEnemyAtPos(Coordinates cords) {
        return enemies.get(cords);
    }

    // Setters
    public void setExitCoordinates(Coordinates pos) { exitCoordinates = pos; }
    public void setEndRoomIndex(int endRoomIndex) { this.endRoomIndex = endRoomIndex; }
    public void setStartRoomIndex(int startRoomIndex) { this.startRoomIndex = startRoomIndex; }
    public void setPositionEnemy(Enemy enemy,Coordinates lastPos, Coordinates newPos) {
        enemies.remove(lastPos);
        enemies.put(newPos, enemy);
        if (!Coordinates.equals(lastPos, newPos)) enemy.setPosition(newPos);
    }

}
