package io.github.example.domain.level;

import  io.github.example.domain.entities.*;
import io.github.example.domain.service.GameConfig;
import io.github.example.domain.service.GameSession;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class LevelGeneration {

    public Level generateLevel(GameSession session) {
        Level level = new Level(session);
        generate(level, session.getPlayer());

        return level;
    }

    private void generate(Level level, Player player) {
        generateSizes(level);
        generateRooms(level);
        generatePassages(level);
        generateEnemies(level);
        roomGenerateItems(level);
        setStartPosPlayer(level, player);
    }

    //метод генерации комнат
    private void generateSizes(Level level) {

        for (int i = 0, count = 0; i < GameConfig.ROOMS_IN_HEIGHT; i++) {
            for (int j = 0; j < GameConfig.ROOMS_IN_WIDTH; j++, count++) {

                int widthRoom = ThreadLocalRandom.current().nextInt(GameConfig.MIN_ROOM_WIDTH, GameConfig.MAX_ROOM_WIDTH);
                int heightRoom = ThreadLocalRandom.current().nextInt(GameConfig.MIN_ROOM_HEIGHT, GameConfig.MAX_ROOM_HEIGHT);

                int left_range_coord = (j * GameConfig.REGION_WIDTH) / GameConfig.ROOMS_IN_WIDTH + 1;
                int right_range_coord = ((j + 1) * GameConfig.REGION_WIDTH) / GameConfig.ROOMS_IN_WIDTH - widthRoom - 1;

                int x_coord = ThreadLocalRandom.current().nextInt(left_range_coord, right_range_coord);

                int up_range_coord = i * GameConfig.REGION_HEIGHT / GameConfig.ROOMS_IN_HEIGHT + 1;
                int bottom_range_coord = (i + 1) * GameConfig.REGION_HEIGHT / GameConfig.ROOMS_IN_HEIGHT - heightRoom - 1;
                int y_coord = ThreadLocalRandom.current().nextInt(up_range_coord, bottom_range_coord);

                level.getRooms()[count] = new Room(new Coordinates(x_coord, y_coord), new Size(heightRoom, widthRoom));
            }
        }
        int start = ThreadLocalRandom.current().nextInt(0, GameConfig.ROOMS_IN_HEIGHT*GameConfig.ROOMS_IN_WIDTH);
        level.getRooms()[start].setType("Start");
        level.setStartRoomIndex(start);
        int end = ThreadLocalRandom.current().nextInt(0, GameConfig.ROOMS_IN_HEIGHT*GameConfig.ROOMS_IN_WIDTH);
        while (end == start) {
            end = ThreadLocalRandom.current().nextInt(0, GameConfig.ROOMS_IN_HEIGHT*GameConfig.ROOMS_IN_WIDTH);
        }
        level.getRooms()[end].setType("End");
        level.setEndRoomIndex(end);
    }


    private void generateRooms(Level level) {
        for (int i = 0; i < GameConfig.REGION_HEIGHT; i++) {
            for (int j = 0; j < GameConfig.REGION_WIDTH; j++) {
                level.getTiles()[i][j] = new Tile(SpaceType.Nothing);
            }
        }

        for (int i = 0; i < level.getRooms().length; i++) {
            for (int k = level.getRooms()[i].getCoordinates().getY(); k < level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight(); k++) {
                for (int j = level.getRooms()[i].getCoordinates().getX(); j < level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth(); j++) {
                    level.getTiles()[k][j].setSpaceType(SpaceType.Wall);
                }
            }
            for (int k = level.getRooms()[i].getCoordinates().getY() + 1; k < level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight() - 1; k++) {
                for (int j = level.getRooms()[i].getCoordinates().getX() + 1; j < level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth() - 1; j++) {
                    level.getTiles()[k][j].setSpaceType(SpaceType.Room);
                }
            }
            int yEnd= level.getRooms()[level.getEndRoomIndex()].getCoordinates().getY() + level.getRooms()[level.getEndRoomIndex()].getSize().getHeight() / 2;
            int xEnd = level.getRooms()[level.getEndRoomIndex()].getCoordinates().getX() + level.getRooms()[level.getEndRoomIndex()].getSize().getWidth() / 2;
            level.getTiles()[yEnd][xEnd].setSpaceType(SpaceType.Exit);
            level.setExitCoordinates(new Coordinates(xEnd, yEnd));
        }
    }


    private void generatePassages(Level level) {
        Edge edge = new Edge(GameConfig.ROOMS_IN_HEIGHT, GameConfig.ROOMS_IN_WIDTH);
        edge.removeRandEdge();

        for (int i : edge.graph.keySet()) {
            if (!edge.graph.containsKey(i)) continue;

            for (int neighbor : edge.graph.get(i)) {
                if (i >= neighbor) {
                    continue;
                }

                if (i / GameConfig.ROOMS_IN_WIDTH == neighbor / GameConfig.ROOMS_IN_WIDTH) {
                    createHorizontalPassage(level, level.getRooms()[i], level.getRooms()[neighbor],i,neighbor);


                } else if (i % GameConfig.ROOMS_IN_HEIGHT == neighbor % GameConfig.ROOMS_IN_HEIGHT) {
                    createVerticalPassage(level, level.getRooms()[i], level.getRooms()[neighbor]);
                }
            }
        }
    }

    private void createHorizontalPassage(Level level, Room room1, Room room2, int fromRoomIndex, int toRoomIndex) {
        int x1 = room1.getCoordinates().getX();
        int y1 = room1.getCoordinates().getY();
        int x2 = room2.getCoordinates().getX();
        int y2 = room2.getCoordinates().getY();

        int medium1 = (y1 + room1.getSize().getHeight() / 2);
        int medium2 = (y2 + room2.getSize().getHeight() / 2);
        int startX;
        int startY;
        int finishY;
        int distance;
        if (x2 - (x1 + room1.getSize().getWidth()) > 0) {
            startX = x1 + room1.getSize().getWidth();
            startY = medium1;
            finishY = medium2;
            distance = x2 - (x1 + room1.getSize().getWidth());
        } else {
            startX = x2 + room2.getSize().getWidth();
            startY = medium2;
            finishY = medium1;
            distance = x1 - (x2 + room2.getSize().getWidth());
        }

        if (medium2 == medium1) {
            Passage passage = new Passage(new Coordinates(startX,medium1),new Coordinates(startX+distance-1,medium1),fromRoomIndex,toRoomIndex);
            level.getTiles()[medium1][startX - 1].setSpaceType(SpaceType.Door);
            for (int i = startX; i < startX + distance; i++) {
                level.getTiles()[medium1][i].setSpaceType(SpaceType.Passage);
                passage.addCoordinates(new Coordinates(i,medium1));
            }
            level.getPassages().add(passage);
            level.getTiles()[medium1][startX + distance].setSpaceType(SpaceType.Door);
        } else {
            int xMid;
            Passage passage = new Passage(new Coordinates(startX,startY),new Coordinates(startX+distance-1,finishY),fromRoomIndex,toRoomIndex);
            level.getTiles()[startY][startX - 1].setSpaceType(SpaceType.Door);
            for (xMid = startX; xMid < startX + distance / 2; xMid++) {
                level.getTiles()[startY][xMid].setSpaceType(SpaceType.Passage);
                passage.addCoordinates(new Coordinates(xMid,startY));
            }
            for (int i = min(medium1, medium2); i <= max(medium2, medium1); i++) {
                level.getTiles()[i][xMid].setSpaceType(SpaceType.Passage);
                passage.addCoordinates(new Coordinates(xMid,i));
            }
            for (int i = xMid; i < startX + distance; i++) {
                level.getTiles()[finishY][i].setSpaceType(SpaceType.Passage);
                passage.addCoordinates(new Coordinates(i,finishY));
            }
            level.getTiles()[finishY][startX + distance].setSpaceType(SpaceType.Door);
            level.getPassages().add(passage);
        }
    }

    private void createVerticalPassage(Level level, Room room1, Room room2) {
        int x1 = room1.getCoordinates().getX();
        int y1 = room1.getCoordinates().getY();
        int x2 = room2.getCoordinates().getX();
        int y2 = room2.getCoordinates().getY();

        // Вычисляем горизонтальные середины комнат (координата X)
        int medium1 = (x1 + room1.getSize().getWidth() / 2);
        int medium2 = (x2 + room2.getSize().getWidth() / 2);

        int startY;
        int startX;
        int finishX;
        int distance;

        // Определяем, какая комната выше, а какая ниже
        if (y2 - (y1 + room1.getSize().getHeight()) > 0) {
            startY = y1 + room1.getSize().getHeight();
            startX = medium1;
            finishX = medium2;
            distance = y2 - (y1 + room1.getSize().getHeight());
        } else {
            startY = y2 + room2.getSize().getHeight();
            startX = medium2;
            finishX = medium1;
            distance = y1 - (y2 + room2.getSize().getHeight());
        }

        if (medium2 == medium1) {
            level.getTiles()[startY - 1][medium1].setSpaceType(SpaceType.Door);
            for (int i = startY; i < startY + distance; i++) {
                level.getTiles()[i][startX].setSpaceType(SpaceType.Passage);
            }
            level.getTiles()[startY + distance][medium1].setSpaceType(SpaceType.Door);

        } else {
            int yMid;
            level.getTiles()[startY - 1][medium1].setSpaceType(SpaceType.Door);
            for (yMid = startY; yMid < startY + distance / 2; yMid++) {
                level.getTiles()[yMid][startX].setSpaceType(SpaceType.Passage);
            }
            for (int i = min(medium1, medium2); i <= max(medium1, medium2); i++) {
                level.getTiles()[yMid][i].setSpaceType(SpaceType.Passage);
            }
            for (int i = yMid; i < startY + distance; i++) {
                level.getTiles()[i][finishX].setSpaceType(SpaceType.Passage);
            }
            level.getTiles()[startY + distance][medium2].setSpaceType(SpaceType.Door);
        }
    }

    private void generateEnemies(Level level) {
        for (int i = 0; i < level.getRooms().length; i++) {
            if (i == level.getStartRoomIndex()) {
                continue;
            }
            int chanceOfSpawn = ThreadLocalRandom.current().nextInt(1, 11);
            if (chanceOfSpawn <= 8) {
                int max_monsters = ThreadLocalRandom.current().nextInt(1, GameConfig.MAX_MONSTERS_PER_ROOM + level.getLevel_num() / 5);
                for (int j = 0; j < max_monsters; j++) {
                    Enemy enemy = new Enemy(EnemyType.values()[ThreadLocalRandom.current().nextInt(EnemyType.values().length)], level.getLevel_num());
                    Coordinates[] coordinates =
                            {
                                    new Coordinates(ThreadLocalRandom.current().nextInt(level.getRooms()[i].getCoordinates().getX() + 3, level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth() - 3),
                                            ThreadLocalRandom.current().nextInt(level.getRooms()[i].getCoordinates().getY() + 2, level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight() - 2)
                                    ),
                                    new Coordinates(level.getRooms()[i].getCoordinates().getX() + 1, level.getRooms()[i].getCoordinates().getY() + 1),
                                    new Coordinates(level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth() - 2, level.getRooms()[i].getCoordinates().getY() + 1),
                                    new Coordinates(level.getRooms()[i].getCoordinates().getX() + 1, level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight() - 2),
                                    new Coordinates(level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth() - 2, level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight() - 2),
                            };


                    int randMovementIndex = ThreadLocalRandom.current().nextInt(0, coordinates.length);
                    while(level.getEnemies().containsKey(coordinates[randMovementIndex]) || level.getItems().containsKey(coordinates[randMovementIndex]) || level.getExitCoordinates().equals(coordinates[randMovementIndex]))
                    {
                        randMovementIndex = ThreadLocalRandom.current().nextInt(0, coordinates.length);
                    }
                    level.addEnemy(coordinates[randMovementIndex], enemy);
                }

            }
        }
    }

    private void roomGenerateItems(Level level) {
        for (int i = 0; i < level.getRooms().length; i++) {
            if(i != level.getStartRoomIndex())
            {
                Item item = generateRandItem();
                Coordinates coordinates = generateRandCoordsForEntity(level, i);
                item.setPos(coordinates);
                level.getItems().put(coordinates, item);
            }

        }
    }

    private Item generateRandItem() {
        ItemSubType subTypeRand = ItemSubType.values()[(ThreadLocalRandom.current().nextInt(1, ItemSubType.values().length))];
//        int itemChance = ThreadLocalRandom.current().nextInt(40, 81);
        int itemChance = ThreadLocalRandom.current().nextInt(0, 101);
        Item item = null;
        if (itemChance > 80) {
            item = new Item("tREASURE", ItemType.TREASURE, ItemSubType.NONE);
            item.setCost(ThreadLocalRandom.current().nextInt(10, 100));
        } else if (itemChance > 60) {
            item = new Item("eLIXIR test name", ItemType.ELIXIR, subTypeRand);
            if(subTypeRand == ItemSubType.MAX_HEALTH) {
                item.setHealthRestoreElixir(ThreadLocalRandom.current().nextInt(15, 30));
            }
            else {
                item.setStatBoost(ThreadLocalRandom.current().nextInt(5, 10));
            }
        } else if (itemChance > 40) {
            item = new Item("fOOD test name", ItemType.FOOD, ItemSubType.NONE);
            item.setHealthRestoreFood(
                    ThreadLocalRandom.current().nextInt(5, 20)
            );
        } else if (itemChance > 20) {
            item = new Item("sCROLL test name", ItemType.SCROLL, subTypeRand);
            if(subTypeRand == ItemSubType.MAX_HEALTH) {
                item.setHealthRestoreElixir(ThreadLocalRandom.current().nextInt(15, 30));
            }
            else{
                item.setStatBoost(ThreadLocalRandom.current().nextInt(5, 10));
            }
        } else if (itemChance >= 0) {
            item = new Item("wEAPON test name", ItemType.WEAPON, ItemSubType.STRENGTH);
            item.setStrengthBonus(ThreadLocalRandom.current().nextInt(5, 10));
        }
        return item;
    }
    //метод для случаных координат
    private Coordinates generateRandCoordsForEntity(Level level, int roomIndex){
        int randX = ThreadLocalRandom.current().nextInt(level.getRooms()[roomIndex].getCoordinates().getX()+2, level.getRooms()[roomIndex].getCoordinates().getX() + level.getRooms()[roomIndex].getSize().getWidth() - 2);
        int randY = ThreadLocalRandom.current().nextInt(level.getRooms()[roomIndex].getCoordinates().getY()+2,level.getRooms()[roomIndex].getCoordinates().getY() + level.getRooms()[roomIndex].getSize().getHeight() - 2);
        while(level.getEnemies().containsKey(new Coordinates(randX,randY)) || level.getItems().containsKey(new Coordinates(randX,randY)) || level.getExitCoordinates().equals(new Coordinates(randX,randY))) {
            randX = ThreadLocalRandom.current().nextInt(level.getRooms()[roomIndex].getCoordinates().getX()+2, level.getRooms()[roomIndex].getCoordinates().getX() + level.getRooms()[roomIndex].getSize().getWidth() - 2);
            randY = ThreadLocalRandom.current().nextInt(level.getRooms()[roomIndex].getCoordinates().getY()+2,level.getRooms()[roomIndex].getCoordinates().getY() + level.getRooms()[roomIndex].getSize().getHeight() - 2);
        }
        return new Coordinates(randX,randY);
    }

    private void setStartPosPlayer(Level level, Player player) {
        Coordinates pos = generateRandCoordsForEntity(level, level.getStartRoomIndex());
        player.setPosition(pos);
    }
}
