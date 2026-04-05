package io.github.example.domain.service;

public enum MoveResult {
    SUCCESS,                    // Ход успешен
    HIT,                        // Атака
    HIT_DOOR_LOCKED,            // Дверь закрыта
    OUT_OF_BOUNDS,              // Попытка выйти за границы карты
    EXIT_REACHED,               // Игрок дошел до выхода (победа)
    NOT_WALKABLE,               // Нет возможности сходить на клетку
    GAME_STATUS_NOT_PLAYING     // Игра находится в другом статусе
}
