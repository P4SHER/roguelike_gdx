package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.level.Passage;
import io.github.example.domain.level.Room;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LevelSaveData {
    private final List<TileSaveData> tiles;
    private final List<ItemSaveData> items;
    private final List<EnemySaveData> enemies;

    // Добавление данных
    private final List<RoomSaveData> rooms;
//    private final List<Passage> passages;
    private final int exitX;
    private final int exitY;
//    private final int coordX;
//    private final int coordY;

    @JsonCreator
    public LevelSaveData(
          @JsonProperty("tiles") List<TileSaveData> tiles,
          @JsonProperty("items") List<ItemSaveData> items,
          @JsonProperty("enemies") List<EnemySaveData> enemies,
          // Доп
          @JsonProperty("rooms") List<RoomSaveData> rooms,
//          @JsonProperty("passages") List<Passage> passages,
          @JsonProperty("exitX") int exitX,
          @JsonProperty("exitY") int exitY
//          @JsonProperty("coordX") int coordX,
//          @JsonProperty("coordY") int coordY
          ){
        this.tiles = tiles;
        this.items = items;
        this.enemies = enemies;

        this.rooms = rooms;
//        this.passages = passages;
        this.exitX = exitX;
        this.exitY = exitY;
//        this.coordX = coordX;
//        this.coordY = coordY;
    }

    // Геттеры...
    public List<EnemySaveData> getEnemies() {
        return enemies;
    }
    public List<ItemSaveData> getItems() {
        return items;
    }
    public List<TileSaveData> getTiles() {
        return tiles;
    }
//    public int getCoordY() {
//        return coordY;
//    }
//    public int getCoordX() {
//        return coordX;
//    }
    public int getExitY() {
        return exitY;
    }
    public int getExitX() {
        return exitX;
    }
//    public List<Passage> getPassages() {
//        return passages;
//    }
    public List<RoomSaveData> getRooms() {
        return rooms;
    }

}
