# Требования для реализации Presentation слоя LibGDX Roguelike

## 1. Введение

Данный документ описывает требования для реализации **presentation слоя** - визуального отображения и интерфейса пользователя для roguelike игры на базе LibGDX с пиксельной графикой (32x32).

### Выбранные параметры:
| Параметр | Значение |
|----------|----------|
| **Разрешение** | 1920x1080 |
| **Размер тайла** | 32x32 пиксели |
| **Проекция** | Orthographic (2D вид сверху) |
| **Платформа** | LibGDX (LWJGL3) |
| **Модуль** | Новый `presentation` в core |
| **Архитектура** | Раздельные слои: domain, presentation, datalayer |

---

## 2. Архитектура Presentation слоя

### 2.1 Структура пакетов

```
io.github.example.presentation/
├── MainPresentationLayer.java          # Точка входа в слой
├── renderer/
│   ├── MainRenderer.java               # Главный рендерер (отрисовка сцены)
│   ├── LayerRenderer.java              # Интерфейс для рендеринга отдельных слоев
│   ├── TileLayerRenderer.java          # Рендеринг слоя тайлов
│   ├── ActorLayerRenderer.java         # Рендеринг персонажей и врагов
│   ├── ItemLayerRenderer.java          # Рендеринг предметов
│   ├── FogRenderer.java                # Рендеринг тумана войны
│   └── HudRenderer.java                # Рендеринг HUD
├── camera/
│   └── CameraController.java           # Управление камерой (позиция, зум)
├── assets/
│   ├── AssetManager.java               # Менеджер загрузки спрайтов
│   ├── SpriteAtlas.java                # Набор спрайтов по категориям
│   └── AnimatedSprite.java             # Анимированный спрайт
├── screens/
│   ├── Screen.java                     # Базовый интерфейс экрана
│   ├── ScreenManager.java              # Управление переходами между экранами
│   ├── MenuScreen.java                 # Главное меню
│   ├── GameScreen.java                 # Основной игровой экран
│   ├── PauseScreen.java                # Экран паузы
│   ├── InventoryScreen.java            # Экран инвентаря
│   ├── LeaderboardScreen.java          # Экран лидерборда
│   └── DeathScreen.java                # Экран смерти
├── ui/
│   ├── UIComponent.java                # Базовый компонент UI
│   ├── UIButton.java                   # Кнопка
│   ├── UIText.java                     # Текст
│   ├── UIPanel.java                    # Панель
│   ├── UIIcon.java                     # Иконка
│   ├── UIProgressBar.java              # Полоса прогресса
│   ├── LayoutManager.java              # Менеджер макета (якоря, выравнивание)
│   └── HUD.java                        # Heads-up display во время игры
├── animation/
│   └── AnimationController.java        # Управление анимациями спрайтов
├── input/
│   ├── InputHandler.java               # Обработчик ввода (InputProcessor LibGDX)
│   └── InputContext.java               # Контекст для маршрутизации ввода
├── util/
│   ├── ColorScheme.java                # Схема цветов игры
│   ├── ViewportUtils.java              # Утилиты для работы с viewport
│   └── Constants.java                  # Константы (размеры, отступы и т.д.)
└── resources/
    └── Sprites должны быть в /assets/sprites/
```

### 2.2 Основные компоненты

#### **MainRenderer** (Главный рендерер)
- Координирует отрисовку всех слоев
- Слои отрисовки (z-order):
  1. **Фон**: тайлы земли, стены, объекты уровня
  2. **Враги**: враги на уровне ниже персонажа
  3. **Предметы**: оружие, броня, зелья
  4. **Персонаж**: главный персонаж (всегда выше врагов)
  5. **Туман войны**: видимость (неопциональна для roguelike)
  6. **Эффекты**: летящие стрелы, взрывы (опционально на этом этапе)
  7. **UI/HUD**: интерфейс на экране

**Требования**:
- Оптимизация: рендер только видимых элементов (culling)
- Эффективное кэширование спрайтов
- Поддержка масштабирования для адаптации к разным разрешениям

#### **AssetManager** (Менеджер ассетов)
Система загрузки и управления спрайтами:

**Структура ассетов**:
```
assets/sprites/
├── player/
│   ├── idle_down.png          (32x32 или анимированный спрайт-лист)
│   ├── walk_down.png
│   ├── attack_down.png
│   └── ... (8 направлений)
├── enemies/
│   ├── zombie/
│   │   ├── idle_down.png
│   │   ├── walk_down.png
│   │   └── ...
│   └── ...
├── items/
│   ├── sword.png              (16x16)
│   ├── potion_health.png
│   └── ...
├── tiles/
│   ├── floor.png              (32x32)
│   ├── wall.png
│   └── ...
└── ui/
    ├── button_idle.png
    ├── heart.png
    └── ...
```

**Требования**:
- Ленивая загрузка спрайтов
- Кэширование в памяти
- Автоматическая очистка при смене уровня
- Поддержка анимированных спрайтов
- Fallback на пустой спрайт если файл не найден

#### **CameraController** (Управление камерой)
Ортографическая камера для 2D вида сверху

**Требования**:
- Следить за персонажем
- Ограничивать границы просмотра
- Мягкое перемещение (lerp)
- Вычисление видимых тайлов для оптимизации
- Опциональный зум

#### **ScreenManager** (Управление экранами)
- Переключение между экранами
- Поддержка стека экранов (для паузы)
- Инициализация/очистка ресурсов
- Управление обработкой ввода

