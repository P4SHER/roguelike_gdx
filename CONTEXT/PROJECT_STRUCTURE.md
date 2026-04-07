# Структура проекта LibGDX Roguelike

## 📋 Краткое резюме

Это трёхслойная архитектура игры **LibGDX Roguelike** - пошагового подземелья на Java 17.

```
Main.java (LibGDX entry point)
    ├── Domain Layer (Бизнес-логика игры)
    ├── Data Layer (Сохранение/загрузка)
    └── Presentation Layer (Рендеринг и интерфейс)
```

---

## 🎮 DOMAIN LAYER - Бизнес-логика игры

**Путь:** `core/src/main/java/io/github/example/domain/`

Этот слой содержит **чистую логику игры**, независимую от рендеринга или сохранения.

### 1. **entities/** - Игровые сущности

#### Персонаж и враги:
- **`Character.java`** - Абстрактный базовый класс для всех персонажей
  - `stats` - объект `Stats` с характеристиками
  - `coordinates` - текущая позиция `Coordinates`
  - `currentWeapon` - экипированное оружие `Item`
  - `statusEffects` - список активных эффектов
  - Методы: `move()`, `takeDamage()`, `isAlive()`, `getDamage()`

- **`Player.java`** - Персонаж игрока (наследует `Character`)
  - `backpack` - рюкзак для предметов
  - Метод: `deleteBust()` - удаление временных бонусов эликсиров

- **`Enemy.java`** - Враг (наследует `Character`)
  - `enemyType` - тип врага (`ZOMBIE`, `VAMPIRE`, `GHOST`, `OGRE`, `SNAKE_MAGE`)
  - `hostility` - дистанция атаки

#### Характеристики:
- **`Stats.java`** - Объект характеристик персонажей
  - `maxHealth` - максимальное здоровье
  - `currentHealth` - текущее здоровье
  - `strength` - сила (урон)
  - `agility` - ловкость (уклонение/попадание)
  - `deleteXXX` - накопленные убытки от временных эффектов
  - Методы: `setStrength()`, `setAgility()`, `setMaxHealth()` (с отслеживанием убытков)

#### Предметы и инвентарь:
- **`Item.java`** - Предмет
  - `type` - тип: `FOOD`, `SCROLL`, `ELIXIR`, `WEAPON`, `TREASURE`
  - `subType` - подтип (для разных вариантов)
  - Бонусы: `healthBonus`, `maxHealthBonus`, `strengthBonus`, `agilityBonus`
  - `isPermanent()` - эликсиры исчезают, свитки остаются
  - Методы: `copyObject()`, `lowerCountFoodInBackpack()`

- **`Backpack.java`** - Рюкзак игрока
  - Хранит до 9 каждого типа предмета (кроме сокровищ)
  - `totalTreasureValue` - накопленные сокровища
  - Методы: `addItem()`, `removeItem()`, `getAllItems()`

#### Статус-эффекты:
- **`StatusEffect.java`** - Активный эффект (яд, огонь и т.д.)
- **`StatusEffectType.java`** - Тип эффекта (перечисление)

#### Конфигурация:
- **`EnemyType.java`** - 5 типов врагов с характеристиками
- **`ItemType.java`** - типы предметов
- **`ItemSubType.java`** - подтипы предметов
- **`EntityConfig.java`** - начальные параметры игрока

### 2. **level/** - Генерация и структура уровня

- **`LevelGeneration.java`** - **ГЕНЕРАЦИЯ УРОВНЕЙ** 🔑
  - Разделение на **9 комнат** (3x3 сетка)
  - Генерирует комнаты разных размеров в каждой ячейке
  - Генерирует коридоры между комнатами
  - Методы:
    - `generateLevel()` - точка входа
    - `generateSizes()` - создание комнат со случайными размерами
    - `generateRooms()` - заполнение плиток стеной/полом
    - `generatePassages()` - соединение комнат коридорами
    - `generateEnemies()` - расстановка врагов на уровне
    - `roomGenerateItems()` - расстановка предметов
    - `setStartPosPlayer()` - начальная позиция игрока

- **`Level.java`** - Текущий уровень
  - `tiles[][]` - сетка плиток (стена/пол/ничего)
  - `rooms[]` - массив из 9 комнат
  - `enemies` - Map координат на врагов
  - `items` - Map координат на предметы
  - Методы: `isExit()`, `isNotRoomAndPassage()`, `getEnemyAtPos()`, `getItemCopyAtPos()`

