# Быстрый справочник - LibGDX Roguelike

## 🎯 Быстрый старт навигации по коду

### Я хочу изменить... → Нужно отредактировать:

| Что нужно изменить | Файл | Метод |
|---|---|---|
| **Здоровье/сила/ловкость** | `domain/entities/Stats.java` | `setCurrentHealth()`, `setStrength()` |
| **Наносимый урон в бою** | `domain/service/CombatService.java` | `calculateDamage()` |
| **Вероятность попадания** | `domain/service/CombatService.java` | `isHitSuccessful()` |
| **Поведение врагов** | `domain/service/EnemyAiService.java` | `inAttackZone()`, `calculationNewCoordinates()` |
| **Использование предметов** | `domain/service/InventoryService.java` | `useItem()` |
| **Размер уровня (200x200)** | `domain/service/GameConfig.java` | `REGION_WIDTH`, `REGION_HEIGHT` |
| **Количество комнат (3x3)** | `domain/service/GameConfig.java` | `ROOMS_IN_WIDTH`, `ROOMS_IN_HEIGHT` |
| **Размер комнат (8-15)** | `domain/service/GameConfig.java` | `MIN/MAX_ROOM_WIDTH/HEIGHT` |
| **Кол-во врагов на уровне** | `domain/level/LevelGeneration.java` | `calculateNumberOfEnemies()` |
| **Типы врагов** | `domain/entities/EnemyType.java` | (добавить) |
| **Характеристики враг** | `domain/entities/EntityConfig.java` | (константы) |
| **Типы предметов** | `domain/entities/ItemType.java` | (добавить) |
| **Сохранение/загрузка** | `datalayer/SaveService.java` | `convertToSaveData()` |
| **Рендеринг (LibGDX)** | `presentation/renderer/layers/` | (выбрать слой) |
| **Интерфейс (HUD)** | `presentation/renderer/layers/UILayerRenderer.java` | `render()` |
| **Главный цикл игры** | `domain/service/GameService.java` | `processPlayerAction()` |

---

## 🗂️ Структура файлов - Краткая карта

```
Domain Layer (io/github/example/domain/)
├── entities/
│   ├── Character.java (базовый класс)
│   ├── Player.java (персонаж игрока)
│   ├── Enemy.java (враги)
│   ├── Stats.java ⭐ (характеристики)
│   ├── Item.java ⭐ (предметы)
│   ├── Backpack.java (рюкзак)
│   └── [типы: EnemyType.java, ItemType.java, ItemSubType.java]
│
├── level/
│   ├── LevelGeneration.java ⭐ (генерация уровня)
│   ├── Level.java ⭐ (текущий уровень)
│   ├── Room.java (одна комната)
│   ├── Tile.java (одна клетка)
│   ├── Coordinates.java (X, Y)
│   └── [вспомогательные: Size, SpaceType, etc]
│
└── service/
    ├── GameService.java ⭐⭐⭐ (ГЛАВНЫЙ - управление всем)
    ├── GameSession.java ⭐ (текущая сессия)
    ├── GameRecord.java ⭐ (статистика)
    ├── Leaderboard.java ⭐ (таблица рекордов)
    ├── MovementService.java ⭐ (перемещение)
    ├── CombatService.java ⭐ (боевая система)
    ├── EnemyAiService.java ⭐ (ИИ врагов)
    ├── InventoryService.java ⭐ (инвентарь)
    ├── GameSessionGeneration.java (новая игра)
    ├── GameConfig.java (конфигурация)
    └── [результаты: CombatResult, MoveResult, Direction]

Data Layer (io/github/example/datalayer/)
├── SaveService.java ⭐ (ГЛАВНЫЙ - сохранение)
├── JsonMapper.java (JSON сериализация)
├── SaveData.java (корневой DTO)
└── [DTO для каждой сущности: PlayerSaveData, EnemySaveData, ItemSaveData, etc]

Presentation Layer (presentation/)
├── PresentationLayer.java (управление экранами)
├── screens/ (различные экраны)
│   ├── MenuScreen.java
│   ├── GameScreen.java ⭐ (главный игровой экран)
│   ├── InventoryScreen.java
│   └── LeaderboardScreen.java
├── renderer/
│   ├── layers/
│   │   ├── TileLayerRenderer.java (стены, полы)
│   │   ├── ActorLayerRenderer.java (враги, игрок)
│   │   ├── ItemLayerRenderer.java (предметы)
│   │   ├── FogLayerRenderer.java (туман войны)
│   │   ├── EffectsLayerRenderer.java (частицы)
│   │   └── UILayerRenderer.java (HUD, интерфейс)
│   └── MainRenderer.java
├── input/
│   ├── InputHandler.java (обработка клавиш)
│   └── InputQueue.java (очередь команд)
└── util/
    ├── Logger.java (логирование)
    └── Constants.java (константы)

Main.java (точка входа LibGDX)
```

