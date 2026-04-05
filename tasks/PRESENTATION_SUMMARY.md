# Анализ Presentation слоя LibGDX Roguelike - ИТОГОВАЯ СВОДКА

📋 **Дата**: 2026-04-05  
🎯 **Статус**: Требования и план завершены, задачи созданы в Beads  
👤 **Разработчик**: Copilot CLI (Claude Haiku 4.5)

---

## I. ОБЗОР ПРОЕКТА

### Текущая архитектура
```
LibGDX_Roguelike/
├── core/                    # Основной модуль (domain, datalayer, presentation_lanterna)
│   ├── domain/             # Игровая логика (независима от рендеринга)
│   ├── datalayer/          # Сохранение/загрузка (Jackson JSON)
│   └── presentation_lanterna/ # Терминальный рендерер (будет заменен на LibGDX)
└── lwjgl3/                 # Desktop лаунчер (LWJGL3 backend)
```

### Выбранная конфигурация
| Параметр | Значение |
|----------|----------|
| **Разрешение** | 1920x1080 пиксели |
| **Размер тайла** | 32x32 пиксели |
| **Проекция камеры** | Orthographic (2D вид сверху) |
| **Архитектура** | 3 слоя: Domain → Presentation → LibGDX |
| **Модуль presentation** | Новый пакет в core |
| **Язык** | Java 17 |
| **Фреймворк** | LibGDX 1.12.1+ |

---

## II. АРХИТЕКТУРА PRESENTATION СЛОЯ

### Структура пакетов

```java
io.github.example.presentation/
│
├── [MainPresentationLayer.java]     // Точка входа в слой
│
├── renderer/                        // Отрисовка
│   ├── MainRenderer.java            // Координатор всех слоев
│   ├── LayerRenderer.java           // Интерфейс для рендеринга
│   ├── TileLayerRenderer.java       // Слой тайлов
│   ├── ActorLayerRenderer.java      // Персонажи и враги
│   ├── ItemLayerRenderer.java       // Предметы на земле
│   ├── FogRenderer.java             // Туман войны (видимость)
│   └── HudRenderer.java             // HUD информация
│
├── camera/                          // Управление камерой
│   └── CameraController.java        // Ортографическая камера
│
├── assets/                          // Управление ресурсами
│   ├── AssetManager.java            // Загрузка и кэширование спрайтов
│   ├── SpriteAtlas.java             // Набор спрайтов по категориям
│   └── AnimatedSprite.java          // Анимированные спрайты
│
├── screens/                         // Экраны/сцены
│   ├── Screen.java                  // Базовый интерфейс
│   ├── ScreenManager.java           // Управление переходами
│   ├── MenuScreen.java              // Главное меню
│   ├── GameScreen.java              // Основной экран
│   ├── PauseScreen.java             // Пауза
│   ├── InventoryScreen.java         // Инвентарь
│   ├── LeaderboardScreen.java       // Лидерборд
│   └── DeathScreen.java             // Game Over
│
├── ui/                              // Система UI
│   ├── UIComponent.java             // Базовый компонент
│   ├── UIButton.java                // Кнопка
│   ├── UIText.java                  // Текст
│   ├── UIPanel.java                 // Панель/контейнер
│   ├── UIIcon.java                  // Иконка/спрайт
│   ├── UIProgressBar.java           // Полоса прогресса
│   ├── LayoutManager.java           // Система позиционирования
│   └── HUD.java                     // HUD компонент
│
├── animation/                       // Анимации
│   └── AnimationController.java     // Управление анимациями спрайтов
│
├── input/                           // Обработка ввода
│   ├── InputHandler.java            // InputProcessor для LibGDX
│   └── InputContext.java            // Маршрутизация ввода
│
└── util/                            // Утилиты
    ├── ColorScheme.java             // Цветовая схема
    ├── ViewportUtils.java           // Работа с viewport
    └── Constants.java               // Константы (размеры, отступы)
```

### Структура ассетов