- **`Room.java`** - Одна комната
  - `coordinates` - верхний-левый угол
  - `size` - ширина и высота
  - `type` - "Start", "End" или обычная

- **`Tile.java`** - Одна клетка
  - `spaceType` - тип пространства (`Wall`, `Room`, `Passage`, `Nothing`)

- **`Coordinates.java`** - Координаты (X, Y)
- **`Size.java`** - Размеры (высота, ширина)
- **`SpaceType.java`** - Перечисление типов пространства
- **`Edge.java`** - граница комнаты
- **`Passage.java`** - коридор
- **`GameObject.java`** - базовый игровой объект

### 3. **service/** - Сервисы бизнес-логики

#### Главный управляющий сервис:
- **`GameService.java`** - **ГЛАВНЫЙ КЛАСС ЛОГИКИ** 🔑
  - Управляет текущей сессией
  - Содержит ссылки на все сервисы:
    - `MovementService` - перемещение
    - `CombatService` - боевая система
    - `EnemyAiService` - ИИ врагов
    - `InventoryService` - инвентарь
    - `SaveService` - сохранение/загрузка
  - **Ключевые методы:**
    - `startNewGame()` - новая игра
    - `processPlayerAction(Direction)` - ход игрока + враги
    - `useItemFromBackpack()` - использование предмета
    - `loadSaveGame()` - загрузка сохранения
    - `transitionToNextLevel()` - переход на следующий уровень

#### Движение и боевая система:
- **`MovementService.java`** - Перемещение персонажа
  - `movePlayer()` - обработка хода игрока
    - Проверка границ, врагов, выхода
    - Автоподбор предметов
    - Инициация боя при соприкосновении
  - `moveEnemy()` - перемещение врага

- **`CombatService.java`** - Боевая система
  - `attack()` - одна атака
    - Проверка попадания (зависит от ловкости)
    - Расчет урона
    - Применение урона, отслеживание статистики
  - `isHitSuccessful()` - вероятность попадания
  - `calculateDamage()` - расчет урона (сила + бонус оружия)
  - `calculateTreasureDrop()` - сокровища при убийстве
  - `handleDeath()` - обработка смерти

- **`EnemyAiService.java`** - ИИ врагов
  - `processAllEnemies()` - ход всех врагов
  - `inAttackZone()` - враг видит игрока на расстояние (враждебность)
  - `calculationNewCoordinates()` - рассчитать новую позицию
  - `determiningDirectionAttack()` - атаковать ближайшего к игроку
  - `definingCorrectNewRandomPos()` - случайное движение если не видит

#### Инвентарь:
- **`InventoryService.java`** - Управление инвентарем
  - `useItem()` - использовать предмет из рюкзака
    - FOOD: восстанавливает здоровье
    - SCROLL: постоянно повышает характеристику
    - ELIXIR: временно повышает характеристику
    - WEAPON: экипирует оружие
  - `pickUpItem()` - подобрать предмет со земли

#### Состояние игры:
- **`GameSession.java`** - Текущая игровая сессия
  - `currentLevel` - текущий уровень
  - `player` - персонаж игрока
  - `state` - состояние (`PLAYING`, `GAME_OVER`, `PAUSED`)
  - `record` - текущая статистика
  - `leaderboard` - таблица рекордов

- **`GameState.java`** - Перечисление состояний

- **`GameRecord.java`** - Статистика попытки
  - `treasures` - собрано сокровищ
  - `currentLevel` - достигнутый уровень
  - `enemiesDefeated` - убито врагов
  - `damageDealt` - урон нанесен
  - `damageTaken` - урон получен
  - Счётчики: ходы, съеденная еда, выпитые эликсиры и т.д.

- **`Leaderboard.java`** - Таблица рекордов
  - `records` - список всех попыток
  - `updateLastRecord()` - обновить последний рекорд
  - `addRecord()` - добавить новый рекорд
  - `getAllRecords()` - получить все рекорды

- **`GameSessionGeneration.java`** - Создание новой сессии
  - `startNewGame()` - инициализирует игрока и уровень 1

