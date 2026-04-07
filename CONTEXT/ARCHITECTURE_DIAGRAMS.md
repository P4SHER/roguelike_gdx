# Архитектурные диаграммы - LibGDX Roguelike

## 1. Трёхслойная архитектура

```
┌─────────────────────────────────────────────────────────────────┐
│                       PRESENTATION LAYER                         │
│  (UI, Rendering, Input - не влияет на логику игры)             │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐  │
│  │   Main.java      │  │ PresentationLayer│  │ InputHandler │  │
│  │  (LibGDX main)   │  │  (Screens, HUD)  │  │ (Keyboard)   │  │
│  └──────────────────┘  └──────────────────┘  └──────────────┘  │
│           ▲                      │                    │          │
└─────────────┼──────────────────────┼────────────────────┼────────┘
              │                      │                    │
              │ вызывает             │                    │
              │ getSession()         │ render()           │
              │                      │ input events       │
┌─────────────┼──────────────────────┼────────────────────┼────────┐
│ DOMAIN LAYER (Pure Game Logic - независим от UI и сохранений)    │
│                                                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              GameService (Orchestrator)                   │  │
│  │ - startNewGame() - processPlayerAction() - loadSaveGame() │  │
│  └───────────────────────────────────────────────────────────┘  │
│    │        │          │              │           │              │
│    │        │          │              │           │              │
│    ▼        ▼          ▼              ▼           ▼              │
│ ┌─────┐ ┌────────┐ ┌─────────┐ ┌──────────┐ ┌─────────────┐    │
│ │ Movement  Combat   EnemyAI  Inventory   Session/      │    │
│ │ Service   Service  Service  Service     Level Gen     │    │
│ └─────┘ └────────┘ └─────────┘ └──────────┘ └─────────────┘    │
│   ▼        ▼          ▼              ▼           ▼              │
│ ┌────────────────────────────────────────────────────────┐      │
│ │           ENTITIES (Game Objects)                      │      │
│ │ Player, Enemy, Character, Stats, Item, Backpack       │      │
│ │ Level, Room, Tile, Coordinates                         │      │
│ └────────────────────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
              │
              │ session состояние и объекты
              │
┌─────────────┼──────────────────────────────────────────────────┐
│ DATA LAYER (Persistence - сохранение/загрузка в JSON)          │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │            SaveService (Main)                            │ │
│  │ - saveGame() - loadGame() - saveLeaderBoard()            │ │
│  └──────────────────────────────────────────────────────────┘ │
│    ▼                                          ▼                │
│  JsonMapper.java              SaveData + all DTOs              │
│  (Jackson serialization)       (ItemSaveData, etc)             │
│    │                                          │                │
│    ▼                                          ▼                │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │  JSON Files (Persistent Storage)                         │ │
│  │  - savegame.json                                         │ │
│  │  - leaderboard.json                                      │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Главный игровой цикл (Turn-based)

```
Main.render() - вызывается каждый фрейм
    ▼
PresentationLayer.render()
    ▼
┌─────────────────────────────────────────┐
│ 1. Обработка ввода                      │
│    InputHandler.processInput()          │
│    ├─ Arrow keys → Direction            │
│    ├─ Number keys → Use item            │
│    └─ Space → Wait turn                 │
└─────────────────────────────────────────┘
    ▼
┌─────────────────────────────────────────┐
│ 2. Игрок делает ход                     │
│    GameService.processPlayerAction(dir) │
│    └─ MovementService.movePlayer()      │
│       ├─ Проверка границ                │
│       ├─ Проверка врагов → бой          │
│       ├─ Подбор предметов               │
│       └─ Проверка выхода → уровень++    │
└─────────────────────────────────────────┘
    ▼