```
assets/sprites/
├── player/
│   ├── idle_down.png
│   ├── walk_down.png
│   ├── attack_down.png
│   └── ... (8 направлений: down, up, left, right, и диагонали)
│
├── enemies/
│   ├── zombie/
│   │   ├── idle_down.png
│   │   ├── walk_down.png
│   │   └── ...
│   ├── goblin/
│   └── ...
│
├── items/
│   ├── sword.png
│   ├── potion_health.png
│   └── ...
│
├── tiles/
│   ├── floor.png
│   ├── wall.png
│   ├── wall_corner.png
│   └── ...
│
└── ui/
    ├── button_idle.png
    ├── button_hover.png
    ├── button_pressed.png
    ├── heart.png
    └── inventory_slot.png
```

---

## III. КЛЮЧЕВЫЕ КОМПОНЕНТЫ И ИХ ФУНКЦИИ

### 1. **MainRenderer** (Главный рендерер)
**Ответственность**: Координирует отрисовку всех слоев в правильном порядке

**Слои отрисовки (z-order)**:
```
7. UI/HUD (кнопки, здоровье, лог)
6. Эффекты (опционально)
5. Туман войны (видимость)
4. Персонаж (всегда выше врагов)
3. Враги
2. Предметы
1. Тайлы (земля, стены)
```

**Требования**:
- Оптимизация: только видимые элементы (culling)
- Кэширование спрайтов
- Масштабирование для адаптации разрешений

---

### 2. **AssetManager** (Менеджер ассетов)
**Ответственность**: Загрузка, кэширование и управление спрайтами

**Функции**:
- Ленивая загрузка (только при необходимости)
- Кэширование в памяти
- Организация по категориям (player, enemies, items, tiles, ui)
- Поддержка анимированных спрайтов (спрайт-листы)
- Fallback на пустой спрайт при ошибке

**Оптимизация**:
- Максимум видимых спрайтов: ~500
- Автоматическая очистка при смене уровня
- Преоптимизация частых спрайтов

---

### 3. **CameraController** (Управление камерой)
**Ответственность**: Управление ортографической камерой

**Функции**:
- Следить за персонажем (центр экрана)
- Ограничивать границы просмотра (не выходить за уровень)
- Вычисление видимых тайлов
- Опциональный зум
- Мягкое перемещение (lerp)

**Преобразования**:
- Мировые координаты → Экранные координаты
- Экранные координаты → Мировые координаты

---

### 4. **ScreenManager** (Управление экранами)
**Ответственность**: Управление переходами между экранами

**Функции**:
- Переключение между экранами
- Поддержка стека экранов (для паузы поверх GameScreen)
- Инициализация, обновление, очистка экранов
- Маршрутизация ввода по активному экрану

**Переходы**:
```
MenuScreen ─→ GameScreen ←─ PauseScreen (поверх)
   ↑            ↓                ↓
   └─ DeathScreen ────────→ InventoryScreen
```

---

### 5. **Экраны** (Screen implementations)

#### MenuScreen
- Кнопки: "Новая игра", "Загрузить", "Выход"
- Фон (цвет или спрайт)
- Интерактивные кнопки (наведение, клик)

#### GameScreen ⭐ Главный экран
- Отрисовка уровня (тайлы, враги, персонаж, предметы)
- HUD (информация персонажа)
- Туман войны
- Обработка ввода:
  - WASD/Стрелки → движение
  - I → инвентарь
  - P/Esc → пауза
- Интеграция с domain слоем (GameService)

#### PauseScreen
- Полупрозрачный оверлей
- Меню: "Продолжить", "Инвентарь", "Выход в меню"
- Остановка игрового цикла

#### InventoryScreen
- Список предметов (из Backpack)
- Выбранный предмет: описание, иконка
- Кнопки: "Использовать", "Отбросить", "Назад"

#### LeaderboardScreen
- Топ 10 игроков: Ранг, Имя, Уровень, Очки, Дата
- Загрузка из Leaderboard (datalayer)