⭐ = часто редактируемые файлы
⭐⭐⭐ = главный файл для понимания логики

---

## 🔄 Главный цикл в одной картинке

```
Main.render()
  ↓
PresentationLayer.render()
  ├─ InputHandler (получить направление)
  ├─ GameService.processPlayerAction()
  │  ├─ MovementService.movePlayer() [игрок ходит]
  │  └─ EnemyAiService.processAllEnemies() [враги ходят]
  └─ MainRenderer.render() [рисовать всё]
  ↓
NEXT FRAME
```

**Ключ:** Каждый ход = одна клетка движения (пошаговый режим)

---

## 📊 Требования README ↔ Код

### Задание 1: Сущности ✅
- `Player.java`, `Enemy.java`, `Character.java`
- `Stats.java` - health, strength, agility, maxHealth
- `Backpack.java` - до 9 каждого типа + treasures
- `Item.java` - все типы бонусов
- `Room.java`, `Tile.java`, `Level.java`

### Задание 2: Геймплей ✅
- `GameService.java` - пошаговый режим
- `MovementService.java` - перемещение, подбор
- `CombatService.java` - урон, попадание, смерть
- `EnemyAiService.java` - враги видят на расстоянии и атакуют
- `InventoryService.java` - использование предметов
- Враги, здоровье, ловкость, сила, сокровища - всё есть ✅

### Задание 3: Генерация ✅
- `LevelGeneration.java`:
  - `generateSizes()` - 9 комнат (3x3)
  - `generateRooms()` - стены и полы
  - `generatePassages()` - коридоры между комнатами
  - `generateEnemies()` - враги на уровне
  - `roomGenerateItems()` - предметы на уровне

### Задание 4: Сохранение ✅
- `SaveService.java` - JSON save/load
- `JsonMapper.java` - Jackson сериализация
- `*SaveData.java` - DTO для каждой сущности
- Восстановление полного состояния, таблица рекордов ✅

### Задание 5: Рендеринг ✅
- `presentation/renderer/layers/*Renderer.java`
- Туман войны: `FogLayerRenderer.java`
- 6 слойный рендеринг для всех компонентов ✅
- Управление: `InputHandler.java` - Arrow keys, числовые клавиши ✅

---

## 🎮 Управление (эти клавиши работают)

| Клавиша | Действие |
|---|---|
| ⬆️⬇️⬅️➡️ | Движение игрока |
| SPACE | Пропустить ход |
| 1-9 | Использовать предмет из рюкзака |
| I | Открыть инвентарь |
| P / ESC | Пауза |
| Q | Выход |

---

## 💾 Файлы сохранений

```
core/src/
└── datalayer/data/
    ├── savegame.json (текущее сохранение)
    └── leaderboard.json (таблица рекордов)
```

При смерти игрока: сохранение ОЧИЩАЕТСЯ, результат добавляется в leaderboard.json

---

## 🧪 Тестирование

### Быстро протестировать новую фишку:
1. Отредактировать нужный `.java` файл в `domain/`
2. Перестроить: `./gradlew clean build`
3. Запустить: `./gradlew lwjgl3:run`
4. Если crashes → проверить `core/src/main/java/io/github/example/domain/unittest/Logger.java` выводы

### Отладка через логирование:
```java
Logger.info("Сообщение");     // Информация
Logger.error("Ошибка");       // Ошибка
```
Выводы пишутся в `core/src/main/java/io/github/example/domain/unittest/log.txt`