┌──────────────────────────────────────────┐
│ 3. Враги делают ход                      │
│    EnemyAiService.processAllEnemies()    │
│    ├─ Для каждого врага:                │
│    │  ├─ calculationNewCoordinates()    │
│    │  │  ├─ Если видит игрока          │
│    │  │  │  └─ attackDirection()        │
│    │  │  └─ Иначе randomDirection()     │
│    │  └─ MovementService.moveEnemy()    │
│    │     ├─ Проверка боя               │
│    │     └─ CombatService.attack()      │
│    │        └─ Урон игроку?             │
│    └─ Если игрок умер → GAME_OVER      │
└──────────────────────────────────────────┘
    ▼
┌──────────────────────────────────────────┐
│ 4. Обновление состояния                  │
│    GameSession обновлена                 │
│    Статистика увеличена                  │
│    (incrementMoves, addDamage, etc)      │
└──────────────────────────────────────────┘
    ▼
┌──────────────────────────────────────────┐
│ 5. Рендер сцены                          │
│    MainRenderer.renderLevel()            │
│    ├─ TileLayerRenderer (стены/полы)     │
│    ├─ ActorLayerRenderer (враги/игрок)   │
│    ├─ ItemLayerRenderer (предметы)       │
│    ├─ FogLayerRenderer (туман войны)     │
│    ├─ EffectsLayerRenderer (частицы)     │
│    └─ UILayerRenderer (HUD/интерфейс)    │
└──────────────────────────────────────────┘
    ▼
Экран обновлен → NEXT FRAME
```

---

## 3. Структура LevelGeneration (Генерация 9 комнат)

```
LevelGeneration.generateLevel()
    ▼
1. generateSizes() - создание комнат
   ┌───────────────────────────────────────┐
   │ Уровень разделен на 3x3 сетку (9 ячеек)
   │ ┌─────┬─────┬─────┐
   │ │ 0   │ 1   │ 2   │
   │ ├─────┼─────┼─────┤
   │ │ 3   │ 4   │ 5   │
   │ ├─────┼─────┼─────┤
   │ │ 6   │ 7   │ 8   │
   │ └─────┴─────┴─────┘
   │
   │ В каждой ячейке случайная комната:
   │ - Размер: 8-15 на 8-15
   │ - Позиция: внутри своей ячейки
   │ - Одна ячейка → Start
   │ - Одна ячейка → End
   └───────────────────────────────────────┘
    ▼
2. generateRooms() - рисование стен и полов
   ┌───────────────────────────────────────┐
   │ Для каждой комнаты:
   │ - Внешний слой → SpaceType.Wall
   │ - Внутренний слой → SpaceType.Room
   │
   │ Результат: 200x200 массив Tile[][]
   │ Каждый Tile содержит: Wall/Room/Passage/Nothing
   └───────────────────────────────────────┘
    ▼
3. generatePassages() - коридоры между комнатами
   ┌───────────────────────────────────────┐
   │ Рекурсивное деление + соединение:
   │ - Для каждой соседней пары комнат
   │ - Рисуем L-образный коридор
   │ - Гарантирует связный граф
   │
   │ Результат: SpaceType.Passage плитки
   │ соединяют все комнаты
   └───────────────────────────────────────┘
    ▼
4. generateEnemies() - враги на уровне
   ┌───────────────────────────────────────┐
   │ На каждом уровне N врагов:
   │ - Уровень 1: ~3 врага
   │ - Уровень 21: ~15+ врагов
   │
   │ Враги случайно размещены в комнатах
   │ (кроме Start комнаты)
   │
   │ Типы: ZOMBIE, VAMPIRE, GHOST, OGRE, SNAKE_MAGE
   │ - Каждый тип имеет свой паттерн AI
   │ - Враждебность 3-8 (расстояние атаки)
   └───────────────────────────────────────┘
    ▼
5. roomGenerateItems() - предметы на уровне
   ┌───────────────────────────────────────┐
   │ Случайные предметы в комнатах:
   │ - FOOD: восстановление
   │ - SCROLL: постоянный бонус
   │ - ELIXIR: временный бонус
   │ - WEAPON: экипировка
   │ - TREASURE: (выпадают с врагов, не спауны)
   │
   │ На уровнях >3: больше врагов, меньше еды
   └───────────────────────────────────────┘
    ▼
