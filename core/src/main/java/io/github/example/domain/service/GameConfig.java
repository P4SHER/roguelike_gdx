package io.github.example.domain.service;

/**
 * Класс хранящий константы
 */
public class GameConfig {
    // Level
    public static final int MAX_LEVEL = 2;

    // Коэффициент роста здоровья врагов с каждым уровнем (1.1 = +10%)
    public static final double ENEMY_HP_SCALING_FACTOR = 1.1;
    // Коэффициент роста силы врагов
    public static final double ENEMY_STRENGTH_SCALING_FACTOR = 1.05;

    // Настройки лута и дропа
    public static final int BASE_TREASURE_DROP = 10;   // Базовое количество сокровищ с врага
    public static final double ITEM_SPAWN_CHANCE = 0.3; // Шанс появления предмета в комнате

    // Setting map
    public static final int COUNT_OF_ROOMS = 9;
    public static final int MAX_MONSTERS_PER_ROOM = 2;
    public static final int REGION_HEIGHT = 30;
    public static final int REGION_WIDTH = 50;
    public static final int ROOMS_IN_WIDTH = 3;
    public static final int ROOMS_IN_HEIGHT = 3;
    public static final int MIN_ROOM_HEIGHT = 5;
    public static final int MAX_ROOM_HEIGHT = 8;
    public static final int MIN_ROOM_WIDTH = 7;
    public static final int MAX_ROOM_WIDTH = 13;
}
