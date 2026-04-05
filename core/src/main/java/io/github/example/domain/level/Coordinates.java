package io.github.example.domain.level;

/*
 * Пусть координаты (0;0) находятся в самом левом углу,
 * а координаты комнат X и Y соответсвенно будут их левым верхним углом
 */

import io.github.example.domain.service.Direction;

import java.util.Objects;

/**
 * Координаты объекта X и Y
 */
public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public Coordinates translate(Direction direction) {
        Coordinates newPos = new Coordinates(x, y);
        newPos.move(direction.dx(), direction.dy());
        return newPos;
    }

    /**
     * Метод для сравнения текущей координаты
     * @param o   объект с которым сравнивается.
     * @return если координаты x и y равны, то это один
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x && y == that.y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    /**
     * Метод сравнения двух координат
    */
    public static boolean equals(Coordinates pos1, Coordinates pos2) {
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY();
    }
}