---

## 🎓 Ключевые концепции

### Пошаговая логика
```
GameService.processPlayerAction(direction)
  ├─ movePlayer()          [1 действие]
  └─ processAllEnemies()   [враги ходят в ответ]
```

### Трёхслойная архитектура
- **Domain**: чистая логика (не зависит от UI/сохранений)
- **Data**: JSON save/load (независим от UI)
- **Presentation**: только рендеринг и ввод (не содержит логику)

### Отслеживание характеристик
- Постоянные бонусы (свитки): хранятся в Stats
- Временные бонусы (эликсиры): отслеживаются через `deleteXXX` поля
- При отмене эликсира: `player.deleteBust()` вычитает накопленные бонусы

### Сокровища
- Выпадают ТОЛЬКО с убитых врагов (не спауны)
- Хранятся в одной ячейке Backpack: `totalTreasureValue`
- Последний рекорд сохраняется при смерти

---

## 🚨 Частые ошибки

| Ошибка | Причина | Решение |
|---|---|---|
| Player не двигается | InputHandler не обновляет Direction | Проверить `InputHandler.processInput()` |
| Враги не атакуют | `inAttackZone()` неправильна | Проверить дистанцию враждебности |
| Сохранение не работает | Файл нельзя записать | Проверить путь: `src/datalayer/data/` |
| Урон считается неправильно | Оружие не учитывается | `calculateDamage()` + `getStrengthBonus()` |
| Уровень бесконечен | Враги не спауны в Start комнате | `LevelGeneration.generateEnemies()` |
| Рюкзак переполняется | Не проверяется размер | `Backpack.addItem()` вернёт false |

---

## 🔍 Как найти функционал по названию

```
"Как работает сокровище?"
  → CombatService.calculateTreasureDrop()
  → CombatService.handleDeath()
  → Backpack.addTotalTreasureValue()

"Где враги атакуют?"
  → EnemyAiService.inAttackZone()
  → MovementService.moveEnemy()
  → CombatService.attack()

"Как сохранить ловкость?"
  → Stats.setAgility(int, boolean)
  → SaveService.convertToSaveData()
  → PlayerSaveData.agility

"Почему не подбираются предметы?"
  → MovementService.movePlayer() → pickUpItem()
  → Backpack.addItem()
  → Level.removeItem()

"Где генерируются враги?"
  → LevelGeneration.generateEnemies()
  → EnemyType → EntityConfig → новый Enemy()
```

---

## 📈 Сложность по уровню

| Уровень | Враги | Еда | Эликсиры | Свитки |
|---|---|---|---|---|
| 1 | 3 | 20 | 5 | 3 |
| 5 | 5 | 15 | 4 | 2 |
| 10 | 8 | 10 | 3 | 2 |
| 15 | 12 | 5 | 2 | 1 |
| 21 | 15+ | 2 | 1 | 0 |

*Точные числа регулируются в `LevelGeneration.calculateNumberOfXXX(level)`*

---

## 💡 Советы для расширения

### Добавить новый враг
1. `EnemyType.java` - добавить тип
2. `EntityConfig.java` - добавить конфиг
3. `EnemyAiService.java` - добавить поведение (если особое)

### Добавить новый предмет
1. `ItemType.java` - добавить тип
2. `ItemSubType.java` - добавить подтип
3. `InventoryService.useItem()` - обработка использования

### Добавить новый экран
1. Создать `screens/NewScreen.java` (наследовать `Screen`)
2. `PresentationLayer.java` - добавить метод переключения

### Изменить генерацию
1. `LevelGeneration.java` - редактировать нужный метод
2. `GameConfig.java` - если это параметр конфигурации

---

## 📚 Дополнительная информация

- **README.md** - полное описание требований
- **CONTEXT/PROJECT_STRUCTURE.md** - подробное описание всех файлов
- **CONTEXT/ARCHITECTURE_DIAGRAMS.md** - диаграммы и потоки
- **CONTEXT/CODE_EXAMPLES.md** - примеры реальных функций

---

*Справочник позволяет быстро ориентироваться в кодовой базе из ~50 файлов*

**Помни:** GameService.java - главное окно, через которое видна вся логика игры!
