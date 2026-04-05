package io.github.example.domain.level;

/**
 * Сущность описывающая отдельную точку карты
 */
public class Tile {
    private SpaceType spaceType; // тип пространства(комната, коридор и тд)
    private boolean isVisited=false;
    private boolean isVisible=false;
    private Coordinates pos;

    public Tile(SpaceType spaceType) {
        this.spaceType = spaceType;
    }

    // Getters

    public Coordinates getPos() {
        return pos;
    }

    public SpaceType getSpaceType() {
        return spaceType;
    }
    public boolean isVisited() {
        return isVisited;
    }

    public boolean isVisible() {
        return isVisible;
    }
//     Setters
    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
    public void setSpaceType(SpaceType newSpaceType) {
        spaceType=newSpaceType;
    }

    public void setPos(Coordinates pos) {
        this.pos = pos;
    }
}
