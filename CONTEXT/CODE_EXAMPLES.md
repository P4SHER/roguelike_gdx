# Примеры кода и функционала - LibGDX Roguelike

## 1. Как работает главный цикл игры

### Точка входа в LibGDX:

```java
// Main.java - LibGDX Application Listener
public class Main extends Game {
    private GameService gameService;
    private PresentationLayer presentationLayer;

    @Override
    public void create() {
        // Инициализация
        gameService = new GameService();
        presentationLayer = new PresentationLayer(WIDTH, HEIGHT);
        
        // Первый экран - меню
        presentationLayer.setInitialScreen(new MenuScreen(...));
    }

    @Override
    public void render() {
        // Вызывается каждый фрейм (~60 раз в секунду)
        presentationLayer.render(deltaTime);  // ← Здесь вся логика игры
    }
}
```

### Новая игра:

```java
// GameService.startNewGame()
public void startNewGame() {
    // GameSessionGeneration создает первую сессию
    this.session = sessionGeneration.startNewGame();
    // session содержит:
    // - Player (начальные хар-ки)
    // - Level 1 (сгенерирована LevelGeneration)
    // - Leaderboard (пусто)
}
```

### Обработка хода игрока:

```java
// GameService.processPlayerAction(Direction direction)
public void processPlayerAction(Direction direction) {
    if (session.getState() != GameState.PLAYING) return;

    // 1. Игрок делает ход
    MoveResult actionSuccess = movementService.movePlayer(
        session, 
        direction, 
        combatService
    );
    
    if (actionSuccess == MoveResult.SUCCESS) {
        session.getRecord().incrementMoves();  // Счётчик ходов
    }

    // 2. Враги делают ход (только если игра ещё идёт)
    if (session.getState() == GameState.PLAYING) {
        enemyAiService.processAllEnemies(
            session, 
            combatService, 
            movementService
        );
        session.getRecord().incrementMoves();
    }
}
```

---

## 2. Как работает движение

### Перемещение игрока:

```java
// MovementService.movePlayer(GameSession session, Direction direction, CombatService combatService)
public MoveResult movePlayer(GameSession session, Direction direction, CombatService combatService) {
    Player player = session.getPlayer();
    Level level = session.getCurrentLevel();
    
    Coordinates currentPos = player.getCoordinates();
    Coordinates newPos = currentPos.translate(direction);  // Новая позиция
    
    // Проверка 1: Выход на новый уровень?
    if (level.isExit(newPos)) {
        session.transitionToNextLevel();
        player.setPosition(newPos);
        return MoveResult.EXIT_REACHED;  // ← Уровень ++
    }
    
    // Проверка 2: За границы карты?
    if (level.isNotRoomAndPassage(newPos)) {
        return MoveResult.OUT_OF_BOUNDS;  // ← Заблокировано
    }
    
    // Проверка 3: Враг на новой позиции?
    if (level.getEnemies().containsKey(newPos)) {
        Enemy enemy = level.getEnemyAtPos(newPos);
        CombatResult attackResult = combatService.attack(player, enemy, session);
        if (attackResult == CombatResult.KILL) {
            level.removeEnemy(newPos);
        }
        return MoveResult.HIT;  // ← Боевой контакт
    }
    
    // Проверка 4: Предмет на новой позиции? (автоподбор)
    if (level.getItems().containsKey(newPos)) {
        Item itemCopy = level.getItemCopyAtPos(newPos);
        if (pickUpItem(session, itemCopy)) {
            level.removeItem(newPos);
        }
    }
    
    // Успешное перемещение
    player.setPosition(newPos);
    return MoveResult.SUCCESS;
}
```

### ИИ врагов:

```java
// EnemyAiService.processAllEnemies()
public void processAllEnemies(GameSession session, CombatService combatService, MovementService movementService) {
    Level level = session.getCurrentLevel();
    
    // Копируем врагов (чтобы не изменять коллекцию во время итерации)
    Map<Coordinates, Enemy> newEnemies = new HashMap<>(level.getEnemies());
    
    for(Enemy enemy : newEnemies.values()) {
        // Рассчитываем новую позицию
        Coordinates newPos = calculationNewCoordinates(session, enemy);
        
        // Переместить врага
        movementService.moveEnemy(session, enemy, newPos, combatService);
    }
}

private Coordinates calculationNewCoordinates(GameSession session, Enemy enemy) {
    // Враг видит игрока на расстоянии = враждебность?
    if (inAttackZone(session, enemy)) {
        // ДА → Двигаться в сторону игрока (А*)
        Direction directionMoveEnemy = determiningDirectionAttack(session, enemy);
        Coordinates newPos = new Coordinates(
            enemy.getCoordinates().getX() + directionMoveEnemy.dx(),
            enemy.getCoordinates().getY() + directionMoveEnemy.dy()
        );
        
        // Проверка, что шаг валиден
        if (!session.getCurrentLevel().isNotRoomAndPassage(newPos)) {
            return newPos;  // Шаг атаки
        } else {
            return definingCorrectNewRandomPos(session, enemy);  // Случайно если блокирован
        }
    } else {
        // НЕТ → Случайное движение
        return definingCorrectNewRandomPos(session, enemy);
    }
}

private boolean inAttackZone(GameSession session, Enemy enemy) {
    // Манхэттенское расстояние
    int dx = Math.abs(session.getPlayer().getCoordinates().getX() - enemy.getCoordinates().getX());
    int dy = Math.abs(session.getPlayer().getCoordinates().getY() - enemy.getCoordinates().getY());
    int distance = dx + dy;
    return distance <= enemy.getHostility();
}
```

---

## 3. Как работает боевая система

### Атака:

```java
// CombatService.attack(Character attacker, Character defender, GameSession session)
public CombatResult attack(Character attacker, Character defender, GameSession session) {
    // ЭТАП 1: Проверка на попадание (зависит от ловкости)
    if (!isHitSuccessful(attacker, defender)) {
        return CombatResult.MISS;  // Промах!
    }

    // ЭТАП 2: Расчет урона
    int damage = calculateDamage(attacker);

    // ЭТАП 3: Применение урона
    defender.takeDamage(damage);
    
    // Обновление статистики
    if (attacker instanceof Player) {
        session.addDamageDealt(damage);  // Счётчик урона от игрока
    } else if (defender instanceof Player) {
        session.addDamageTaken(damage);  // Счётчик урона по игроку
    }

    // Защищающийся мертв?
    if (!defender.isAlive()) {
        handleDeath(attacker, defender, session);
        return CombatResult.KILL;
    }

    return CombatResult.HIT;  // Нормальное попадание
}

private boolean isHitSuccessful(Character attacker, Character defender) {
    // Формула попадания
    int hitChance = 50;  // базовая вероятность
    hitChance += attacker.getStats().getAgility();  // +ловкость атакующего
    hitChance -= defender.getStats().getAgility() / 2;  // -ловкость защищающегося (0.5x)
    
    int roll = random.nextInt(100);  // 0-99
    return roll < hitChance;  // Если рандом < вероятность → попадание
}

private int calculateDamage(Character attacker) {
    int baseDamage = attacker.getStats().getStrength();
    
    // Если есть оружие, добавляем его урон
    if (attacker instanceof Player player && player.getCurrentWeapon() != null) {
        baseDamage += player.getCurrentWeapon().getStrengthBonus();
    }
    
    return baseDamage;
}

private void handleDeath(Character attacker, Character defender, GameSession session) {
    if (defender instanceof Enemy enemy) {
        // ВРАГ УБИТ
        session.getRecord().incrementNumberDefeatedEnemies();
        
        // Вероятность выпадения сокровищ
        int treasureDrop = calculateTreasureDrop(enemy);
        if (attacker instanceof Player player) {
            player.getBackpack().addTotalTreasureValue(treasureDrop);
        }
    } 
    else if (defender instanceof Player) {
        // ИГРОК УБИТ
        session.setState(GameState.GAME_OVER);
        
        // Обновить рекорд
        session.getRecord().setNumberTreasures(
            session.getPlayer().getBackpack().getTotalTreasureValue()
        );
        session.getLeaderboard().updateLastRecord(session.getRecord());
        
        // Сохранить в таблицу рекордов
        session.getSaveService().saveLeaderBoard(session);
    }
}
```

---

## 4. Как работает инвентарь и предметы

### Использование предмета:

```java
// InventoryService.useItem(int indexInBackpack, boolean dropWeapon)
public ItemType useItem(GameSession session, int indexInBackpack, boolean dropWeapon) {
    Item item = session.getPlayer().getBackpack().getAllItems().get(indexInBackpack);
    
    if (item == null) return ItemType.EMPTY;
    
    ItemType type = item.getType();
    
    switch (type) {
        case FOOD -> {
            // ЕДА: восстанавливает здоровье
            changingCharacteristic(session, item);
            item.lowerCountFoodInBackpack();  // Уменьшить счётчик
            session.getRecord().incrementMealsEaten();  // Статистика
            
            if (item.getCountFoodInBackpack() == 0) {
                session.getPlayer().getBackpack().removeItem(item);  // Удалить, если закончилась
            }
        }
        
        case SCROLL -> {
            // СВИТОК: ПОСТОЯННО повышает характеристику
            changingCharacteristic(session, item);  // Применить бонус
            session.getRecord().incrementScrollsRead();  // Статистика
            // СВИТОК НЕ УДАЛЯЕТСЯ, можно использовать много раз!
        }
        
        case ELIXIR -> {
            // ЭЛИКСИР: ВРЕМЕННО повышает характеристику
            changingCharacteristic(session, item);  // Применить бонус
            session.getRecord().incrementElixirsConsumed();  // Статистика
            // ЭЛИКСИР УДАЛЯЕТСЯ после использования
            session.getPlayer().getBackpack().removeItem(item);
        }
        
        case WEAPON -> {
            // ОРУЖИЕ: экипировка
            Item lastWeapon = session.getPlayer().getCurrentWeapon();
            
            if (dropWeapon) {
                // Положить старое оружие в рюкзак
                if (!session.getPlayer().getBackpack().addItem(lastWeapon)) {
                    // Если не влезает в рюкзак → drop на пол рядом с игроком
                    Coordinates pos = posDropWeapon(session);
                    session.getCurrentLevel().addItemInLevel(pos, lastWeapon.copyObject());
                }
                session.getPlayer().takeWeapon(null);  // Снять оружие
            } else {
                // Экипировать новое
                Item copyItem = item.copyObject();  // Создать копию
                session.getPlayer().takeWeapon(copyItem);
                session.getPlayer().getBackpack().removeItem(item);
                
                // Положить старое в рюкзак если было
                if (lastWeapon != null) {
                    if (!session.getPlayer().getBackpack().addItem(lastWeapon)) {
                        // drop на пол
                        ...
                    }
                }
            }
        }
    }
    
    return type;
}

private void changingCharacteristic(GameSession session, Item item) {
    Stats stats = session.getPlayer().getStats();
    
    // Применить все бонусы предмета
    if (item.getHealthBonus() > 0) {
        stats.heal(item.getHealthBonus());
    }
    
    if (item.getMaxHealthBonus() > 0) {
        stats.setMaxHealth(
            stats.getMaxHealth() + item.getMaxHealthBonus(),
            item.getType() == ItemType.ELIXIR  // true если временный
        );
    }
    
    if (item.getStrengthBonus() > 0) {
        stats.setStrength(
            stats.getStrength() + item.getStrengthBonus(),
            item.getType() == ItemType.ELIXIR
        );
    }
    
    if (item.getAgilityBonus() > 0) {
        stats.setAgility(
            stats.getAgility() + item.getAgilityBonus(),
            item.getType() == ItemType.ELIXIR
        );
    }
}
```

---

## 5. Как работает генерация уровня

### Создание 9 комнат:

```java
// LevelGeneration.generateSizes()
private void generateSizes(Level level) {
    // 3x3 сетка = 9 комнат
    for (int i = 0, count = 0; i < GameConfig.ROOMS_IN_HEIGHT; i++) {  // 3 строки
        for (int j = 0; j < GameConfig.ROOMS_IN_WIDTH; j++, count++) {  // 3 столбца
            
            // Случайный размер комнаты (8-15 тайлов)
            int widthRoom = ThreadLocalRandom.current().nextInt(
                GameConfig.MIN_ROOM_WIDTH, 
                GameConfig.MAX_ROOM_WIDTH
            );
            int heightRoom = ThreadLocalRandom.current().nextInt(
                GameConfig.MIN_ROOM_HEIGHT, 
                GameConfig.MAX_ROOM_HEIGHT
            );

            // Случайная позиция внутри своей ячейки сетки
            int left_range_coord = (j * GameConfig.REGION_WIDTH) / GameConfig.ROOMS_IN_WIDTH + 1;
            int right_range_coord = ((j + 1) * GameConfig.REGION_WIDTH) / GameConfig.ROOMS_IN_WIDTH - widthRoom - 1;
            int x_coord = ThreadLocalRandom.current().nextInt(left_range_coord, right_range_coord);

            int up_range_coord = i * GameConfig.REGION_HEIGHT / GameConfig.ROOMS_IN_HEIGHT + 1;
            int bottom_range_coord = (i + 1) * GameConfig.REGION_HEIGHT / GameConfig.ROOMS_IN_HEIGHT - heightRoom - 1;
            int y_coord = ThreadLocalRandom.current().nextInt(up_range_coord, bottom_range_coord);

            // Создать комнату на позиции
            level.getRooms()[count] = new Room(
                new Coordinates(x_coord, y_coord), 
                new Size(heightRoom, widthRoom)
            );
        }
    }
    
    // Выбрать случайную Start комнату
    int start = ThreadLocalRandom.current().nextInt(0, 9);
    level.getRooms()[start].setType("Start");
    level.setStartRoomIndex(start);
    
    // Выбрать случайную End комнату (другую)
    int end = ThreadLocalRandom.current().nextInt(0, 9);
    while (end == start) {
        end = ThreadLocalRandom.current().nextInt(0, 9);
    }
    level.getRooms()[end].setType("End");
    level.setEndRoomIndex(end);
}

// GameConfig константы:
// REGION_WIDTH = 200
// REGION_HEIGHT = 200
// ROOMS_IN_WIDTH = 3
// ROOMS_IN_HEIGHT = 3
// MIN_ROOM_WIDTH = 8
// MAX_ROOM_WIDTH = 15
// и т.д.
```

