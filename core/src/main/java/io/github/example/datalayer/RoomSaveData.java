package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomSaveData {
    private final int x;
    private final int y;
    private final int height;
    private final int width;
    private final String type;

    @JsonCreator
    RoomSaveData(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("height") int height,
            @JsonProperty("width") int width,
            @JsonProperty("type") String type ){
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.type = type;
    }

    public String getType() {
        return type;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getY() {
        return y;
    }
    public int getX() {
        return x;
    }
}