#### **Экраны** (Screens)

| Экран | Описание |
|-------|---------|
| **MenuScreen** | Главное меню с кнопками (Новая игра, Загрузить, Выход) |
| **GameScreen** | Основной экран: уровень, персонаж, враги, HUD |
| **PauseScreen** | Пауза поверх GameScreen |
| **InventoryScreen** | Инвентарь: список предметов, опции (использовать/отбросить) |
| **LeaderboardScreen** | Топ 10 игроков (из Leaderboard) |
| **DeathScreen** | Game Over: статистика, кнопки меню/выход |

#### **UI Framework** (Система UI)

**Компоненты**:
- **UIButton**: Кнопка с состояниями (idle/hover/pressed)
- **UIText**: Текстовый элемент
- **UIPanel**: Контейнер с фоном
- **UIIcon**: Иконка/спрайт
- **UIProgressBar**: Полоса прогресса (для здоровья)
- **UISlider**: Ползунок (для настроек)

**Layout система**:
- Якоря (anchor)
- Выравнивание (align)
- Смещения (offset)

#### **HUD** (Heads-Up Display)
- Полоса здоровья (HP: 15/20)
- Информация о уровне
- Счетчик врагов
- Лог боевых действий (последние 3-5 сообщений)

#### **AnimationController** (Система анимации)
- 8 направлений движения
- Состояния: idle, walk, attack, damage
- Синхронизация с game loop (turn-based)
- Кэширование спрайтов

#### **InputHandler** (Обработчик ввода)

**Клавиатура**:
| Клавиша | Действие |
|---------|----------|
| W / ↑ | Движение вверх |
| A / ← | Движение влево |
| S / ↓ | Движение вниз |
| D / → | Движение вправо |
| I | Инвентарь |
| P / Esc | Пауза |
| Enter | Подтверждение |

**Требования**:
- InputProcessor для LibGDX
- Маршрутизация ввода по экранам
- Кэширование нажатых клавиш

---

## 3. Интеграция с Domain слоем

### 3.1 Читаемые данные
```java
GameSession session = gameService.getSession();
Player player = session.getPlayer();
Level level = session.getLevel();
List<Enemy> enemies = level.getEnemies();
List<Item> items = level.getItems();
```

### 3.2 Отправляемые команды
```java
MoveResult moveResult = gameService.movePlayer(direction);
CombatResult combatResult = gameService.attackEnemy(enemy);
gameService.pickUpItem(item);
gameService.useItem(item);
gameService.dropItem(item);
```

---

## 4. Управление ресурсами

### 4.1 Жизненный цикл
**При инициализации**: Загрузить AssetManager, ScreenManager
**При GameScreen**: Загрузить спрайты уровня
**При выходе**: Dispose всех текстур

### 4.2 Оптимизация
- Максимум видимых спрайтов: ~500
- Кэш спрайтов: основные враги загружены
- Максимум строк в логе HUD: 100

---

## 5. Требования к качеству

### 5.1 Производительность
- **FPS**: 60+
- **Рендеринг**: < 5ms за кадр
- **Максимум врагов**: 50+ без снижения FPS
- **Память**: < 300 MB

### 5.2 Код
- Слои не смешиваются
- Тестируемость UI компонентов
- Javadoc для публичных методов

### 5.3 UX
- Отзывчивость: < 16ms на клик
- Визуальная обратная связь на UI
- Ясность HUD информации

---

## 6. Поэтапная реализация

### **Фаза 1: Инфраструктура**
- [ ] Создать пакеты presentation
- [ ] AssetManager (загрузка PNG)
- [ ] CameraController
- [ ] MainRenderer (базовая)
- [ ] ScreenManager

### **Фаза 2: Экраны**
- [ ] MenuScreen
- [ ] GameScreen (базовая)
- [ ] PauseScreen
- [ ] InputHandler

### **Фаза 3: Рендеринг**
- [ ] TileLayerRenderer
- [ ] ActorLayerRenderer
- [ ] ItemLayerRenderer
- [ ] FogRenderer

### **Фаза 4: UI**
- [ ] UIComponent и базовые компоненты
- [ ] HUD система
- [ ] LayoutManager

### **Фаза 5: Доп. экраны**
- [ ] InventoryScreen
- [ ] LeaderboardScreen
- [ ] DeathScreen

### **Фаза 6: Полировка**
- [ ] AnimationController
- [ ] Оптимизация
- [ ] Визуальные улучшения

---

## 7. Зависимости и ограничения

### 7.1 Зависимости
- LibGDX 1.12.1+
- Jackson
- Domain слой (завершен)
- Ассеты (32x32 PNG)

### 7.2 Ограничения
- **Нет**: 3D графики, сложные шейдеры, particle effects
- **Да**: 2D спрайты, простые шрифты, базовая анимация

---

## 8. Соглашения

- Enum для направлений (Direction)
- Const для размеров (TILE_SIZE = 32)
- Snake_case для PNG файлов
- Javadoc для публичных методов

---

## 9. Критерии завершения

✅ **Готово когда**:
1. ✅ Рендеринг GameScreen работает
2. ✅ Все 6 экранов реализованы
3. ✅ UI система работает
4. ✅ HUD отображает информацию
5. ✅ Обработка ввода синхронизирована
6. ✅ Нет утечек памяти
7. ✅ FPS ≥ 60
