package io.github.example.presentation_lanterna;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import io.github.example.domain.entities.Item;
import io.github.example.domain.entities.ItemType;
import io.github.example.domain.service.*;
import io.github.example.domain.unittest.Logger;

import javax.swing.*;
import java.util.List;

public class MainRenderer {

    private static final int TERMINAL_WIDTH = 200;
    private static final int TERMINAL_HEIGHT = 60;
    public static GameService gameService;

    public static void main(String[] args) throws Exception {

        // Для тестов
        Logger.init();

        gameService = new GameService();
        gameService.startNewGame();
//        gameService.getSession().getLeaderboard().getAllRecords().clear();
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        factory.setInitialTerminalSize(new TerminalSize(TERMINAL_WIDTH, TERMINAL_HEIGHT));
        Screen screen = new TerminalScreen(factory.createTerminal());
        screen.startScreen();
        screen.setCursorPosition(null);

        boolean appRunning = true;

        try {
            TerminalScreen terminalScreen = (TerminalScreen) screen;
            Terminal terminal = terminalScreen.getTerminal();
            while (appRunning) {
                if (terminal instanceof SwingTerminalFrame) {
                    SwingTerminalFrame swingFrame = (SwingTerminalFrame) terminal;

                    swingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    swingFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                            try {
                                if (gameService != null && gameService.getSession() != null) {
                                    gameService.getSaveService().saveGame(gameService.getSession());
                                    screen.stopScreen();
                                }
                            }
                            catch (Exception e) {

                            }

                            System.exit(0);
                        }
                    });
                }
                while (true) {
                    MenuLanternaExample.MenuAction action = MenuLanternaExample.showMainMenu(screen, TERMINAL_WIDTH, TERMINAL_HEIGHT);

                    if (action == MenuLanternaExample.MenuAction.STATS) {
                        MenuLanternaExample.showStatsScreen(screen, TERMINAL_WIDTH, TERMINAL_HEIGHT, gameService.getSession().getLeaderboard());
                        continue;
                    }

                    if (action == MenuLanternaExample.MenuAction.NEW_GAME) {
                        gameService.startNewGame();
                        break;
                    }

                    if (action == MenuLanternaExample.MenuAction.CONTINUE) {
                        if (!gameService.loadSaveGame()) gameService.startNewGame();
                        break;
                    }

                    if (action == MenuLanternaExample.MenuAction.EXIT) {
                        appRunning = false;
                        break;
                    }
                }

                if (!appRunning) {
                    break;
                }

                screen.clear();
                TurnRenderer renderer = new TurnRenderer(screen, TERMINAL_WIDTH, TERMINAL_HEIGHT);
                renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                screen.refresh();

                TextGraphics textGraphics = screen.newTextGraphics();