6. setStartPosPlayer() - позиция игрока
   ┌───────────────────────────────────────┐
   │ Игрок спауниться в Start комнате
   │ - Случайная позиция внутри комнаты
   │ - Врагов нет в Start комнате
   │ - Гарантирует безопасное начало
   └───────────────────────────────────────┘
```

---

## 4. Боевая система (Combat Flow)

```
Player попадает на врага
    ▼
MovementService.movePlayer() обнаруживает enemy
    ▼
CombatService.attack(player, enemy, session)
    ▼
┌──────────────────────────────────────────┐
│ Этап 1: Проверка попадания               │
│ isHitSuccessful(attacker, defender)      │
│                                          │
│ Вероятность = 50% + ловкость_атакующего  │
│            - 20% * ловкость_защищающегося│
│                                          │
│ Результат: HIT или MISS                  │
└──────────────────────────────────────────┘
    ▼
  Попадание?  ├─ НЕТ → return MISS
  (yes/no)    │
              └─ ДА ↓
    ┌──────────────────────────────────────────┐
    │ Этап 2: Расчет урона                     │
    │ calculateDamage(attacker)                │
    │                                          │
    │ Урон = Сила + Бонус_оружия              │
    │        + случайный разброс (0-2)        │
    │                                          │
    │ Если враг → базовый урон                 │
    │ Если игрок + оружие → +strength_bonus    │
    └──────────────────────────────────────────┘
    ▼
    ┌──────────────────────────────────────────┐
    │ Этап 3: Применение урона                 │
    │ defender.takeDamage(damage)              │
    │ → stats.setCurrentHealth(health - dmg)   │
    │                                          │
    │ Обновление статистики:                   │
    │ - addDamageDealt() если атакующий-игрок  │
    │ - addDamageTaken() если защищающийся-игрок
    └──────────────────────────────────────────┘
    ▼
  Защищающийся мертв?
  (health <= 0)  ├─ НЕТ → return HIT (бой продолжается)
                 │
                 └─ ДА ↓
    ┌──────────────────────────────────────────┐
    │ handleDeath(attacker, defender, session) │
    │                                          │
    │ Если враг убит:                          │
    │ ├─ incrementNumberDefeatedEnemies()      │
    │ ├─ calculateTreasureDrop(enemy)          │
    │ └─ player.backpack.addTotalTreasure()    │
    │                                          │
    │ Если игрок убит:                         │
    │ ├─ GameState = GAME_OVER                 │
    │ ├─ Leaderboard.updateLastRecord()        │
    │ ├─ SaveService.saveLeaderBoard()         │
    │ └─ SaveService.clearJsonFile()           │
    └──────────────────────────────────────────┘
    ▼
  return KILL (персонаж мертв)
```

---

## 5. Инвентарь и использование предметов

```
Player нажимает 1-9 для использования предмета
    ▼
InventoryService.useItem(indexInBackpack, dropWeapon)
    ▼
    ├─ Получить Item из Backpack
    │   ▼
    ├─ ItemType?
    │   │
    │   ├─ FOOD
    │   │  ├─ stats.heal(healthBonus)
    │   │  ├─ item.lowerCountFoodInBackpack()
    │   │  ├─ record.incrementMealsEaten()
    │   │  └─ Если count = 0 → removeItem()
    │   │
    │   ├─ SCROLL
    │   │  ├─ stats.setStrength() (postоянно)
    │   │  ├─ stats.setAgility()   (постоянно)
    │   │  ├─ stats.setMaxHealth()  (постоянно)
    │   │  ├─ record.incrementScrollsRead()
    │   │  └─ Свиток остается в инвентаре
    │   │
    │   ├─ ELIXIR
    │   │  ├─ stats.setStrength() (временно - deleteStrength)
    │   │  ├─ stats.setAgility()   (временно)
    │   │  ├─ stats.setMaxHealth()  (временно)
    │   │  ├─ record.incrementElixirsConsumed()
    │   │  └─ removeItem() (эликсир исчезает)
    │   │
    │   ├─ WEAPON
    │   │  ├─ Item lastWeapon = player.getCurrentWeapon()
    │   │  ├─ Если dropWeapon:
    │   │  │  ├─ Положить lastWeapon в рюкзак
    │   │  │  │  (если не влезает → drop на пол)
    │   │  │  ├─ player.takeWeapon(newWeapon)
    │   │  │  └─ removeItem(newWeapon)
    │   │  └─ Иначе:
    │   │     ├─ player.takeWeapon(newWeapon)
    │   │     └─ removeItem(newWeapon)
    │   │
    │   └─ TREASURE
    │      └─ Нельзя использовать (только на врагов)
    │
    └─ EnemyAiService.processAllEnemies() (враги ходят)
