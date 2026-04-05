package io.github.example.presentation.input;

import com.badlogic.gdx.Input;

/**
 * Перечисление направлений для движения.
 * 8 направлений для полноты.
 */
public enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    UP_LEFT(-1, 1),
    UP_RIGHT(1, 1),
    DOWN_LEFT(-1, -1),
    DOWN_RIGHT(1, -1),
    NONE(0, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Получает направление по коду клавиши.
     */
    public static Direction fromKeyCode(int keyCode) {
        switch (keyCode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                return UP;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                return DOWN;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                return LEFT;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                return RIGHT;
            default:
                return NONE;
        }
    }

    /**
     * Возвращает true, если это направление движения (не NONE).
     */
    public boolean isMovement() {
        return this != NONE;
    }
}
