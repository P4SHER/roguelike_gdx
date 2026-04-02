package presentation_lanterna;

import domain.service.Leaderboard;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import domain.service.GameRecord;

import java.util.List;

public class MenuLanternaExample {

    public enum MenuAction {
        NEW_GAME,
        CONTINUE,
        STATS,
        EXIT
    }

    public static MenuAction showMainMenu(Screen screen, int width, int height) throws Exception {
        String title = "ROGUELIKE";
        String[] items = {"НОВАЯ ИГРА", "ПРОДОЛЖИТЬ", "СТАТИСТИКА"};
        int selected = 0;

        TextGraphics g = screen.newTextGraphics();

        while (true) {
            screen.clear();

            int titleX = (width - title.length()) / 2;
            int titleY = height / 2 - 6;

            g.setForegroundColor(TextColor.ANSI.WHITE);
            g.putString(Math.max(0, titleX), Math.max(0, titleY), title);

            int startY = titleY + 2;
            for (int i = 0; i < items.length; i++) {
                String text = items[i];
                String line = (i == selected) ? ("> " + text + " <") : ("  " + text + "  ");

                int x = (width - line.length()) / 2;
                int y = startY + i;

                if (i == selected) {
                    g.setForegroundColor(TextColor.ANSI.BLACK);
                    g.setBackgroundColor(TextColor.ANSI.WHITE);
                } else {
                    g.setForegroundColor(TextColor.ANSI.WHITE);
                    g.setBackgroundColor(TextColor.ANSI.BLACK);
                }

                g.putString(Math.max(0, x), Math.max(0, y), line);
            }
            g.setForegroundColor(TextColor.ANSI.WHITE);
            g.setBackgroundColor(TextColor.ANSI.BLACK);
            String hint = "↑/↓ выбрать, Enter подтвердить";
            g.putString(Math.max(0, (width - hint.length()) / 2), Math.max(0, startY + items.length + 2), hint);

            screen.refresh();

            KeyStroke key = screen.readInput();
            if (key == null) continue;

            if (key.getKeyType() == KeyType.ArrowUp) {
                selected = (selected - 1 + items.length) % items.length;
            } else if (key.getKeyType() == KeyType.ArrowDown) {
                selected = (selected + 1) % items.length;
            } else if (key.getKeyType() == KeyType.Enter) {
                return switch (selected) {
                    case 0 -> MenuAction.NEW_GAME;
                    case 1 -> MenuAction.CONTINUE;
                    case 2 -> MenuAction.STATS;
                    default -> MenuAction.EXIT;
                };
            } else if (key.getKeyType() == KeyType.Escape) {
                return MenuAction.EXIT;
            }
        }
    }

    public static void showStatsScreen(Screen screen, int width, int height, Leaderboard leaderboard) throws Exception {
        TextGraphics g = screen.newTextGraphics();
        List<GameRecord> records = leaderboard.getAllRecords();
//        List<GameRecord> records = new ArrayList<>();
//        int id = 0;
//        while (true) {
//            GameRecord record = null;
//            if (leaderboard.getGameRecordById(id) == null) {
//                break;
//            }
//            record = leaderboard.getGameRecordById(id);
//            records.add(record);
//            id++;
//        }

        records.sort((a, b) -> Integer.compare(b.getNumberTreasures(), a.getNumberTreasures()));

        String header = String.format("%-5s | %-3s | %-5s | %-3s | %-5s | %-6s | %-5s | %-5s | %-5s",
                "Сокр", "Ур", "Враги", "Еда", "Эликс", "Свитки", "Урон+", "Урон-", "Шаги");
        String separator = "-".repeat(header.length());

        while (true) {
            screen.clear();

            String title = "СТАТИСТИКА ПРОХОЖДЕНИЙ";
            int titleX = (width - title.length()) / 2;
            int startY = 2;

            g.setForegroundColor(TextColor.ANSI.WHITE);
            g.putString(Math.max(0, titleX), startY, title);

            int tableX = (width - header.length()) / 2;
            g.putString(Math.max(0, tableX), startY + 2, header);
            g.putString(Math.max(0, tableX), startY + 3, separator);

            int rowY = startY + 4;
//            int maxRows = Math.min(records.size(), height - 8);

            int maxRows = Math.min(records.size(), 20);

            if (records.isEmpty()) {
                String emptyMsg = "История игр пока пуста...";
                g.putString(Math.max(0, (width - emptyMsg.length()) / 2), rowY, emptyMsg);
            } else {
                for (int i = 0; i < maxRows; i++) {
                    GameRecord r = records.get(i);
                    String row = String.format("%-5d | %-3d | %-5d | %-3d | %-5d | %-6d | %-5d | %-5d | %-5d",
                            r.getNumberTreasures(),
                            r.getNumberLevel(),
                            r.getNumberDefeatedEnemies(),
                            r.getNumberMealsEaten(),
                            r.getNumberElixirsConsumed(),
                            r.getNumberScrollsRead(),
                            r.getDamageDealt(),
                            r.getDamageTaken(),
                            r.getIncrementMoves()
                    );
                    g.putString(Math.max(0, tableX), rowY + i, row);
                }
            }

            String hint = "Esc - назад в меню";
            g.putString(Math.max(0, (width - hint.length()) / 2), height - 2, hint);

            screen.refresh();

            KeyStroke key = screen.readInput();
            if (key != null && key.getKeyType() == KeyType.Escape) {
                return;
            }
        }
    }