#### DeathScreen
- "Game Over"
- Статистика: уровень, враги убиты, лут
- Кнопки: "Меню", "Выход"

---

### 6. **UI Framework** (Система UI компонентов)

**Базовые компоненты**:

| Компонент | Функция |
|-----------|---------|
| **UIButton** | Кнопка: текст, состояния (idle/hover/pressed), callback |
| **UIText** | Текст: шрифт, размер, цвет, многострочный |
| **UIPanel** | Контейнер: фон, границы, размещение детей |
| **UIIcon** | Иконка/спрайт + опциональный текст |
| **UIProgressBar** | Полоса прогресса (0-100%) |
| **UISlider** | Ползунок (для настроек) |

**Система позиционирования (Layout)**:
- Якоря (anchor): TL, TC, TR, ML, MC, MR, BL, BC, BR
- Выравнивание (align): Relative к экрану или элементу
- Смещения (offset): От якоря
- Масштабирование: % от экрана или пиксели

**События**:
- Click (клик мышью)
- Hover (наведение)
- Focus (фокус)

---

### 7. **HUD** (Heads-Up Display)
**Ответственность**: In-game интерфейс

**Элементы**:
```
Верхний левый угол:
  ❤ HP: 15/20
  📍 Lvl: 3
  👹 Enemies: 5

Нижний левый угол:
  [Action Log]
  You hit Zombie for 5 damage!
  Zombie hit you for 2 damage!
```

**Требования**:
- Обновление в реальном времени
- Логирование действий (последние 3-5)
- Четкая читаемость

---

### 8. **InputHandler** (Обработчик ввода)
**Ответственность**: Обработка ввода и маршрутизация

**Клавиатура**:
```
W / ↑       → Движение вверх
A / ←       → Движение влево
S / ↓       → Движение вниз
D / →       → Движение вправо
I           → Инвентарь
P / Esc     → Пауза
Enter       → Подтверждение
Space       → Атака (опционально)
```

**Мышь** (опционально):
- Клики на UI
- Клик для движения

**Требования**:
- InputProcessor для LibGDX
- Маршрутизация по активному экрану
- Кэширование клавиш для плавного движения
- Блокировка ввода при паузе

---

### 9. **AnimationController** (Система анимации)
**Ответственность**: Управление анимациями спрайтов

**Направления** (8):
- Вверх, Вниз, Влево, Вправо
- Диагонали (ВВ-Лево, ВВ-Право, НВ-Лево, НВ-Право)

**Состояния**:
- Idle (стояние)
- Walk (ходьба)
- Attack (атака)
- Damage (получение урона)

**Требования**:
- Синхронизация с game loop (turn-based)
- Кэширование спрайтов
- Настраиваемая скорость

---

## IV. ИНТЕГРАЦИЯ С DOMAIN СЛОЕМ

### Читаемые данные из Domain
```java
// Из GameService
GameSession session = gameService.getSession();

// Уровень и персонажи
Level level = session.getLevel();
Player player = level.getPlayer();
List<Enemy> enemies = level.getEnemies();
List<Item> items = level.getItems();

// Состояние
GameState gameState = gameService.getGameState();
Tile[][] tiles = level.getTiles();
```

### Отправляемые команды в Domain
```java
// Движение
MoveResult moveResult = gameService.movePlayer(Direction direction);

// Боевое действие
CombatResult combatResult = gameService.attackEnemy(Enemy enemy);

// Инвентарь
gameService.pickUpItem(Item item);
gameService.useItem(Item item);
gameService.dropItem(Item item);

// Состояние игры
gameService.savegame();
gameService.endGame();
```

### Паттерн: Observer (опционально)
```java
gameService.addGameStateListener(new GameStateListener() {
    @Override public void onPlayerMoved(MoveResult result) { }
    @Override public void onEnemyDefeated(Enemy enemy) { }
    @Override public void onGameOver() { }
});
```

---

## V. ТРЕБОВАНИЯ К КАЧЕСТВУ

