package io.github.example.presentation.input;

import com.badlogic.gdx.Input;
import io.github.example.domain.service.Direction;

/**
 * Перечисление для представления направлений в presentation слое.
 * Используется для передачи команд в domain слой.
 * 
 * NOTE: domain слой использует собственный Direction enum.
 * Это просто алиас для удобства использования в presentation.
 */
public class DirectionHelper {
    /**
     * Получает Direction по коду клавиши.
     */
    public static Direction fromKeyCode(int keyCode) {
        switch (keyCode) {
            case Input.Keys.W:
            case Input.Keys.UP:
                return Direction.UP;
            case Input.Keys.S:
            case Input.Keys.DOWN:
                return Direction.DOWN;
            case Input.Keys.A:
            case Input.Keys.LEFT:
                return Direction.LEFT;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                return Direction.RIGHT;
            default:
                return Direction.NONE;
        }
    }

    /**
     * Возвращает true, если это направление движения (не NONE).
     */
    public static boolean isMovement(Direction direction) {
        return direction != Direction.NONE;
    }
}

