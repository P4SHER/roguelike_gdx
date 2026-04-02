# LibGDX Implementation Plan для Roguelike

## 📋 Обзор

Интеграция **LibGDX** в текущий проект, который сейчас использует Lanterna для терминального UI.
После завершения этого плана Lanterna будет удалена, и LibGDX станет основным рендерером.

**Целевые спецификации:**
- Разрешение: **1920x1080**
- Графика: **Пиксельная 16-бит** (RGBA565)
- Размер тайла: **32x32 пиксели**
- Режимы окна: **Оконный + Полноэкранный** (выбор в меню)
- Камера: **Масштабируемая, следует за игроком**
- UI: **Минимальный** (основной HUD: HP, инвентарь, логи)
- Управление UI: **WASD** для переключения между элементами

---

## 🏗️ Архитектура

### Текущая структура
```
core/
  domain/              ← Игровая логика (НЕИЗМЕННА)
  datalayer/           ← Сохранения (НЕИЗМЕННА)
  presentation/        ← Пусто, будет реализована
  presentation_lanterna/  ← Удалится в конце
lwjgl3/
  src/main/java/       ← Launcher
```

### После реализации LibGDX
```
core/
  presentation/
    libgdx/
      screens/         ← GameScreen, MenuScreen
      renderers/       ← DungeonRenderer, UIRenderer, ActorRenderer
      assets/          ← AssetManager, TextureAtlas loading
      input/           ← InputHandler с WASD
      ui/              ← UIPanel, Button, HUD components
```

---

## 📦 Фазы реализации

### Фаза 1: Базовая рендеринг инфраструктура
**Зависит от:** Ничего  
**Результат:** Окно LibGDX открывается и показывает чёрный экран

#### Задачи:
1. **Добавить LibGDX зависимости** в `core/build.gradle`
   - `gdx-tools` для построения атласов
   - Опционально: `gdx-freetype` (уже есть) для шрифтов

2. **Создать `GameAssetManager`** в `presentation/assets/`
   - Загрузка спрайтов (32x32 тайлы)
   - Текстуры врагов, предметов, окружения
   - Шрифты для UI
   - Использовать TextureAtlas для оптимизации

3. **Создать `LibGdxGameScreen`** в `presentation/libgdx/screens/`
   - Наследует `Screen` от LibGDX
   - Реализует `render(float delta)` пусто (просто очищает экран)
   - Инициализирует `SpriteBatch` и `ShapeRenderer`

4. **Модифицировать `lwjgl3/Lwjgl3Launcher`**
   - Вместо Lanterna использует `Lwjgl3Application`
   - Передаёт `LibGdxGameScreen` как начальный экран

5. **Создать конфиг окна** в `lwjgl3/src/main/resources/`
   - 1920x1080, поддержка полноэкрана
   - V-sync включен, 60 FPS

---

### Фаза 2: Рендеринг игрового мира
**Зависит от:** Фаза 1  
**Результат:** Видны тайлы подземелья, персонаж, враги

#### Задачи:

1. **Создать `DungeonRenderer`** в `presentation/libgdx/renderers/`
   - Получает `Level` из `GameService`
   - Рисует все тайлы через SpriteBatch
   - Использует координаты: screen_x = (world_x - camera_x) * 32
   - Обработка видимости (fog of war через маску/альфу)

2. **Создать `ActorRenderer`** в `presentation/libgdx/renderers/`
   - Рисует `Player`, `Enemy` на карте
   - Спрайты врагов в зависимости от `EnemyType`
   - Слои: фон → враги → игрок → эффекты

3. **Создать `CameraController`** в `presentation/libgdx/`
   - Камера следует за игроком с интерполяцией
   - Границы (не выходит за края карты)
   - Масштабирование под разрешение экрана
   - Формула: зум = window_width / (level_width * tile_size)

4. **Интегрировать в `LibGdxGameScreen.render()`**
   - Обновить камеру
   - Отрисовать подземелье
   - Отрисовать врагов/игрока

---

### Фаза 3: Минимальный UI HUD
**Зависит от:** Фаза 2  
**Результат:** На экране виден HUD с HP, инвентарём, логом действий

#### Задачи:

1. **Создать `UIPanel`** в `presentation/libgdx/ui/`
   - Базовый контейнер для UI элементов
   - Поддержка разделов: top (информация), bottom (лог), left (инвентарь)

2. **Создать `HUDRenderer`** в `presentation/libgdx/renderers/`
   - Показывает HP игрока/врага при наведении
   - Текущий уровень
   - Лог последних 5 действий (боевые, движение)
   - Использует `BitmapFont` из LibGDX

