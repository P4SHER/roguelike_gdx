package io.github.example.domain.level;

import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Size;

/*
 * По идее следует сделать сделать так, чтобы все объекты наследовались
 * от GameObject, в котором лежат характеристики большинства объектов и обладают
 * может удалим потом
*/

/**
 * Базовый класс для большинства объектов, содержащий характеристики, такие как size(размер MxN), coordinates(текущие координаты)
 */

public class GameObject {
    private Coordinates coordinates;
    private Size size;
    public GameObject(Coordinates coordinates, Size size) {
        this.coordinates = coordinates;
        this.size = size;
    }
    public Coordinates getCoordinates() {return  coordinates;}
    public Size getSize() {return  size;}
}