                while (true) {
                    screen.clear();
                    KeyStroke key = screen.readInput();
                    Logger.info("---------------------------Новый ход---------------------------");

                    if (isQuit(key)) {
//                        appRunning = false;
                        gameService.getSaveService().saveGame(gameService.getSession());
                        break;
                    }

                    Direction dir = toDirection(key);
                    if (dir != null) {
                        gameService.processPlayerAction(dir);
                        if (gameService.getSession().getState() == GameState.LEVEL_COMPLETE) {
                            gameService.transitionToNextLevel();
                        }

                        renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                        screen.refresh();

                    } else {
                        List<Item> items = backpackGet(key, gameService.getSession());
                        int weaponChoose=0;
                        if(key.getCharacter() != null && (key.getCharacter()== 'h' || key.getCharacter()=='H' || key.getCharacter()== 'Р'|| key.getCharacter()== 'р'))
                        {weaponChoose=1;}
                        if (items != null) {
                            renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                            renderItems(items, textGraphics);
                            screen.refresh();

                            key = screen.readInput();
                            renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                            int index = selectItem(key, items, textGraphics);

                            screen.refresh();
//                            System.out.println("index " + index);
                            //так он должен его в инвентарь складывать, если нажата h, а затем 0,
                            // то он должен убрать оружие в инвентарь(если нет, то положить на пол, если есть место)
                            //СЕЙЧАС если нажать h + 0, то будет вылетать ошибка, так как индекс = -1 и он будет использовать предмет с индексом -1
                            boolean dropWeapon=false;

                            if(weaponChoose==1 && key.getCharacter()=='0')
                            {
                                dropWeapon=true;
                                gameService.useItemFromBackpack(index, dropWeapon);
                                screen.clear();
                                renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                                screen.refresh();
                            }
                            else
                            {dropWeapon=false;}

                            if (index != -1) {
//                                System.out.println(dropWeapon);
                                // ПЕРЕМЕННАЯ ОТВЕЧАЮЩАЯ БУДЕМ МЫ ВЫБРАСЫВАТЬ ОРУЖИЕ ИЛИ НЕТ.
                                // как я понял. 0 это выбросить оружие, в 1-9 это оставить в рюкзаке. Ну или после 0-9 тебе дается выбор еще один
//                                boolean dropWeapon = false;
                                gameService.useItemFromBackpack(index, dropWeapon);
                                screen.clear();
                                renderer.render(gameService.getSession(), TERMINAL_WIDTH, TERMINAL_HEIGHT);
                                screen.refresh();
                            } else {
                                continue;
                            }
                        }
                    }

                    if (gameService.getSession().getState() == GameState.GAME_OVER) {
                        MenuLanternaExample.showGameOverScreen(screen, TERMINAL_WIDTH, TERMINAL_HEIGHT);
//                        appRunning = false;
                        break;
                    }

                    if (gameService.getSession().getState() == GameState.WIN) {
                        // ADD LOGIC
                        MenuLanternaExample.showVictoryScreen(screen, TERMINAL_WIDTH, TERMINAL_HEIGHT);
//                        appRunning=false;
                        break;
                    }
                }
            }

        } finally {
            screen.stopScreen();
        }

        Logger.close();
    }


    private static boolean isQuit(KeyStroke key) {
        if (key.getKeyType() == KeyType.Escape) return true;
        return key.getKeyType() == KeyType.Character && (key.getCharacter() == 'q' || key.getCharacter() == 'Q' || key.getCharacter() == 'Й' || key.getCharacter() == 'й');
    }

    private static Direction toDirection(KeyStroke key) {
        if (key.getKeyType() != KeyType.Character) return null;
        return switch (Character.toLowerCase(key.getCharacter())) {
            case 'w', 'ц', 'W', 'Ц' -> Direction.UP;
            case 's', 'ы', 'S', 'Ы' -> Direction.DOWN;
            case 'a', 'ф', 'A', 'Ф' -> Direction.LEFT;
            case 'd', 'в', 'D', 'В' -> Direction.RIGHT;

            default -> null;
        };
    }


    private static List<Item> backpackGet(KeyStroke key, GameSession gameSession) {
        if (key.getKeyType() != KeyType.Character) return null;
        return switch (key.getCharacter()) {
            case 'h', 'H', 'Р', 'р' ->
                    gameSession.getPlayer().getBackpack().getAllItems().stream().filter(item -> item.getType() == ItemType.WEAPON).toList();
            case 'j', 'J', 'О', 'о' ->
                    gameSession.getPlayer().getBackpack().getAllItems().stream().filter(item -> item.getType() == ItemType.FOOD).toList();
            case 'k', 'K', 'Л', 'л' ->
                    gameSession.getPlayer().getBackpack().getAllItems().stream().filter(item -> item.getType() == ItemType.ELIXIR).toList();
            case 'e', 'E', 'у', 'У' ->
                    gameSession.getPlayer().getBackpack().getAllItems().stream().filter(item -> item.getType() == ItemType.SCROLL).toList();
            case 'i', 'I', 'ш', 'Ш' -> gameSession.getPlayer().getBackpack().getAllItems();
            default -> null;
        };
    }

    private static void renderItems(List<Item> items, TextGraphics textGraphics) {
//        System.out.println("\n[" + "] ");
        int shift = 26;
        int add_count = 1;
        for (int i = 0; i < items.size(); i++) {
            add_count += i;
            textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "[" + (i+1) + "] " + items.get(i).getType() + " " + items.get(i).getName());
            add_count += 1;
            if (items.get(i).getType() == ItemType.WEAPON) {
                textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Strength bonus: " + String.valueOf(items.get(i).getStrengthBonus()));
                add_count += 1;
                textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Total damage: " + String.valueOf(items.get(i).getStrengthBonus() + gameService.getSession().getPlayer().getStats().getStrength()));
                add_count += 1;
            }

            if (items.get(i).getType() == ItemType.FOOD) {
                textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Healing: " + String.valueOf(items.get(i).getHealthRestoreFood()));
                add_count += 1;
            }
            if (items.get(i).getType() == ItemType.SCROLL) {
                textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Boosting stat: " + String.valueOf(items.get(i).getSubType()) + " +" + items.get(i).getStatBoost());
                add_count += 1;
            }
            if (items.get(i).getType() == ItemType.ELIXIR) {
                if (items.get(i).getHealthRestoreElixir() != 0) {
                    textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Boosting max health: " + String.valueOf(items.get(i).getSubType()) + " +" + items.get(i).getHealthRestoreElixir());
                } else
                    textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Boosting stat: " + String.valueOf(items.get(i).getSubType()) + " +" + items.get(i).getStatBoost());
                add_count += 1;
            }
            if (items.get(i).getType() == ItemType.TREASURE) {
                textGraphics.putString(TERMINAL_WIDTH / 2 + shift, 5 + add_count, "    -- Cost: " + String.valueOf(items.get(i).getCost()));
                add_count += 1;
            }
            add_count -= i;

        }
    }


    private static int selectItem(KeyStroke key, List<Item> items, TextGraphics textGraphics) {
        if (key.getKeyType() != KeyType.Character) return -1;
        char pressed = key.getCharacter();
//        System.out.println((pressed - '0'-1));
        //Добавить описание каждого предмета в зависимостиот его типа
        if (Character.isDigit(pressed) && pressed - '0'-1 >= 0 && pressed - '0'-1 < items.size()) {

            return (pressed - '0'-1);
        }
//        if(Character.isDigit(pressed) && pressed - '0' == 0)
//        {
//            return pressed - '0';
//        }
        return -1;
    }


}