3. **Создать `InventoryPanel`** в `presentation/libgdx/ui/`
   - Список предметов (слева или справа)
   - Выбор через WASD (навигация)
   - Показывает описание выбранного предмета

4. **Создать `ActionLogger`** в `presentation/libgdx/ui/`
   - Очередь текстовых событий
   - Автоматическое удаление старых (max 5 строк)
   - Цветной текст (боевые → красный, лут → жёлтый)

---

### Фаза 4: Управление вводом
**Зависит от:** Фаза 2, 3  
**Результат:** Игрок может двигаться, атаковать, управлять интерфейсом

#### Задачи:

1. **Создать `InputHandler`** в `presentation/libgdx/input/`
   - Слушатель клавиш WASD, Enter, ESC
   - Передаёт команды в `GameService`
   - Режимы ввода: движение, меню, инвентарь

2. **Интегрировать в `LibGdxGameScreen`**
   - `InputMultiplexer` для обработки нескольких слушателей
   - Камера не обновляется, если открыто меню

3. **Команды:**
   - **W/A/S/D**: Движение игрока (→ `MovementService.move()`)
   - **Enter**: Подтверждение (использовать предмет)
   - **ESC**: Пауза / открыть меню
   - **I**: Открыть инвентарь
   - **Tab**: Следующий враг / UI элемент

---

### Фаза 5: Меню и состояния игры
**Зависит от:** Фаза 1, 4  
**Результат:** Главное меню, пауза, выбор режима (оконный/полноэкран)

#### Задачи:

1. **Создать `MenuScreen`** в `presentation/libgdx/screens/`
   - Главное меню с кнопками: Start, Settings, Exit
   - Навигация через WASD, Enter для выбора

2. **Создать `PauseScreen`** в `presentation/libgdx/screens/`
   - Показывает возможность: Resume, Save, Load, Quit

3. **Создать `SettingsScreen`** в `presentation/libgdx/screens/`
   - Переключение Windowed/Fullscreen
   - Громкость (если музыка/звуки добавятся позже)
   - Масштаб UI

4. **Создать `GameOverScreen`** в `presentation/libgdx/screens/`
   - Результат: Win/Lose
   - Показать время, уровень достижения
   - Кнопки: Retry, MainMenu

---

### Фаза 6: Анимации и эффекты
**Зависит от:** Фаза 2, 3  
**Результат:** Плавные движения персонажей, эффекты урона, дыма

#### Задачи:

1. **Создать `AnimationManager`** в `presentation/libgdx/`
   - Поддержка spritesheet анимаций
   - Плавное перемещение персонажей между тайлами
   - Кэширование frames

2. **Создать `ParticleSystem`** в `presentation/libgdx/`
   - Эффекты урона (красный текст + вспышка)
   - Дым на смерть врага
   - Сверкание при подборе предмета

3. **Интегрировать в `ActorRenderer` и `DungeonRenderer`**
   - Обновлять анимации в `render(delta)`

---

### Фаза 7: Интеграция сохранений
**Зависит от:** Фаза 5  
**Результат:** Можно сохранить игру, загрузить, продолжить

#### Задачи:

1. **Интегрировать `SaveService`** в LibGDX экраны
   - При Save → вызвать `SaveService.save(gameState)`
   - При Load → `SaveService.load()` и восстановить состояние
   - Показать UI подтверждения

2. **Добавить горячие клавиши**
   - Ctrl+S: Быстрое сохранение
   - Ctrl+L: Быстрая загрузка

---

### Фаза 8: Удаление Lanterna
**Зависит от:** Все фазы 1-7  
**Результат:** Lanterna полностью удалена из проекта

#### Задачи:

1. **Удалить модули:**
   - `core/src/main/java/io/github/example/presentation_lanterna/`
   - Зависимость Lanterna из `build.gradle`

2. **Удалить старые классы:**
   - `MainRenderer`, `TurnRenderer`, `ActorsRenderer`, `ColorScheme`

3. **Обновить `FirstScreen` / `Main`:**
   - Теперь запускается `GameScreen` (LibGDX), а не Lanterna

4. **Тестирование:**
   - Убедиться, что `gradlew lwjgl3:run` всё работает

---

## 🎨 Технические детали

### Пиксельная графика в LibGDX

**Масштабирование:** Ближайший сосед (Nearest Neighbor), не линейная интерполяция
```java
texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
```

**Размер спрайтов:**
- Тайл: 32x32 пиксела
- Персонаж: 32x32 (может использовать подмножество тайла)
- Враги: 32x32
- Предметы: 16x16 или 32x32

