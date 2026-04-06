package io.github.example.presentation.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.example.presentation.PresentationLayer;
import io.github.example.presentation.input.InputHandler;
import io.github.example.presentation.screens.MenuScreen;
import io.github.example.presentation.screens.GameScreen;
import io.github.example.presentation.screens.Screen;
import io.github.example.presentation.screens.InventoryScreen;
import io.github.example.presentation.util.Logger;
import io.github.example.domain.service.GameService;
import io.github.example.domain.service.GameSession;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Player;

/**
 * Главное приложение LibGDX для игры Roguelike.
 * Реализует ApplicationListener интерфейс и управляет жизненным циклом приложения.
 */
public class LibGdxGameApplicationListener implements ApplicationListener {
    private SpriteBatch batch;
    private PresentationLayer presentationLayer;
    private InputHandler inputHandler;
    private GameService gameService;
    private GameScreen currentGameScreen; // Ссылка на текущий GameScreen для InventoryScreen

    @Override
    public void create() {
        Logger.info("Приложение запускается...");

        // Инициализируем основные компоненты
        batch = new SpriteBatch();
        inputHandler = new InputHandler();
        presentationLayer = new PresentationLayer(1920, 1080);
        gameService = new GameService();

        // Устанавливаем обработчик ввода
        Gdx.input.setInputProcessor(inputHandler);

        // Создаем и устанавливаем главное меню как начальный экран
        final MenuScreen[] currentMenuScreen = new MenuScreen[1];
        currentMenuScreen[0] = new MenuScreen(new MenuScreen.MenuCallback() {
            @Override
            public void onNewGame() {
                Logger.info("Начало новой игры");
                startNewGame();
            }

            @Override
            public void onLoadGame() {
                Logger.info("Загрузка игры");
                // TODO: Загрузить сохраненную игру
            }

            @Override
            public void onLeaderboard() {
                Logger.info("Открытие таблицы лидеров");
                // TODO: Открыть экран таблицы лидеров
            }

            @Override
            public void onExit() {
                Logger.info("Выход из игры");
                Gdx.app.exit();
            }
        });

        // Устанавливаем обработчик ввода для меню
        inputHandler.setInputListener(new InputHandler.InputListener() {
            @Override
            public void onMove(io.github.example.domain.service.Direction direction) {}

            @Override
            public void onAction(InputHandler.Action action) {}

            @Override
            public void onMenuInput(InputHandler.MenuInput input) {
                // Проверяем текущий экран и отправляем ввод ему
                Screen currentScreen = presentationLayer.getCurrentScreen();

                if (currentScreen instanceof MenuScreen) {
                    // Конвертируем InputHandler.MenuInput в MenuScreen.MenuInput
                    MenuScreen.MenuInput screenInput = MenuScreen.MenuInput.valueOf(input.name());
                    ((MenuScreen) currentScreen).handleMenuInput(screenInput);
                } else if (currentScreen instanceof InventoryScreen) {
                    // Передаём ввод в InventoryScreen
                    ((InventoryScreen) currentScreen).handleMenuInput(input);
                }
            }

            @Override
            public void onItemUse(int slotIndex) {}

            @Override
            public void onWait() {}

            @Override
            public void onToggleInventory() {}

            @Override
            public void onTogglePause() {}

            @Override
            public void onQuitToMenu() {
                Gdx.app.exit();
            }
        });

        presentationLayer.setInitialScreen(currentMenuScreen[0]);

        Logger.info("Приложение готово!");
    }

    /**
     * Запускает новую игру: создаёт GameService и переходит на GameScreen
     */
    private void startNewGame() {
        try {
            Logger.info("Инициализация новой игры...");

            // Создаём новую игровую сессию
            gameService.startNewGame();
            Logger.info("GameService инициализирован, начало на уровне 1");

            // Создаём GameScreen с GameService и inputHandler
            GameScreen gameScreen = new GameScreen(
                gameService,
                presentationLayer.getMainRenderer(),
                inputHandler,
                presentationLayer.getAssetManager()
            );

            // Сохраняем ссылку на GameScreen для InventoryScreen
            this.currentGameScreen = gameScreen;

            // Устанавливаем callback для паузы/выхода
            gameScreen.setCallback(new GameScreen.GameCallback() {
                @Override
                public void onPause() {
                    Logger.info("Игра паузирована");
                    // TODO: Показать экран паузы
                }

                @Override
                public void onGameOver() {
                    Logger.info("Игра окончена");
                    // TODO: Показать экран смерти, затем вернуться в меню
                    presentationLayer.transitionToScreen(new MenuScreen(
                        new MenuScreen.MenuCallback() {
                            @Override
                            public void onNewGame() {
                                startNewGame();
                            }
                            @Override
                            public void onLoadGame() {}
                            @Override
                            public void onLeaderboard() {}
                            @Override
                            public void onExit() {
                                Gdx.app.exit();
                            }
                        }
                    ));
                }

                @Override
                public void onToggleInventory() {
                    Logger.info("Инвентарь переключен");
                    if (currentGameScreen != null) {
                        // Получить игрока
                        GameSession session = gameService.getSession();
                        if (session == null) {
                            Logger.warn("Cannot open inventory: GameSession is null");
                            return;
                        }
                        Player player = session.getPlayer();
                        if (player == null) {
                            Logger.warn("Cannot open inventory: Player is null");
                            return;
                        }

                        // Переключить на InventoryScreen
                        Screen inventoryScreen = new InventoryScreen(
                            player,
                            gameService,
                            new InventoryScreen.InventoryCallback() {
                                @Override
                                public void onClose() {
                                    // Callback: вернуться на GameScreen
                                    if (currentGameScreen != null) {
                                        presentationLayer.transitionToScreen(currentGameScreen);
                                    }
                                }
                            },
                            inputHandler
                        );
                        presentationLayer.transitionToScreen(inventoryScreen);
                    }
                }

                @Override
                public void onQuitToMenu() {
                    Logger.info("Возврат в меню");
                    presentationLayer.transitionToScreen(new MenuScreen(
                        new MenuScreen.MenuCallback() {
                            @Override
                            public void onNewGame() {
                                startNewGame();
                            }
                            @Override
                            public void onLoadGame() {}
                            @Override
                            public void onLeaderboard() {}
                            @Override
                            public void onExit() {
                                Gdx.app.exit();
                            }
                        }
                    ));
                }
            });

            // Переходим на GameScreen
            presentationLayer.setScreen(gameScreen);
            Logger.info("Игра запущена!");

        } catch (Exception e) {
            Logger.error("Ошибка при запуске новой игры: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void render() {
        // Очищаем экран черным цветом
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Вычисляем delta time
        float delta = Gdx.graphics.getDeltaTime();

        // Обновляем и отрисовываем presentation слой
        presentationLayer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        Logger.debug("Окно изменило размер: " + width + "x" + height);
        presentationLayer.resize(width, height);
    }

    @Override
    public void pause() {
        Logger.debug("Приложение паузировано");
    }

    @Override
    public void resume() {
        Logger.debug("Приложение возобновлено");
    }

    @Override
    public void dispose() {
        Logger.info("Приложение завершается...");
        presentationLayer.dispose();
        batch.dispose();
        Logger.info("Приложение закрыто");
    }
}