### Производительность
- **FPS**: 60+ при нормальных условиях
- **Рендеринг**: < 5ms за кадр
- **Максимум врагов**: 50+ без снижения FPS
- **Память**: < 300 MB

### Архитектура
- Слои не смешиваются (presentation не содержит domain логику)
- Тестируемость UI компонентов
- Javadoc для публичных методов

### UX
- Отзывчивость: < 16ms на клик
- Визуальная обратная связь на UI
- Ясность информации в HUD

---

## VI. ПОЭТАПНАЯ РЕАЛИЗАЦИЯ

### **Фаза 1: Инфраструктура** (Блокирует все остальное)
- [ ] Архитектура и пакеты
- [ ] AssetManager (загрузка PNG)
- [ ] CameraController
- [ ] MainRenderer (базовая)
- [ ] ScreenManager

### **Фаза 2: Базовые экраны**
- [ ] MenuScreen
- [ ] GameScreen (базовая, без отрисовки)
- [ ] PauseScreen
- [ ] InputHandler

### **Фаза 3: Рендеринг игры**
- [ ] TileLayerRenderer
- [ ] ActorLayerRenderer
- [ ] ItemLayerRenderer
- [ ] FogRenderer

### **Фаза 4: UI система**
- [ ] UIComponent и базовые компоненты
- [ ] HUD система
- [ ] LayoutManager

### **Фаза 5: Дополнительные экраны**
- [ ] InventoryScreen
- [ ] LeaderboardScreen
- [ ] DeathScreen

### **Фаза 6: Полировка**
- [ ] AnimationController
- [ ] Оптимизация производительности
- [ ] Визуальные улучшения

---

## VII. СОЗДАННЫЕ ЗАДАЧИ В BEADS

### Priority 1 (Критичные) - 9 задач
1. ✅ **LibGDX_Roguelike-aww** - Архитектура и инфраструктура слоя
2. ✅ **LibGDX_Roguelike-34u** - AssetManager и система загрузки спрайтов
3. ✅ **LibGDX_Roguelike-15c** - CameraController - управление камерой
4. ✅ **LibGDX_Roguelike-2pv** - MainRenderer и система слоев отрисовки
5. ✅ **LibGDX_Roguelike-0vd** - ScreenManager и система управления экранами
6. ✅ **LibGDX_Roguelike-754** - UI Framework - базовые компоненты
7. ✅ **LibGDX_Roguelike-gw2** - InputHandler - обработка ввода
8. ✅ **LibGDX_Roguelike-2ec** - GameScreen - основной экран игры
9. ✅ **LibGDX_Roguelike-um6** - Интеграция с Domain слоем

### Priority 2 (Важные) - 5 задач
10. ✅ **LibGDX_Roguelike-v35** - HUD система
11. ✅ **LibGDX_Roguelike-g53** - PauseScreen
12. ✅ **LibGDX_Roguelike-8ym** - InventoryScreen
13. ✅ **LibGDX_Roguelike-faq** - LeaderboardScreen и DeathScreen
14. ✅ **LibGDX_Roguelike-2dc** - Управление ресурсами и оптимизация
15. ✅ **LibGDX_Roguelike-typ** - Рендеринг слоев (TileLayer, ActorLayer, FogRenderer)

### Priority 3 (Полировка)
16. ✅ **LibGDX_Roguelike-dky** - AnimationController

---

## VIII. ДОКУМЕНТАЦИЯ И РЕСУРСЫ

### LibGDX Documentation (из Context7)
Получена информация о:
- OrthographicCamera для 2D проекции
- SpriteBatch для отрисовки спрайтов
- InputProcessor для обработки ввода
- Viewport для адаптации разрешений
- Работа с текстурами и спрайтами

### Примеры кода LibGDX
- Инициализация камеры: `new OrthographicCamera(30, 30 * (h/w))`
- Рендеринг: `batch.setProjectionMatrix(cam.combined)` + `batch.begin/end()`
- Преобразование координат: `camera.unproject(screenCoords)`
- Обработка ввода: реализовать `InputProcessor`