#### Вспомогательные классы:
- **`CombatResult.java`** - Результат атаки (`MISS`, `HIT`, `KILL`)
- **`MoveResult.java`** - Результат движения (`SUCCESS`, `HIT`, `EXIT_REACHED`, etc)
- **`Direction.java`** - Направление (`LEFT`, `UP`, `RIGHT`, `DOWN`)
- **`GameConfig.java`** - Конфигурация игры
  - `ROOMS_IN_WIDTH`, `ROOMS_IN_HEIGHT` - 3x3 комнаты
  - `REGION_WIDTH`, `REGION_HEIGHT` - размер уровня
  - `MIN/MAX_ROOM_WIDTH/HEIGHT` - размеры комнат

#### Юнит-тесты:
- **`Logger.java`** - логирование для отладки

---

## 💾 DATA LAYER - Сохранение и загрузка

**Путь:** `core/src/main/java/io/github/example/datalayer/`

Преобразует игровые объекты в JSON и обратно для сохранения/загрузки.

### Главный сервис:
- **`SaveService.java`** - **ТОЧКА ВХОДА** 🔑
  - `saveGame(GameSession)` - сохранить в `savegame.json`
  - `loadGame()` - загрузить из `savegame.json`
  - `saveLeaderBoard()` - сохранить рекорды в `leaderboard.json`
  - Содержит методы конвертации: `convertToSaveData()`, `convertToGameSession()`

### Data Transfer Objects (DTOs):
Структуры данных для сериализации в JSON:

- **`SaveData.java`** - корневой объект сохранения
  - содержит `GameSessionData`, уровни, статистику

- **`GameRecordSaveData.java`** - сохраненная статистика попытки
- **`LevelSaveData.java`** - сохраненный уровень
  - содержит `RoomSaveData`, плитки, враги, предметы

- **`RoomSaveData.java`** - сохраненная комната
- **`TileSaveData.java`** - сохраненная плитка
- **`PlayerSaveData.java`** - сохраненный персонаж
- **`EnemySaveData.java`** - сохраненный враг
- **`ItemSaveData.java`** - сохраненный предмет
- **`LeaderboardSave.java`** - все рекорды

### JSON маппер:
- **`JsonMapper.java`** - синглтон для сериализации Jackson
  - `writeValue()` - объект → JSON файл
  - `readValue()` - JSON файл → объект

### Файлы данных:
- `src/datalayer/data/savegame.json` - текущее сохранение
- `src/datalayer/data/leaderboard.json` - таблица рекордов

---

## 🎨 PRESENTATION LAYER (Lanterna) - Рендеринг ASCII

**Путь:** `core/src/main/java/io/github/example/presentation_lanterna/`

**Используется для тестирования!** Позже заменяется LibGDX рендерингом.

### Главные классы:
- **`MainRenderer.java`** - **СТАРАЯ ГЛАВНАЯ ТОЧКА** (для ASCII рендеринга)
  - Инициализирует Lanterna терминал
  - Главный цикл: рендер → обработка ввода
  - Методы: `render()`, `handleInput()`, `display()`

- **`ActorsRenderer.java`** - рендер персонажа и врагов
  - Рисует символы для каждого типа врага

- **`FogRenderer.java`** - туман войны
  - Рендерит видимые клетки на основе позиции игрока

- **`TurnRenderer.java`** - рендер информации о ходе
  - Статистика, сообщения боя, инвентарь

- **`ColorScheme.java`** - цветовая схема терминала

---

## 🎯 PRESENTATION LAYER (LibGDX) - Новый рендеринг

**Путь:** `lwjgl3/src/main/java/` (главная игра теперь здесь!)

Это новое представление на LibGDX. Структура:
- `presentation/` - основной слой рендеринга
  - `screens/` - различные экраны (меню, игра, инвентарь)
  - `renderer/` - компоненты рендеринга
    - `layers/` - 6-слойный рендеринг
  - `input/` - обработка ввода
  - `events/` - диспетчер событий
  - `util/` - утилиты (логирование, константы)

---

## 🔄 ПОТОК ДАННЫХ - Как работает игра

### 1️⃣ Инициализация (Main.java)
```
Main.create()
  ├─ GameService.startNewGame()
  │  └─ GameSessionGeneration.startNewGame()
  │     ├─ создает Player
  │     ├─ LevelGeneration.generateLevel()
  │     └─ GameSession инициализирована
  └─ PresentationLayer инициализирована
```