    public static void showGameOverScreen(Screen screen, int width, int height) throws Exception {
        TextGraphics g = screen.newTextGraphics();
        screen.clear();
        String[] gameOverArt = {
                "  _____          __  __ ______    ______      ________ _____  ",
                " / ____|   /\\   |  \\/  |  ____|  / __ \\ \\    / /  ____|  __ \\ ",
                "| |  __   /  \\  | \\  / | |__    | |  | \\ \\  / /| |__  | |__) |",
                "| | |_ | / /\\ \\ | |\\/| |  __|   | |  | |\\ \\/ / |  __| |  _  / ",
                "| |__| |/ ____ \\| |  | | |____  | |__| | \\  /  | |____| | \\ \\ ",
                " \\_____/_/    \\_\\_|  |_|______|  \\____/   \\/   |______|_|  \\_\\"
        };

        g.setForegroundColor(TextColor.ANSI.RED);
        int startY = height / 2 - gameOverArt.length / 2 - 2;

        for (int i = 0; i < gameOverArt.length; i++) {
            int startX = (width - gameOverArt[i].length()) / 2;
            g.putString(Math.max(0, startX), startY + i, gameOverArt[i]);
        }

        String hint = "Нажмите любую клавишу для выхода в меню...";
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(Math.max(0, (width - hint.length()) / 2), startY + gameOverArt.length + 4, hint);

        screen.refresh();
        while (true) {
            KeyStroke key = screen.readInput();
            if (key != null) {
                break;
            }
        }
    }
    public static void showVictoryScreen(Screen screen, int width, int height) throws Exception {
        TextGraphics g = screen.newTextGraphics();
        screen.clear();

        String[] victoryArt = {
                "╔══════════════════════════════════════╗",
                "║              VICTORY!                ║",
                "╠══════════════════════════════════════╣",
                "║            ROGUELIKE 1980            ║",
                "║                                      ║",
                "║    YOU HAVE CONQUERED THE DUNGEON    ║",
                "╚══════════════════════════════════════╝"
        };

        String[] lines = {
                "",
                "YOU ARE THE LEGEND OF THE DUNGEON!",
                "",
                "[Press any key to return to menu]"
        };

        int maxWidth = 0;
        for (String line : victoryArt) {
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }
        for (String line : lines) {
            if (line.length() > maxWidth) {
                maxWidth = line.length();
            }
        }

        int totalHeight = victoryArt.length + lines.length;
        int startX = Math.max(0, (width - maxWidth) / 2)-5;
        int startY = Math.max(0, (height - totalHeight) / 2)-5;

        g.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 0; i < victoryArt.length; i++) {
            g.putString(startX, startY + i, victoryArt[i]);
        }

        g.setForegroundColor(TextColor.ANSI.GREEN);
        int currentY = startY + victoryArt.length;
        for (String line : lines) {
            int x = Math.max(0, (width - line.length()) / 2)-5;
            if (!line.isEmpty()) {
                g.putString(x, currentY, line);
            }
            currentY++;
        }

        screen.refresh();

        while (true) {
            KeyStroke key = screen.readInput();
            if (key != null) break;
        }
    }
}