**Палитра:** 16-бит (RGB565) или RGBA8888 (для прозрачности)

### Камера и проекция

```
Камера (Orthographic):
  - Размер: (1920/32) x (1080/32) = 60 x 33.75 тайла
  - Позиция: center = player_position
  - Границы: не выходит за края Level
```

### Производительность

- SpriteBatch вмещает 8191 спрайт за раз (достаточно для подземелья)
- Cull invisible tiles вне видимости камеры
- Использовать TextureAtlas вместо отдельных текстур

---

## 📝 Соглашения кода (специфично для LibGDX слоя)

### Структура файлов
```
presentation/libgdx/
  screens/              ← Screen наследники
    GameScreen.java
    MenuScreen.java
    PauseScreen.java
    SettingsScreen.java
    GameOverScreen.java
  
  renderers/            ← Отрисовка компонентов
    DungeonRenderer.java
    ActorRenderer.java
    HUDRenderer.java
    UIRenderer.java
  
  ui/                   ← UI компоненты
    UIPanel.java
    InventoryPanel.java
    ActionLogger.java
    Button.java
  
  input/                ← Обработка ввода
    InputHandler.java
    InputMode.java      ← enum
  
  assets/               ← Менеджеры ресурсов
    GameAssetManager.java
    SpriteManager.java
  
  camera/               ← Камера и движение
    CameraController.java
  
  animation/            ← Анимации и эффекты
    AnimationManager.java
    ParticleSystem.java
```

### Именование
- **Renderers**: наследуют или используют `SpriteBatch` / `ShapeRenderer`
- **Screens**: наследуют `Screen`
- **UI Components**: методы `draw(SpriteBatch)`, `update(float delta)`, `handleInput(int key)`
- **Constants**: `TILE_SIZE = 32`, `SCREEN_WIDTH = 1920`, хранить в `GameConfig`

### Зависимости в domain
**ВООБЩЕ НЕ МЕНЯТЬ!** Domain остаётся UI-агностичным.
- `GameService`, `MovementService`, `CombatService` — вызываются из `InputHandler`
- Результаты (`GameState`, `CombatResult`, `MoveResult`) читаются в `Renderers`

---

## 🧪 Тестирование по фазам

| Фаза | Проверка |
|------|----------|
| 1 | `gradlew lwjgl3:run` открывает окно 1920x1080, чёрный экран, нет ошибок |
| 2 | Видны тайлы подземелья, враги, игрок в центре экрана |
| 3 | HUD: HP, инвентарь, лог действий отображаются правильно |
| 4 | WASD двигает игрока, враги реагируют на ход |
| 5 | Меню, Settings, Pause работают; переключение режима окна |
| 6 | Персонажи плавно двигаются, видны эффекты атак |
| 7 | Ctrl+S сохраняет, Ctrl+L загружает игру |
| 8 | Lanterna удалена, всё работает, тесты проходят |

---

## ⚠️ Риски и моменты

1. **Масштабирование под разные экраны** — камера зум должен быть адаптивным
2. **Производительность тайлов** — на больших картах могут быть лаги (решение: culling)
3. **Выравнивание текста в UI** — LibGDX BitmapFont работает иначе, чем Lanterna
4. **Анимации и timing** — delta time может быть нестабильным (использовать fixed timestep)
5. **Сохранения и совместимость** — убедиться, что SaveService не зависит от Lanterna

---

## 📅 Порядок выполнения

1. ✅ Фаза 1 (базовая инфраструктура)
2. ✅ Фаза 2 (рендеринг мира)
3. ✅ Фаза 3 (UI HUD)
4. ✅ Фаза 4 (управление)
5. ✅ Фаза 5 (меню)
6. ⚙️ Фаза 6 (анимации — опционально, если время)
7. ✅ Фаза 7 (сохранения)
8. ✅ Фаза 8 (удаление Lanterna)

**Критические фазы:** 1, 2, 4, 7, 8  
**Можно пропустить/отложить:** 6 (частично)

---

## 📚 Дополнительные ресурсы

- **LibGDX Wiki**: https://libgdx.com/wiki/
- **Pixel Art масштабирование**: https://libgdx.com/wiki/graphics/orthographic-camera
- **TextureAtlas в LibGDX**: https://libgdx.com/wiki/graphics/textures-atlases
- **Input Handling**: https://libgdx.com/wiki/input/keyboard-handling

---

**Создано для:** LibGDX Roguelike Project  
**Дата:** 2026-04-02  
**Версия плана:** 1.0
