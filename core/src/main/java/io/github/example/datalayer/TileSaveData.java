package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.SpaceType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TileSaveData {
    private final SpaceType type;
    private final boolean isVisited;
    private final boolean  isVisible;
    private final int x;
    private final int y;

    @JsonCreator
    public TileSaveData(
            @JsonProperty("type") SpaceType type,
            @JsonProperty("isVisited") boolean isVisited,
            @JsonProperty("isVisible") boolean isVisible,
            @JsonProperty("x") int x,
            @JsonProperty("y") int y){
        this.type = type;
        this.isVisited = isVisited;
        this.isVisible = isVisible;
        this.x = x;
        this.y = y;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public boolean isVisited() {
        return isVisited;
    }
    public SpaceType getType() {
        return type;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
