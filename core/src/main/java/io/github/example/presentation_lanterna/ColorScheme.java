package io.github.example.presentation_lanterna;

import com.googlecode.lanterna.TextColor;
import io.github.example.domain.entities.EnemyType;
import io.github.example.domain.level.SpaceType;

/**
 * Цвета и символы для всех типов тайлов и акторов.
 */
public class ColorScheme {

    public static char symbolOf(SpaceType type) {
        return switch (type) {
            case Room -> '.';
            case Wall -> '#';
            case Passage -> '+';
            case Door -> '/';
            case Exit -> '>';
            case Nothing -> ' ';
            default -> ' ';
            // Акторы рисуются отдельно в ActorRenderer,
            // но под ними всегда пол комнаты
//            case Monster, Item, Player -> '1';
        };
    }

    public static TextColor fgOf(SpaceType type,boolean isVisited) {
        if(isVisited == false)
        {
           return new TextColor.RGB(78,87,84);
        }
        else{
        return switch (type) {
//            case Room -> new TextColor.RGB(100, 100, 100); // тёмно-серый пол
//            case Wall -> new TextColor.RGB(200, 200, 200); // светло-серая стена
            case Room -> TextColor.ANSI.WHITE_BRIGHT; // тёмно-серый пол
            case Wall -> TextColor.ANSI.WHITE_BRIGHT; // светло-серая стена
            case Passage -> TextColor.ANSI.YELLOW;
            case Door -> TextColor.ANSI.YELLOW_BRIGHT;
            case Exit -> TextColor.ANSI.GREEN_BRIGHT;
//            case Monster, Item, Player -> new TextColor.RGB(100, 100, 100);
            case Nothing -> TextColor.ANSI.BLACK;
            default -> TextColor.ANSI.BLACK;
        };}
    }

    public static char enemySymbol(EnemyType type) {
        return switch (type) {
            case ZOMBIE -> 'z';
            case VAMPIRE -> 'v';
            case GHOST -> 'g';
            case OGRE -> 'О';
            case SNAKE_MAGE -> 's';
        };
    }

    public static TextColor enemyColor(EnemyType type) {
        return switch (type) {
            case ZOMBIE -> TextColor.ANSI.GREEN;
            case VAMPIRE -> TextColor.ANSI.RED;
            case GHOST -> TextColor.ANSI.WHITE;
            case OGRE -> TextColor.ANSI.YELLOW;
            case SNAKE_MAGE -> TextColor.ANSI.WHITE;
        };
    }

    public static final char ITEM_SYMBOL = '!';
    public static final TextColor ITEM_COLOR = TextColor.ANSI.CYAN;
    public static final char PLAYER_SYMBOL = '@';
    public static final TextColor PLAYER_COLOR = TextColor.ANSI.WHITE_BRIGHT;
    public static final TextColor BG = TextColor.ANSI.BLACK;
}