### 2️⃣ Главный игровой цикл
```
Main.render() каждый фрейм:
  └─ PresentationLayer.render()
     ├─ Обработка ввода (стрелки, ввод)
     ├─ GameService.processPlayerAction(Direction)
     │  ├─ MovementService.movePlayer()
     │  │  ├─ проверка границ
     │  │  ├─ проверка врагов → CombatService.attack()
     │  │  ├─ подбор предметов
     │  │  └─ игрок переместился
     │  └─ EnemyAiService.processAllEnemies()
     │     └─ для каждого врага:
     │        ├─ calculationNewCoordinates() (атака или случайно)
     │        ├─ MovementService.moveEnemy()
     │        └─ проверка боя с игроком
     └─ Рендер уровня, врагов, интерфейса
```

### 3️⃣ Использование предмета
```
GameService.useItemFromBackpack(indexInBackpack)
  ├─ InventoryService.useItem()
  │  └─ в зависимости от типа:
  │     ├─ FOOD: Stats.heal()
  │     ├─ SCROLL/ELIXIR: Stats.setXXX() с отслеживанием
  │     └─ WEAPON: Player.takeWeapon()
  └─ EnemyAiService.processAllEnemies() (враги тоже ходят)
```

### 4️⃣ Переход на следующий уровень
```
Player попадает на exit → MovementService.movePlayer() → MoveResult.EXIT_REACHED
  ├─ GameService.transitionToNextLevel()
  │  ├─ LevelGeneration.generateLevel() (новый уровень)
  │  ├─ SaveService.saveGame() (автосохранение)
  │  └─ GameState → PLAYING
```

### 5️⃣ Смерть игрока
```
CombatService.handleDeath()
  ├─ Player.health <= 0
  ├─ GameState → GAME_OVER
  ├─ Leaderboard.updateLastRecord() (обновить рекорд)
  └─ SaveService.saveLeaderBoard() (сохранить таблицу)
```

### 6️⃣ Сохранение/загрузка
```
SaveService.saveGame(GameSession)
  ├─ convertToSaveData() (трансформ объектов)
  └─ JsonMapper.writeValue() (JSON → файл)

SaveService.loadGame()
  ├─ JsonMapper.readValue() (JSON → объекты)
  └─ convertToGameSession() (восстановление)
```

---

## 📊 Карта соответствия требований README ↔ Код

| Требование из README | Где реализовано |
|---|---|
| **Задание 1: Сущности** | |
| Игровая сессия | `GameSession.java` |
| Уровень | `Level.java` + `LevelGeneration.java` |
| Комната, Коридор | `Room.java`, `Passage.java`, `Tile.java` |
| Персонаж | `Player.java` → `Character.java` |
| Рюкзак | `Backpack.java` |
| Враг | `Enemy.java` → `Character.java` |
| Предмет | `Item.java` |
| **Задание 2: Геймплей** | |
| 21 уровень | `GameSessionGeneration.java` инициализирует уровень 1 |
| 9 комнат (3x3) | `LevelGeneration.generateSizes()` - `ROOMS_IN_WIDTH/HEIGHT = 3` |
| Враги и предметы в комнатах | `generateEnemies()`, `roomGenerateItems()` |
| Управление перемещением | `MovementService.movePlayer()` |
| Сражение | `CombatService.attack()`, `CombatResult` |
| Пошаговый режим | `GameService.processPlayerAction()` → враги ходят |
| Здоровье персонажа | `Stats.currentHealth`, `Stats.maxHealth` |
| Ловкость в попадании | `CombatService.isHitSuccessful()` |
| Сила в урона | `CombatService.calculateDamage()` |
| Сокровища от врагов | `CombatService.calculateTreasureDrop()` |
| Инвентарь | `Backpack.java`, `InventoryService.java` |
| Использование предметов | `InventoryService.useItem()` |
| Выход на уровень | `MovementService.movePlayer()` → `Level.isExit()` |
| **Задание 3: Генерация** | |
| 9 комнат в сетке | `LevelGeneration.generateSizes()` |
| Коридоры между комнатами | `LevelGeneration.generatePassages()` |
| Связный граф | алгоритм рекурсивного деления в `generatePassages()` |
| Start и End комнаты | `generateSizes()` - `setType("Start")`, `setType("End")` |
| **Задание 4: Сохранение** | |
| JSON сохранение | `SaveService.java`, `JsonMapper.java` |
| Восстановление статистики | `convertToGameSession()` полностью восстанавливает |
| Таблица рекордов | `Leaderboard.java`, `LeaderboardSave.java` |
| **Задание 5: Рендеринг (ASCII)** | |
| Рендер сцены | `MainRenderer.java` (старый), `TileLayerRenderer.java` (новый) |
| Рендер врагов/игрока | `ActorsRenderer.java` (старый), `ActorLayerRenderer.java` (новый) |
| Туман войны | `FogRenderer.java` (старый), `FogLayerRenderer.java` (новый) |
| Управление WASD | LibGDX слой обработки ввода |
| Использование предметов (keys) | `InputHandler.java` обрабатывает сочетания |

