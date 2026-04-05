package io.github.example.domain.level;


import java.util.ArrayList;

/**
 * Класс, реализующий сущность "Коридор"
 */
public class Passage {
    private Coordinates from;
    private Coordinates to;
    private int startRoomNumber;
    private int finishRoomNumber;
    private ArrayList<Coordinates> coordinates = new ArrayList<>();

    Passage(Coordinates from, Coordinates to, int startRoomNumber, int finishRoomNumber) {
        this.from = from;
        this.to = to;
        this.startRoomNumber = startRoomNumber;
        this.finishRoomNumber = finishRoomNumber;
    }
    public ArrayList<Coordinates> getCoordinates() {
        return coordinates;
    }
    public void addCoordinates(Coordinates coordinates) {
        this.coordinates.add(coordinates);
    }

    public void setFinishRoomNumber(int finishRoomNumber) {
        this.finishRoomNumber = finishRoomNumber;
    }

    public void setStartRoomNumber(int startRoomNumber) {
        this.startRoomNumber = startRoomNumber;
    }

    public void setTo(Coordinates to) {
        this.to = to;
    }

    public void setFrom(Coordinates from) {
        this.from = from;
    }

    public Coordinates getFrom() {
        return from;
    }

    public Coordinates getTo() {
        return to;
    }

    public int getStartRoomNumber() {
        return startRoomNumber;
    }

    public int getFinishRoomNumber() {
        return finishRoomNumber;
    }
}
