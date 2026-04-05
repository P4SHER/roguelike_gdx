package io.github.example.datalayer;

import io.github.example.domain.level.*;
import io.github.example.domain.service.GameSession;
import io.github.example.domain.service.GameRecord;
import io.github.example.domain.entities.*; // или Coordinates
import io.github.example.domain.service.Leaderboard;
import io.github.example.domain.unittest.Logger;
import io.github.example.domain.service.GameConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SaveService {
    private static final String SAVE_FILE = "src/datalayer/data/savegame.json";
    private static final String LEADERBOARD_FILE = "src/datalayer/data/leaderboard.json";

    /**
     * Сохраняет текущую сессию в файл
     */
    public void saveGame(GameSession session) {
        try {
            SaveData saveData = convertToSaveData(session);
            JsonMapper.getInstance().writeValue(new File(SAVE_FILE), saveData);
            Logger.info("Игра сохранена в " + SAVE_FILE);
        } catch (IOException e) {
            Logger.error("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static void clearJsonFile() {
        // Очищаем файл
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            writer.write("{}");
            Logger.info("Файл успешно очищен!");
        } catch (IOException e) {
            Logger.error("File not clear");
        }
    }

    /**
     * Загружает сессию из файла
     * @return загруженная сессия или null если файл не найден
     */
    public GameSession loadGame() {
        try {
            File saveFile = new File(SAVE_FILE);
            if (!saveFile.exists()) {
                return null;
            }

            SaveData saveData = JsonMapper.getInstance().readValue(saveFile, SaveData.class);
            return convertToGameSession(saveData);
        } catch (IOException e) {
            Logger.error("Ошибка загрузки: " + e.getMessage());
            return null;
        }
    }

    public void saveLeaderBoard(GameSession session) {
        try {
            LeaderboardSave saveLeaderboard = convertLeaderboardToLeaderboardSave(session.getLeaderboard());
            JsonMapper.getInstance().writeValue(new File(LEADERBOARD_FILE), saveLeaderboard);
            Logger.info("Игра сохранена в " + LEADERBOARD_FILE);
        } catch (IOException e) {
            Logger.error("Ошибка сохранения: " + e.getMessage());
        }
    }

    private LeaderboardSave convertLeaderboardToLeaderboardSave(Leaderboard leaderboard) {
        return new LeaderboardSave(
                leaderboard.getAllRecords() );
    }

    public Leaderboard loadLeaderboard() {
        try {
            File saveFile = new File(LEADERBOARD_FILE);
            if (!saveFile.exists()) {
                return new Leaderboard();
            }

            LeaderboardSave saveData = JsonMapper.getInstance().readValue(saveFile, LeaderboardSave.class);
            return convertToLeaderboard(saveData);
        } catch (IOException e) {
            Logger.error("Ошибка загрузки(" + LEADERBOARD_FILE + "): " + e.getMessage());
            return new Leaderboard();
        }
    }

    private Leaderboard convertToLeaderboard(LeaderboardSave saveData) {
        Leaderboard leaderboard = new Leaderboard();
        if (saveData.getLeaderboard() == null) return leaderboard;

        saveData.getLeaderboard()
                .forEach(leaderboard::addRecord);

        return leaderboard;
    }

    /**
     * Конвертирует игровую сессию в DTO для сохранения
     */
    private SaveData convertToSaveData(GameSession session) {

        // Конвертация игрока
        Logger.info("Convert Player to data");
        PlayerSaveData playerData = convertPlayerToPlayerSaveData(session.getPlayer());

        // Конвертация уровня
        Logger.info("Convert level to data");
        LevelSaveData levelData = convertLevelToSaveData(session.getCurrentLevel());

        // Конвертация таблицы рекордов
        Logger.info("Convert leaderboard to data");
        GameRecord leaderboard = session.getLeaderboard().getAllRecords().getLast();

        Logger.info("End save data");
        return new SaveData(
                playerData,
                levelData,
                session.getState(),
                session.getDifficulty(),
                leaderboard,
                session.getCurrentLevelNumber(),
                convertGameRecordToGameRecordSaveData(session.getRecord()),
                session.getNumberRound()
        );
    }

    private PlayerSaveData convertPlayerToPlayerSaveData(Player player) {
        return new PlayerSaveData(
                player.getStats().getMaxHealth(),
                player.getStats().getCurrentHealth(),
                player.getStats().getStrength(),
                player.getStats().getAgility(),
                player.getCoordinates().getX(),
                player.getCoordinates().getY(),
                convertItemToSaveData(player.getCurrentWeapon()),
                convertItemsToItemSaveData(player.getBackpack().getAllItems()),
                player.getBackpack().getCurrentItems(),
                player.getBackpack().getTotalTreasureValue()
        );
    }

    private GameRecordSaveData convertGameRecordToGameRecordSaveData(GameRecord record) {
        return new GameRecordSaveData(
                record.getNumberTreasures(),
                record.getNumberLevel(),
                record.getNumberDefeatedEnemies(),
                record.getNumberMealsEaten(),
                record.getNumberElixirsConsumed(),
                record.getNumberScrollsRead(),
                record.getDamageDealt(),
                record.getDamageTaken(),
                record.getIncrementMoves()
        );
    }

    private List<ItemSaveData> convertItemsToItemSaveData(List<Item> items) {
        return items.stream()
                .map(this::convertItemToSaveData)
                .toList();
    }

    /**
     * Конвертирует уровень в SaveData
     */
    private LevelSaveData convertLevelToSaveData(Level level) {
        // Сохраняем карту как массив строк (упрощённо)

        // Сохраняем клетки карты
        Tile[][] allTiles;
        allTiles = level.getTiles();
        List<TileSaveData> tiles = new ArrayList<>();

        for (int y = 0; y < GameConfig.REGION_HEIGHT; y++) {
            for (int x = 0; x < GameConfig.REGION_WIDTH; x++) {
                allTiles[y][x].setPos(new Coordinates(x, y));
                tiles.add(convertTileToTileSaveData(allTiles[y][x]));
            }
        }

        // Сохраняем врагов
        List<EnemySaveData> enemies = level.getAllEnemies().stream()
                .map(this::convertEnemyToSaveData)
                .collect(Collectors.toList());

        // Сохраняем предметы на полу
        List<ItemSaveData> items = level.getAllItems().stream()
                .map(this::convertItemToSaveData)
                .collect(Collectors.toList());

        // Сохраняем комнаты
        List<RoomSaveData> rooms = Arrays.stream(level.getRooms())
                .map(this::convertRoomToRoomSaveData)
                .toList();


        return new LevelSaveData(
                tiles,
                items,
                enemies,
                // Доп
                rooms,
                level.getExitCoordinates().getX(),
                level.getExitCoordinates().getY()
        );
    }

    private RoomSaveData convertRoomToRoomSaveData(Room room){
        return new RoomSaveData(
                room.getCoordinates().getX(),
                room.getCoordinates().getY(),
                room.getSize().getHeight(),
                room.getSize().getWidth(),
                room.getType()
        );
    }

    public TileSaveData convertTileToTileSaveData(Tile tile) {
        return new TileSaveData(
                tile.getSpaceType(),
                tile.isVisible(),
                tile.isVisited(),
                tile.getPos().getX(),
                tile.getPos().getY()
        );
    }

    private EnemySaveData convertEnemyToSaveData(Enemy enemy) {
//        Coordinates pos = enemy.getPos();
        return new EnemySaveData(
                enemy.getType(),
                enemy.getHostilityRange(),
                enemy.isVisible(),
                enemy.getCanAttack(),
                enemy.getStats().getMaxHealth(),
                enemy.getStats().getCurrentHealth(),
                enemy.getStats().getStrength(),
                enemy.getStats().getAgility(),
                enemy.getCoordinates().getX(),
                enemy.getCoordinates().getY(),
                convertItemToSaveData(enemy.getCurrentWeapon()),
                enemy.getSymbolForRendering()
        );
    }

    private ItemSaveData convertItemToSaveData(Item item) {
        if (item == null) return null;

        return new ItemSaveData(
                item.getName(),
                item.getType(),
                item.getSubType(),
                item.getHealthRestoreFood(),
                item.getHealthRestoreElixir(),
                item.getStatBoost(),
                item.getStrengthBonus(),
                item.getCost(),
                item.getCountFoodInBackpack(),
                item.getPos().getX(),
                item.getPos().getY()
        );
    }

    /**
     * Восстанавливает игровую сессию из DTO
     */
    private GameSession convertToGameSession(SaveData saveData) {
        GameSession session = new GameSession(saveData.getCurrentLevelNumber());

        // Восстановление игрока
        Player player = restorePlayer(saveData.getPlayer());
        session.setPlayer(player);

        // Восстановление уровня
        Level level = restoreLevel(saveData.getCurrentLevel(), session);
        session.setCurrentLevel(level);
        Logger.info("Level was writing");

        session.setState(saveData.getState());
        session.setDifficulty(saveData.getDifficulty());

        // Восстановление статистики
        session.setRecord(restoreGameRecord(saveData.getRecord()));
        Logger.info("Record was writing");

        // Восстановление таблицы рекордов
        session.setLeaderboard(loadLeaderboard());
        session.getLeaderboard().addRecord(restoreGameRecord(saveData.getRecord()));

        return session;
    }

    private GameRecord restoreGameRecord(GameRecordSaveData data) {
        GameRecord gameRecord = new GameRecord();

        gameRecord.setNumberTreasures(data.getNumberTreasures());
        gameRecord.setNumberLevel(data.getNumberLevel());
        gameRecord.setNumberDefeatedEnemies(data.getNumberDefeatedEnemies());
        gameRecord.setNumberMealsEaten(data.getNumberMealsEaten());
        gameRecord.setNumberElixirsConsumed(data.getNumberElixirsConsumed());
        gameRecord.setNumberScrollsRead(data.getNumberScrollsRead());
        gameRecord.setDamageDealt(data.getDamageDealt());
        gameRecord.setDamageTaken(data.getDamageTaken());
        gameRecord.setIncrementMoves(data.getIncrementMoves());

        return gameRecord;
    }

    private Player restorePlayer(PlayerSaveData data) {
        Player player = new Player();
        player.getStats().setMaxHealth(data.getMaxHealth(), false);
        player.getStats().setCurrentHealth(data.getCurrentHealth());
        player.getStats().setStrength(data.getStrength(), false);
        player.getStats().setAgility(data.getAgility(), false);
        player.setPosition(new Coordinates(data.getX(), data.getY()));

        player.getBackpack().setTotalTreasureValue(data.getTotalTreasureValue());
        player.getBackpack().setCurrentItems(data.getCurrentItems());
        player.getBackpack().setItems(
                data.getItems().stream()
                        .map(this::restoreItem)
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        if (data.getCurrentWeapon() != null) {
            player.takeWeapon(restoreItem(data.getCurrentWeapon()));
        }

        return player;
    }

    private Level restoreLevel(LevelSaveData data, GameSession session) {
        Level level = new Level(session);

        // Восстановление карты
        restoreAllTiles(level, data.getTiles());


        // Восстановление врагов
        data.getEnemies().stream()
                .map(this::restoreEnemy)
                .forEach(enemy -> level.setPositionEnemy(enemy, new Coordinates(0, 0), enemy.getCoordinates()));

        // Восстановление предметов на полу
        data.getItems().stream()
                .map(this::restoreItem)
                .forEach(item -> {
                    Coordinates pos = item.getPos();
                    level.addItemInLevel(pos, item);
                });

        // Восстановление комнат
        restoreRoom(level, data.getRooms());

        // Восстановление координаты
        level.setExitCoordinates(new Coordinates(data.getExitX(), data.getExitX()));

        return level;
    }

    private void restoreRoom(Level level, List<RoomSaveData> data) {
        int id = 0;
        for (RoomSaveData room : data) {
            level.getRooms()[id] = new Room(new Coordinates(room.getX(), room.getY()), new Size(room.getHeight(), room.getWidth()));
            level.getRooms()[id].setType(room.getType());
            id++;
        }
    }


    private void restoreAllTiles(Level level, List<TileSaveData> data) {

        int id = 0;
        for (int y = 0; y < GameConfig.REGION_HEIGHT; y++) {
            for (int x = 0; x < GameConfig.REGION_WIDTH; x++) {
                level.getTiles()[y][x] = new Tile(data.get(id).getType());
                level.getTiles()[y][x].setVisible(data.get(id).isVisible());
                Logger.error(data.get(id).isVisible() ? "true" : "false");
                level.getTiles()[y][x].setVisited(data.get(id).isVisited());

                id++;
            }
        }

        Logger.info("All tiles was writing in level");

    }

    private Enemy restoreEnemy(EnemySaveData data) {
        Enemy enemy = new Enemy(data.getType(), 1);

        enemy.setHostilityRange(data.getHostilityRange());
        enemy.setVisible(data.isVisible());
        enemy.setCanAttack(data.isCanAttack());
        enemy.getStats().setMaxHealth(data.getMaxHealth(), false);
        enemy.getStats().setCurrentHealth(data.getCurrentHealth());
        enemy.getStats().setStrength(data.getStrength(), false);
        enemy.getStats().setAgility(data.getAgility(), false);
        enemy.setPosition(new Coordinates(data.getX(), data.getY()));

        if (data.getCurrentWeapon() != null) {
            enemy.takeWeapon(restoreItem(data.getCurrentWeapon()));
        }

        return enemy;
    }

    private Item restoreItem(ItemSaveData data) {
        Item item = new Item(data.getName(), data.getType(), data.getSubType());

        item.setHealthRestoreFood(data.getHealthRestoreFood());
        item.setHealthRestoreElixir(data.getHealthRestoreElixir());
        item.setStatBoost(data.getStatBoost());
        item.setStrengthBonus(data.getStrengthBonus());
        item.setCost(data.getCost());
        item.setCountFoodInBackpack(data.getCountFoodInBackpack());
        item.setPos(new Coordinates(data.getX(), data.getY()));

        return item;
    }
}