```

---

## 6. Сохранение/Загрузка (JSON)

```
GameSession (с динамическими объектами)
    ▼
SaveService.saveGame()
    ├─ convertToSaveData()
    │  │
    │  ├─ GameSession → SaveData
    │  ├─ Level → LevelSaveData
    │  │   ├─ Room[] → RoomSaveData[]
    │  │   ├─ Tile[][] → TileSaveData[][]
    │  │   ├─ enemies → EnemySaveData[]
    │  │   └─ items → ItemSaveData[]
    │  ├─ Player → PlayerSaveData
    │  │   ├─ Stats (health, strength, agility)
    │  │   ├─ Backpack → все items
    │  │   └─ currentWeapon
    │  └─ GameRecord (статистика попытки)
    │
    ├─ JsonMapper.writeValue()
    │  └─ Object mapper → JSON
    │
    └─ savegame.json (файл)

─────────────────────────────────────

JSON файл (persistent storage)
    ▼
SaveService.loadGame()
    ├─ JsonMapper.readValue()
    │  └─ JSON → SaveData объект
    │
    ├─ convertToGameSession()
    │  │
    │  ├─ SaveData → GameSession
    │  ├─ LevelSaveData → Level с врагами/предметами
    │  ├─ PlayerSaveData → Player с восстановленным инвентарем
    │  └─ GameRecord → текущая статистика
    │
    └─ return GameSession (полностью восстановлена)
```

---

## 7. Типы врагов и их поведение

```
ZOMBIE
├─ Здоровье: ████████░░ (высокое)
├─ Сила: ██░░░░░░░░ (средняя)
├─ Ловкость: █░░░░░░░░░ (низкая)
├─ Враждебность: 5 (среднее расстояние)
└─ Поведение: обычная атака

VAMPIRE
├─ Здоровье: ███████░░░ (высокое)
├─ Сила: ██░░░░░░░░ (средняя)
├─ Ловкость: ███████░░░ (высокая)
├─ Враждебность: 7 (дальнее расстояние)
├─ Первая атака: ПРОМАХ (всегда)
└─ Побочный эффект: -max_health от успешной атаки

GHOST
├─ Здоровье: ██░░░░░░░░ (низкое)
├─ Сила: ░░░░░░░░░░ (низкая)
├─ Ловкость: ███████░░░ (высокая)
├─ Враждебность: ░░░░░░░░░░ (низкая, почти не атакует)
├─ Поведение: телепортируется случайно
└─ Невидимость: по комнате периодически

OGRE
├─ Здоровье: █████████░ (очень высокое)
├─ Сила: █████████░ (очень высокая)
├─ Ловкость: █░░░░░░░░░ (низкая)
├─ Враждебность: 6 (среднее)
├─ Шаг: ходит на 2 клетки за раз
└─ Поведение: атака - пауза - контратака

SNAKE_MAGE
├─ Здоровье: ███░░░░░░░ (среднее)
├─ Сила: ████░░░░░░ (средняя)
├─ Ловкость: ████████░░ (очень высокая)
├─ Враждебность: 8 (самая высокая)
├─ Движение: по диагонали, меняет направление
└─ Побочный эффект: 30% шанс усыпить игрока на 1 ход
```

---

## 8. Иерархия классов

```
Character (абстрактный)
  ├─ Player
  │  └─ backpack: Backpack
  │
  └─ Enemy
     └─ enemyType: EnemyType
        └─ hostility: int

