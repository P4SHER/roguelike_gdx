package io.github.example.domain.entities;

public enum EnemyType {
    ZOMBIE('z'),
    VAMPIRE('v'),
    GHOST('g'),
    OGRE('O'),
    SNAKE_MAGE('s');

    public final char symbol;
    EnemyType(char symbol) { this.symbol = symbol; }
}
