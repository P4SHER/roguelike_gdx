package io.github.example.domain.service;

public enum Direction {
    UP(0, 1),       // LibGDX: UP increases Y (screen coordinates)
    DOWN(0, -1),    // LibGDX: DOWN decreases Y
    LEFT(-1, 0),
    RIGHT(1, 0),
    NONE(0, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }
}
