package io.github.example.domain.level;

import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.Item;

/**
Класс, описывающий комнату
 */
public class Room extends GameObject {
    private Coordinates coordinates;
    private Size size;
    private String type; // стартовая/конечная
  public Room(Coordinates coordinates, Size size) {
        super(coordinates,size);
        this.coordinates = coordinates;
        this.size = size;
  }
  public Coordinates getCoordinates() {
        return coordinates;
  }

  public Size getSize() {
      return size;
  }
  public String getType() {
      return type;
  }

public void setType(String type) {
      this.type = type;
}

}
