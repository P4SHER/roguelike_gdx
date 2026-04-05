package io.github.example.presentation_lanterna;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.SpaceType;
import io.github.example.domain.level.Tile;
import io.github.example.domain.service.GameConfig;
import io.github.example.domain.service.GameSession;

import java.util.ArrayList;
import java.util.List;

public class FogRenderer {
    private final TextGraphics textGraphics;
    //    private GameSession gameSession;
    private int visionRadius = 7;

    public FogRenderer(TextGraphics textGraphics) {
        this.textGraphics = textGraphics;
//        this.gameSession = gameSession;
    }

    public void render(GameSession gameSession, int TERMINAL_WIDTH, int TERMINAL_HEIGHT) {


        int flag = 1;
//        textGraphics.putString(TERMINAL_WIDTH / 2 + 10, TERMINAL_HEIGHT / 2 + i + 3, new String("Item x = " + entry.getKey().getX() + " y = " + entry.getKey().getY() + " name = " + entry.getValue().getName()));

//        if (gameSession.getCurrentLevel().getTiles()[gameSession.getPlayer().getCoordinates().getY()][gameSession.getPlayer().getCoordinates().getX()].getSpaceType() == SpaceType.Passage ||
//                gameSession.getCurrentLevel().getTiles()[gameSession.getPlayer().getCoordinates().getY()][gameSession.getPlayer().getCoordinates().getX()].getSpaceType() == SpaceType.Door) {
        findVisible(gameSession);

//        }
        if (gameSession.getCurrentLevel().getTiles()[gameSession.getPlayer().getCoordinates().getY()][gameSession.getPlayer().getCoordinates().getX()].getSpaceType() == SpaceType.Room) {
//            if (flag == 1) {
            setRoomVisibleVisited(gameSession);
//                flag = 0;
//            }
        }
//        else {
//            flag = 1;
//        }


        Tile[][] tiles = gameSession.getCurrentLevel().getTiles();
        int height = GameConfig.REGION_HEIGHT;
        int width = GameConfig.REGION_WIDTH;


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Tile tile = tiles[row][col];

                tiles[row][col].setVisible(true);


                if (tile == null) continue;

                renderTile(col, row, tile, TERMINAL_WIDTH, TERMINAL_HEIGHT);
            }
        }

        textGraphics.putString((int)(TERMINAL_WIDTH/2.8),3*TERMINAL_HEIGHT/4,"Level: " + gameSession.getCurrentLevelNumber() +"    Hits: " + "?" + "    Gold: "  + gameSession.getPlayer().getBackpack().getTotalTreasureValue() + "    Health: " + gameSession.getPlayer().getStats().getCurrentHealth() + "("+gameSession.getPlayer().getStats().getMaxHealth()+")");
        textGraphics.putString((int)(TERMINAL_WIDTH/2)-25,3*TERMINAL_HEIGHT/4 + 2, "Strength: "+gameSession.getPlayer().getStats().getStrength()+  "   Agility: " +   + gameSession.getPlayer().getStats().getAgility() + " Damage: " + gameSession.getPlayer().getDamage());


    }

    private void setRoomVisibleVisited(GameSession gameSession) {
        int x = gameSession.getPlayer().getCoordinates().getX();
        int y = gameSession.getPlayer().getCoordinates().getY();

//        System.out.println("x: "+ x+ " y: " + y);
        int room_floor = y / (GameConfig.REGION_HEIGHT / GameConfig.ROOMS_IN_HEIGHT) + 1;
        int roomNumber = GameConfig.ROOMS_IN_WIDTH * room_floor - (GameConfig.ROOMS_IN_HEIGHT - (x / (GameConfig.REGION_WIDTH / GameConfig.ROOMS_IN_WIDTH) + 1)) - 1;
//        System.out.println("этаж "+ room_floor+ " номер комнаты: " + roomNumber);
        int x_room = gameSession.getCurrentLevel().getRooms()[roomNumber].getCoordinates().getX();
        int y_room = gameSession.getCurrentLevel().getRooms()[roomNumber].getCoordinates().getY();
        int room_height = gameSession.getCurrentLevel().getRooms()[roomNumber].getSize().getHeight();
        int room_width = gameSession.getCurrentLevel().getRooms()[roomNumber].getSize().getWidth();
        System.out.println("x: " + x_room + " y: " + y_room + " width: " + (x_room + room_width) + " height: " + (y_room + room_height));
        for (int i = y_room; i < y_room + room_height; i++) {
            for (int j = x_room; j < x_room + room_width; j++) {
                gameSession.getCurrentLevel().getTiles()[i][j].setVisible(true);
                gameSession.getCurrentLevel().getTiles()[i][j].setVisited(true);
            }
        }

    }

    private void findVisible(GameSession gameSession) {
        int height = GameConfig.REGION_HEIGHT;
        int width = GameConfig.REGION_WIDTH;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                gameSession.getCurrentLevel().getTiles()[y][x].setVisible(false);
            }
        }
        if (gameSession.getCurrentLevel().getTiles()[gameSession.getPlayer().getCoordinates().getY()][gameSession.getPlayer().getCoordinates().getX()].getSpaceType() == SpaceType.Passage) {
            visionRadius = 1;
        } else {
            visionRadius = 7;
        }
        for (int i = 0; i < 360; i++) {


            double rad = Math.toRadians(i);
            int endX = (int) (gameSession.getPlayer().getCoordinates().getX() + Math.cos(rad) * visionRadius);
            int endY = (int) (gameSession.getPlayer().getCoordinates().getY() + Math.sin(rad) * visionRadius);
            List<Coordinates> line = getBresenham(gameSession.getPlayer().getCoordinates().getX(), gameSession.getPlayer().getCoordinates().getY(), endX, endY);
//            for (Coordinates p : line) {
//                if (p.getX() < 0 || p.getX() >= width || p.getY() < 0 || p.getY() >= height ) break;
//                if(gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() != SpaceType.Nothing) {
//                    gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].setVisible(true);
//                    gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].setVisited(true);
//                }
////                if(gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType()== SpaceType.Door &&
////                        gameSession.getCurrentLevel().getTiles()[(p+1).getY()][(p+1).getX()].getSpaceType()==SpaceType.Passage) {}
//                if (gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Wall) {
//                    break;
//                }
//            }
            boolean startsInDoor = gameSession.getCurrentLevel()
                    .getTiles()[gameSession.getPlayer().getCoordinates().getY()]
                    [gameSession.getPlayer().getCoordinates().getX()]
                    .getSpaceType() == SpaceType.Door;
            boolean startsInPassage = gameSession.getCurrentLevel()
                    .getTiles()[gameSession.getPlayer().getCoordinates().getY()]
                    [gameSession.getPlayer().getCoordinates().getX()]
                    .getSpaceType() == SpaceType.Passage;
            int count = 0;
//            if(gameSession.getCurrentLevel().getTiles()[line.get(0).getY()][line.get(0).getX()].getSpaceType()== SpaceType.Door){gameSession}
            for (int j = 0; j < line.size(); j++) {
                Coordinates p = line.get(j);
                Tile tile = gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()];
                if (p.getX() < 0 || p.getX() >= width || p.getY() < 0 || p.getY() >= height) break;
                if (startsInPassage && tile.getSpaceType() == SpaceType.Wall) {
                    break;
                }
                if (gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() != SpaceType.Nothing) {
                    tile.setVisible(true);
                }
                if (tile.getSpaceType() != SpaceType.Nothing &&tile.getSpaceType() != SpaceType.Door && tile.getSpaceType() != SpaceType.Wall && tile.getSpaceType() != SpaceType.Room && tile.getSpaceType() != SpaceType.Exit) {
//                    gameSession.getCurrentLevel().getTiles()[line.get(j).getY()][line.get(j).getX()].setVisible(true);
                    gameSession.getCurrentLevel().getTiles()[line.get(j).getY()][line.get(j).getX()].setVisited(true);
                }

//                if (j+1<line.size() && gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Door && gameSession.getCurrentLevel().getTiles()[line.get(j + 1).getY()][line.get(j + 1).getX()].getSpaceType() != SpaceType.Passage) {
//                    break;
//                }
                if (gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Door && j != 0) {
                    break;
                }
//                if(gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Door)
//                if(j+1 < line.size() && gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Passage && (gameSession.getCurrentLevel().getTiles()[line.get(j+1).getY()][line.get(j+1).getX()].getSpaceType() != SpaceType.Passage || gameSession.getCurrentLevel().getTiles()[line.get(j+1).getY()][line.get(j+1).getX()].getSpaceType() != SpaceType.Door))
//                {continue;}
//                if(j+1<line.size() && tile.getSpaceType() == SpaceType.Passage && !(gameSession.getCurrentLevel().getTiles()[line.get(j+1).getY()][line.get(j+1).getX()].getSpaceType()==SpaceType.Passage || gameSession.getCurrentLevel().getTiles()[line.get(j+1).getY()][line.get(j+1).getX()].getSpaceType()==SpaceType.Door)){break;}
                if (startsInDoor && tile.getSpaceType() == SpaceType.Passage) {
                    break;
                }
//                if(startsInPassage && !(tile.getSpaceType() == SpaceType.Door || tile.getSpaceType() == SpaceType.Passage)) {break;}

                if (tile.getSpaceType() == SpaceType.Door && j > 0) {
                    break;
                }
                if (gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Wall || gameSession.getCurrentLevel().getTiles()[p.getY()][p.getX()].getSpaceType() == SpaceType.Nothing) {
                    break;
                }
            }
            line.clear();
        }
    }

    private List<Coordinates> getBresenham(int x0, int y0, int x1, int y1) {
        List<Coordinates> coordinates = new ArrayList<>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;
        int err = dx - dy;

        int x = x0;
        int y = y0;

        while (true) {
            coordinates.add(new Coordinates(x, y));
            if (x == x1 && y == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return coordinates;
    }

    private void renderTile(int col, int row, Tile tile, int TERMINAL_WIDTH,  int TERMINAL_HEIGHT) {
        SpaceType type = tile.getSpaceType();
        char symbol = ColorScheme.symbolOf(type);
        var fg = ColorScheme.fgOf(type, tile.isVisible());
        if (tile.isVisible() || tile.isVisited()) {
            textGraphics.setCharacter((col + (int)(TERMINAL_WIDTH/(3))), row + TERMINAL_HEIGHT/5,
                    new TextCharacter(symbol, fg, ColorScheme.BG));
        }

//        } else if (tile.isVisited()) {
//            graphics.setCharacter(col, row,
//                    new TextCharacter(symbol, fg,new TextColor.RGB(128,128,128)));
//
//        }
    }


//    private void renderTile(int col, int row, Tile tile) {
//        SpaceType type = tile.getSpaceType();
//        char symbol = ColorScheme.symbolOf(type);
//        var fg = ColorScheme.fgOf(type);
//
//        graphics.setCharacter(col, row,
//                new TextCharacter(symbol, fg, ColorScheme.BG));
//    }
}
