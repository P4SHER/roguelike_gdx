package presentation_lanterna;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import domain.entities.Enemy;
import domain.entities.Item;
import domain.level.Coordinates;
import domain.level.Level;
import domain.service.GameSession;

import java.util.Map;

/**
 * Рисует акторов поверх тайлов карты:
 * сначала предметы, потом враги, потом игрок (чтобы игрок был всегда виден).
 */
public class ActorsRenderer {

    private final TextGraphics graphics;
    private int TERMINAL_WIDTH;
    private int TERMINAL_HEIGHT;
    public ActorsRenderer(TextGraphics graphics, int TERMINAL_WIDTH, int TERMINAL_HEIGHT) {
        this.graphics = graphics;
        this.TERMINAL_WIDTH = TERMINAL_WIDTH/3;
        this.TERMINAL_HEIGHT = TERMINAL_HEIGHT/5;
    }

    public void render(GameSession session) {
        Level level = session.getCurrentLevel();
        renderItems(level);
        renderEnemies(level);
        renderPlayer(session);
    }

    private void renderItems(Level level) {
        for (Map.Entry<Coordinates, Item> entry : level.getItems().entrySet()) {
            Coordinates pos = entry.getKey();
            if (level.getTiles()[pos.getY()][pos.getX()].isVisible()) {
                graphics.setCharacter(pos.getX()+TERMINAL_WIDTH, pos.getY()+TERMINAL_HEIGHT,
                        new TextCharacter(
                                ColorScheme.ITEM_SYMBOL,
                                ColorScheme.ITEM_COLOR,
                                ColorScheme.BG
                        ));
            }
//            new TextCharacter(
//                    ColorScheme.ITEM_SYMBOL,
//                    ColorScheme.ITEM_COLOR,
//                    ColorScheme.BG
//            );
        }
    }

    private void renderEnemies(Level level) {
        for (Map.Entry<Coordinates, Enemy> entry
                : level.getEnemies().entrySet()) {

            Coordinates pos = entry.getKey();
            Enemy enemy = entry.getValue();
            char symbol = ColorScheme.enemySymbol(enemy.getType());
            var color = ColorScheme.enemyColor(enemy.getType());
            if (level.getTiles()[enemy.getCoordinates().getY()][enemy.getCoordinates().getX()].isVisible()) {
            graphics.setCharacter(enemy.getCoordinates().getX()+TERMINAL_WIDTH, enemy.getCoordinates().getY()+TERMINAL_HEIGHT,
                    new TextCharacter(symbol, color, ColorScheme.BG));
            }
        }

    }

    private void renderPlayer(GameSession session) {
        Coordinates pos = session.getPlayer().getCoordinates();

        graphics.setCharacter(pos.getX() + TERMINAL_WIDTH, pos.getY() + TERMINAL_HEIGHT,
                new TextCharacter(
                        ColorScheme.PLAYER_SYMBOL,
                        ColorScheme.PLAYER_COLOR,
                        ColorScheme.BG
                ));
    }
}