---

## 🔑 Ключевые точки входа для изменений

Если нужно **изменить функционал:**

| Что менять | Куда идти |
|---|---|
| Механика боя, урон | `CombatService.java` методы `attack()`, `calculateDamage()` |
| Движение врагов | `EnemyAiService.java` методы `inAttackZone()`, `determiningDirectionAttack()` |
| Использование предметов | `InventoryService.java` метод `useItem()` |
| Типы врагов | `EnemyType.java`, `EntityConfig.java` |
| Типы предметов | `ItemType.java`, `Item.java` |
| Размер уровня | `GameConfig.java` константы `REGION_WIDTH`, `REGION_HEIGHT` |
| Количество комнат | `GameConfig.java` `ROOMS_IN_WIDTH`, `ROOMS_IN_HEIGHT` |
| Сохранение/загрузка | `SaveService.java` методы `convertToSaveData()`, `convertToGameSession()` |
| Рендеринг | `presentation/renderer/layers/` - 6 слоев рендеринга |
| Рендеринг интерфейса | `UILayerRenderer.java` |

---

## 🎓 Учебные примечания

- **Чистая архитектура:** domain слой полностью независим от presentation/datalayer
- **TDD-дружественно:** легко юнит-тестировать `GameService` и сервисы без рендеринга
- **Масштабируемо:** новые типы врагов/предметов добавляются в конфиг + перечисления
- **Пошаговая логика:** все действия идут через `GameService.processPlayerAction()` - одна точка контроля
- **Сохранение:** JSON DTOs позволяют легко добавлять новые поля без переписания парсеров

---

## 📁 Полная структура файлов

```
LibGDX_Roguelike/
├── core/src/main/java/io/github/example/
│   ├── domain/
│   │   ├── entities/ [Player, Enemy, Character, Stats, Item, Backpack, etc]
│   │   ├── level/ [Level, LevelGeneration, Room, Tile, Coordinates, etc]
│   │   ├── service/ [GameService, CombatService, MovementService, etc]
│   │   └── unittest/ [Logger for testing]
│   │
│   ├── datalayer/
│   │   ├── SaveService.java [Main save/load orchestrator]
│   │   ├── JsonMapper.java [Jackson serializer]
│   │   ├── SaveData.java [Root DTO]
│   │   ├── *SaveData.java [Various DTOs]
│   │   └── data/ [JSON files]
│   │       ├── savegame.json
│   │       └── leaderboard.json
│   │
│   ├── presentation_lanterna/ [OLD ASCII renderer - for testing]
│   │   ├── MainRenderer.java
│   │   ├── ActorsRenderer.java
│   │   ├── FogRenderer.java
│   │   └── ColorScheme.java
│   │
│   ├── Main.java [LibGDX entry point]
│   └── FirstScreen.java
│
└── lwjgl3/src/main/java/io/github/example/
    ├── presentation/ [NEW LibGDX renderer]
    │   ├── screens/ [GameScreen, MenuScreen, etc]
    │   ├── renderer/
    │   │   └── layers/ [6-layer rendering]
    │   ├── input/ [InputHandler, InputQueue]
    │   └── util/ [Logger, Constants]
    └── [other files]
```

---

## 🚀 Как начать разрабатывать

1. **Для изменения логики:**
   - Отредактируй нужный сервис в `domain/service/`
   - Логика игры полностью в `GameService` и его зависимостях

2. **Для добавления врага:**
   - Добавь тип в `EnemyType.java`
   - Конфигурация в `EntityConfig.java`
   - ИИ логика в `EnemyAiService.java`

3. **Для добавления предмета:**
   - Добавь тип в `ItemType.java`
   - Подтип в `ItemSubType.java`
   - Обработку в `InventoryService.useItem()`

4. **Для тестирования сохранений:**
   - Вызови `GameService.loadSaveGame()` при старте
   - Сохранение происходит автоматически через `SaveService`

---

*Документ автоматически сгенерирован для понимания архитектуры LibGDX Roguelike*