### Рисование стен и полов:

```java
// LevelGeneration.generateRooms()
private void generateRooms(Level level) {
    // 1. Инициализировать все клетки как "ничего"
    for (int i = 0; i < GameConfig.REGION_HEIGHT; i++) {
        for (int j = 0; j < GameConfig.REGION_WIDTH; j++) {
            level.getTiles()[i][j] = new Tile(SpaceType.Nothing);
        }
    }

    // 2. Для каждой комнаты
    for (int i = 0; i < level.getRooms().length; i++) {
        // Внешние границы = стены
        for (int k = level.getRooms()[i].getCoordinates().getY(); 
             k < level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight(); 
             k++) {
            for (int j = level.getRooms()[i].getCoordinates().getX(); 
                 j < level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth(); 
                 j++) {
                level.getTiles()[k][j].setSpaceType(SpaceType.Wall);
            }
        }
        
        // Внутри = пол (с отступом от стен)
        for (int k = level.getRooms()[i].getCoordinates().getY() + 1; 
             k < level.getRooms()[i].getCoordinates().getY() + level.getRooms()[i].getSize().getHeight() - 1; 
             k++) {
            for (int j = level.getRooms()[i].getCoordinates().getX() + 1; 
                 j < level.getRooms()[i].getCoordinates().getX() + level.getRooms()[i].getSize().getWidth() - 1; 
                 j++) {
                level.getTiles()[k][j].setSpaceType(SpaceType.Room);
            }
        }
    }
}
```

### Генерация врагов и предметов:

```java
// LevelGeneration.generateEnemies()
private void generateEnemies(Level level) {
    int numberOfEnemies = calculateNumberOfEnemies(level.getLevelNumber());
    
    for (int i = 0; i < numberOfEnemies; i++) {
        Coordinates randomPos = getRandomPositionInLevel(level, true);  // true = не в Start комнате
        
        Enemy enemy = new Enemy(
            getRandomEnemyType(),  // ZOMBIE, VAMPIRE, etc
            level
        );
        
        enemy.setPosition(randomPos);
        level.addEnemy(randomPos, enemy);
    }
}

// LevelGeneration.roomGenerateItems()
private void roomGenerateItems(Level level) {
    int numberOfItems = calculateNumberOfItems(level.getLevelNumber());
    
    for (int i = 0; i < numberOfItems; i++) {
        Coordinates randomPos = getRandomPositionInLevel(level, true);
        
        Item item = generateRandomItem(level.getLevelNumber());
        
        level.addItemInLevel(randomPos, item);
    }
}
```

---

## 6. Как работает сохранение

### Сохранение игры:

```java
// SaveService.saveGame(GameSession session)
public void saveGame(GameSession session) {
    try {
        // Преобразовать игровые объекты в DTO
        SaveData saveData = convertToSaveData(session);
        
        // Сериализовать в JSON
        JsonMapper.getInstance().writeValue(
            new File(SAVE_FILE),  // "src/datalayer/data/savegame.json"
            saveData
        );
        
        Logger.info("Игра сохранена в " + SAVE_FILE);
    } catch (IOException e) {
        Logger.error("Ошибка сохранения: " + e.getMessage());
    }
}

private SaveData convertToSaveData(GameSession session) {
    SaveData saveData = new SaveData();
    
    // Сохранить уровень
    saveData.setCurrentLevelNumber(session.getCurrentLevelNumber());
    
    // Преобразовать Level → LevelSaveData
    LevelSaveData levelSaveData = new LevelSaveData();
    levelSaveData.setTiles(convertTilesToSaveData(session.getCurrentLevel().getTiles()));
    levelSaveData.setRooms(convertRoomsToSaveData(session.getCurrentLevel().getRooms()));
    levelSaveData.setEnemies(convertEnemiesToSaveData(session.getCurrentLevel().getEnemies().values()));
    levelSaveData.setItems(convertItemsToSaveData(session.getCurrentLevel().getItems().values()));
    saveData.setLevelData(levelSaveData);
    
    // Преобразовать Player → PlayerSaveData
    PlayerSaveData playerData = new PlayerSaveData();
    playerData.setHealth(session.getPlayer().getStats().getCurrentHealth());
    playerData.setMaxHealth(session.getPlayer().getStats().getMaxHealth());
    playerData.setStrength(session.getPlayer().getStats().getStrength());
    playerData.setAgility(session.getPlayer().getStats().getAgility());
    playerData.setCoordinates(session.getPlayer().getCoordinates());
    playerData.setBackpack(convertBackpackToSaveData(session.getPlayer().getBackpack()));
    saveData.setPlayerData(playerData);
    
    // Сохранить статистику
    saveData.setRecord(convertRecordToSaveData(session.getRecord()));
    
    return saveData;
}
```

### Загрузка игры:

```java
// SaveService.loadGame()
public GameSession loadGame() {
    try {
        File saveFile = new File(SAVE_FILE);
        if (!saveFile.exists()) {
            return null;  // Нет сохранения
        }

        // Десериализовать JSON → SaveData
        SaveData saveData = JsonMapper.getInstance().readValue(
            saveFile, 
            SaveData.class
        );
        
        // Преобразовать DTO обратно в игровые объекты
        return convertToGameSession(saveData);
        
    } catch (IOException e) {
        Logger.error("Ошибка загрузки: " + e.getMessage());
        return null;
    }
}

private GameSession convertToGameSession(SaveData saveData) {
    GameSession session = new GameSession();
    
    // Восстановить Level
    Level level = convertLevelFromSaveData(saveData.getLevelData());
    session.setCurrentLevel(level);
    session.setCurrentLevelNumber(saveData.getCurrentLevelNumber());
    
    // Восстановить Player со всеми предметами и характеристиками
    Player player = convertPlayerFromSaveData(saveData.getPlayerData());
    session.setPlayer(player);
    
    // Восстановить статистику
    GameRecord record = convertRecordFromSaveData(saveData.getRecord());
    session.setRecord(record);
    
    // Восстановить таблицу рекордов
    // ...
    
    session.setState(GameState.PLAYING);
    
    return session;  // Полностью восстановленная сессия!
}
```

---

## 7. Характеристики и статистика

### Начальные параметры (EntityConfig):

```java
// EntityConfig.java
public class EntityConfig {
    // PLAYER
    public static final int PLAYER_START_HP = 100;
    public static final int PLAYER_START_STRENGTH = 10;
    public static final int PLAYER_START_AGILITY = 10;
    public static final char PLAYER_SYMBOL_FOR_RENDER = '@';
    
    // ENEMIES (зависят от уровня)
    public static final int ZOMBIE_HP = 50;
    public static final int ZOMBIE_STRENGTH = 8;
    public static final int ZOMBIE_AGILITY = 3;
    public static final int ZOMBIE_HOSTILITY = 5;
    
    public static final int VAMPIRE_HP = 60;
    public static final int VAMPIRE_STRENGTH = 10;
    public static final int VAMPIRE_AGILITY = 12;
    public static final int VAMPIRE_HOSTILITY = 7;
    
    // ... etc для всех 5 типов
}
```

### Отслеживание статистики:

```java
// GameRecord.java
public class GameRecord {
    private int currentLevel;
    private int numberTreasures;
    private int numberOfDefeatedEnemies;
    private int damageTaken;
    private int damageDealt;
    private int numberOfMoves;
    private int numberOfMissesInAttack;
    private int numberOfSuccessfulAttacks;
    private int numberOfCellsTraversed;
    private int mealsEaten;
    private int elixirsConsumed;
    private int scrollsRead;
    
    // Каждый счётчик увеличивается в нужный момент
    public void incrementMoves() { numberOfMoves++; }
    public void incrementNumberDefeatedEnemies() { numberOfDefeatedEnemies++; }
    public void incrementMealsEaten() { mealsEaten++; }
    // ... и т.д.
}
```