Backpack
  └─ items: List<Item>
     └─ totalTreasureValue: int

Item
  ├─ type: ItemType (FOOD, WEAPON, etc)
  ├─ subType: ItemSubType (APPLE, SWORD, etc)
  └─ bonuses (health, strength, agility)

Level
  ├─ tiles[][]: Tile (SpaceType.Wall/Room/Passage/Nothing)
  ├─ rooms[]: Room (9 комнат)
  ├─ enemies: Map<Coordinates, Enemy>
  └─ items: Map<Coordinates, Item>

Room
  ├─ coordinates: Coordinates
  ├─ size: Size
  └─ type: String (Start/End)

GameSession
  ├─ currentLevel: Level
  ├─ player: Player
  ├─ state: GameState
  ├─ record: GameRecord
  └─ leaderboard: Leaderboard

GameRecord
  ├─ currentLevel: int
  ├─ numberTreasures: int
  ├─ numberOfDefeatedEnemies: int
  ├─ damageDealt: int
  ├─ damageTaken: int
  └─ [много счётчиков для статистики]

Leaderboard
  └─ records: List<GameRecord>
```

---

## 9. Состояния игры

```
┌──────────────┐
│ MENU_SCREEN  │
│ (Main Menu)  │
└──────┬───────┘
       │ "New Game"
       ▼
┌──────────────────┐
│ PLAYING          │ ◄─────────────────┐
│ (Gameplay Loop)  │                   │
└──────┬───────────┘                   │
       │                               │
       ├─ Уровень < 21                │
       │  └─ Ход игрока/врагов        │
       │     └─ Не мертв?             │
       │        ├─ да →循環 (loop) ──┤
       │        └─ нет ↓
       │
       │  Уровень == 21 + exit        │
       │  └─ Игра завершена успешно   │
       │     └─ VICTORY               │
       │
       ├─ Player.health <= 0          │
       │  └─ GAME_OVER                │
       │
       └─ Pause нажат                 │
          └─ PAUSED                   │
             └─ Resume нажат → PLAYING

┌──────────────┐
│ GAME_OVER    │
│ (Player died)│
└──────┬───────┘
       │
       ├─ Leaderboard.updateLastRecord()
       ├─ SaveService.saveLeaderBoard()
       ├─ SaveService.clearJsonFile()
       │
       └─ Return to Menu
          └─ MENU_SCREEN

┌──────────────────┐
│ LEADERBOARD      │
│ (High scores)    │
└────────┬─────────┘
         │
         └─ Return to Menu
            └─ MENU_SCREEN
```

---

## 10. Карта памяти уровня

```
       0                              200
    ┌─────────────────────────────────┐
  0 │                                 │
    │  ╔═══╗         ╔═══╗            │
    │  ║ 0 ║         ║ 1 ║            │
    │  ╠═══╣-────────╠═══╣            │
    │  ║   ║ коридор ║   ║            │
    │  ╚═══╝         ╚═══╝            │
    │   │             │               │
    │   │ коридор     │ коридор       │
    │   │             │               │
    │  ╔═══╗         ╔═══╗            │
    │  ║ 3 ║─────────║ 4 ║            │
    │  ╠═══╣ коридор ╠═══╣            │
    │  ║ * ║         ║   ║            │
    │  ╚═══╝         ╚═══╝            │
    │                                 │
200 └─────────────────────────────────┘

* = Player текущая позиция

Каждая ячейка 3x3 сетки:
- Комната 8-15х8-15 тайлов
- Коридор 1 тайл толщиной
- Враги расположены в комнатах
- Предметы расположены в комнатах
```

---

*Диаграммы помогут понять внутреннюю архитектуру и поток данных в игре*