---

## IX. ОСНОВНЫЕ СОГЛАШЕНИЯ

### Кодирование
- Enum для Direction (UP, DOWN, LEFT, RIGHT, UP_LEFT и т.д.)
- Const для размеров: `TILE_SIZE = 32`, `SCREEN_WIDTH = 1920`, `SCREEN_HEIGHT = 1080`
- Snake_case для PNG файлов: `player_idle_down.png`
- Javadoc для публичных методов

### Структура спрайтов
- Одиночный спрайт: 32x32 PNG
- Анимированный: горизонтальный спрайт-лист (256x32 для 8 кадров)

### Обработка ресурсов
- Все ресурсы должны быть Disposed при выходе
- Кэширование частых спрайтов
- Ленивая загрузка остальных

---

## X. КРИТЕРИИ ЗАВЕРШЕНИЯ

✅ **Presentation слой готов когда**:

1. ✅ Рендеринг GameScreen работает (видны тайлы, персонаж, враги, предметы)
2. ✅ Все 6 экранов реализованы (Menu, Game, Pause, Inventory, Leaderboard, Death)
3. ✅ UI система работает (кнопки, клики, текст)
4. ✅ HUD отображает информацию персонажа в реальном времени
5. ✅ Обработка ввода синхронизирована с game loop
6. ✅ Нет утечек памяти (все ресурсы disposed)
7. ✅ Кроссбраузерные тесты покрывают основные компоненты
8. ✅ **FPS ≥ 60** при нормальных условиях
9. ✅ Интеграция с domain слоем работает корректно
10. ✅ Документация завершена

---

## XI. РЕКОМЕНДАЦИИ ДЛЯ РАЗРАБОТЧИКА

### Начало работы
1. **Прочитать требования**: `/tasks/PRESENTATION_REQUIREMENTS.md`
2. **Изучить LibGDX docs**: OrthographicCamera, SpriteBatch, InputProcessor
3. **Запустить `bd ready`**: Посмотреть готовые задачи
4. **Начать с Фазы 1**: Создать пакеты и базовую инфраструктуру

### Инструменты
```bash
# Посмотреть готовые задачи
bd ready

# Взять задачу
bd update <id> --claim

# После завершения
bd close <id>

# Синхронизация перед выходом
bd dolt push
git push
```

### Отладка и тестирование
- Начать с простых черно-белых спрайтов (16x16)
- Использовать Debug рендеринг для визуализации коллизий
- Тестировать с максимальным масштабом врагов (100+)
- Профилировать производительность с инструментами LibGDX

### Частые ошибки
- ❌ Не забыть dispose текстур при выходе → утечка памяти
- ❌ Рендеринг всех спрайтов без culling → упадет FPS
- ❌ Блокирующие операции в render() → фризы
- ✅ Используй threadPool для асинхронной загрузки спрайтов

---

## XII. ИТОГОВАЯ ИНФОРМАЦИЯ

📊 **Статистика**:
- **Файлы требований**: 1 (PRESENTATION_REQUIREMENTS.md)
- **Созданные задачи в Beads**: 16 (9 P1 + 5 P2 + 1 P3 + остальные из предыдущих фаз)
- **Пакетов в presentation**: 7 основных + 16 компонентов
- **Экранов/сцен**: 6
- **UI компонентов**: 6 базовых
- **Слоев отрисовки**: 7

🎯 **Готово к началу работы**:
- ✅ Архитектура определена
- ✅ Требования документированы
- ✅ Задачи созданы в Beads
- ✅ LibGDX документация изучена
- ✅ Соглашения определены

🚀 **Следующий шаг**: Начать с **Фазы 1: Инфраструктура** (задача LibGDX_Roguelike-aww)

---

**Документ подготовлен**: Copilot CLI (Claude Haiku 4.5)  
**Версия**: 1.0  
**Последнее обновление**: 2026-04-05