---

## 8. Характеристики персонажа

### Stats класс:

```java
// Stats.java
public class Stats {
    private int maxHealth;
    private int currentHealth;
    private int strength;
    private int agility;
    
    // Отслеживание ВРЕМЕННЫХ бонусов от эликсиров
    private int deleteMaxHealth = 0;   // Сколько точек будет удалено при отмене
    private int deleteStrength = 0;
    private int deleteAgility = 0;

    public void setStrength(int newStrength, boolean delete) {
        if (delete) {
            // Эликсир - отслеживаем для удаления после
            this.deleteStrength += (newStrength - strength);
        }
        this.strength = Math.max(newStrength, 0);
    }
    
    public void setMaxHealth(int newMaxHealth, boolean delete) {
        if (delete) {
            this.deleteMaxHealth += (newMaxHealth - maxHealth);
        }
        
        // При повышении макс здоровья, текущее тоже повышается
        if (currentHealth < newMaxHealth) {
            currentHealth += newMaxHealth - this.maxHealth;
        }
        
        this.maxHealth = newMaxHealth;
    }
}
```

---

## 9. Перечисления и типы

### Типы врагов:

```java
// EnemyType.java (перечисление)
public enum EnemyType {
    ZOMBIE('z'),        // Зеленый, низкая ловкость
    VAMPIRE('v'),       // Красный, высокая ловкость
    GHOST('g'),         // Белый, телепортирующийся
    OGRE('O'),          // Желтый, ходит на 2 клетки
    SNAKE_MAGE('s');    // Белый, диагональное движение
    
    private char symbol;
    
    EnemyType(char symbol) {
        this.symbol = symbol;
    }
}
```

### Типы предметов:

```java
// ItemType.java (перечисление)
public enum ItemType {
    FOOD,       // Восстановление здоровья
    SCROLL,     // Постоянный бонус
    ELIXIR,     // Временный бонус
    WEAPON,     // Экипировка
    TREASURE,   // Очки
    EMPTY       // Пусто
}

// ItemSubType.java - подтипы
public enum ItemSubType {
    APPLE, MUSHROOM,                    // FOOD
    SCROLL_STRENGTH, SCROLL_AGILITY, SCROLL_HEALTH,  // SCROLL
    ELIXIR_STRENGTH, ELIXIR_AGILITY, ELIXIR_HEALTH,  // ELIXIR
    SWORD, DAGGER, MACE,                // WEAPON
    COIN                                // TREASURE
}
```

---

## 10. Точки для расширения

### Добавить новый тип врага:

```java
// 1. Добавить в EnemyType.java
public enum EnemyType {
    // ... существующие
    GOLEM('G')  // ← новый враг
}

// 2. Добавить конфигурацию в EntityConfig.java
public static final int GOLEM_HP = 100;
public static final int GOLEM_STRENGTH = 15;
public static final int GOLEM_AGILITY = 2;
public static final int GOLEM_HOSTILITY = 3;

// 3. Добавить логику в EnemyAiService.java (если особое поведение)
private Coordinates calculationNewCoordinates(GameSession session, Enemy enemy) {
    if (enemy.getEnemyType() == EnemyType.GOLEM) {
        // Особое поведение для ГОЛЕМА
        return getGolemMovePattern(enemy);  // ← Своя логика
    }
    // ... остальные враги
}

// 4. Добавить генерацию в LevelGeneration.java
private void generateEnemies(Level level) {
    // ...
    EnemyType randomType = getRandomEnemyType();  // Должен включать GOLEM
}
```

### Добавить новый тип предмета:

```java
// 1. Добавить тип в ItemType.java
public enum ItemType {
    // ... существующие
    POTION      // ← новый предмет
}

// 2. Добавить обработку в InventoryService.useItem()
case POTION -> {
    // Логика использования зелья
    changingCharacteristic(session, item);
    // ...
}

// 3. Добавить генерацию в LevelGeneration.java
private Item generateRandomItem(int levelNumber) {
    ItemType type = randomItemType();  // Должен включать POTION
    return new Item(type, ...);
}
```

---

*Примеры показывают как различные компоненты взаимодействуют и логику за каждым функционалом*
