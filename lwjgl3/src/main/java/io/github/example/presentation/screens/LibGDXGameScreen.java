package io.github.example.presentation.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.example.domain.service.GameService;
import io.github.example.domain.service.GameState;
import io.github.example.domain.service.Direction;
import io.github.example.presentation.util.Logger;
import io.github.example.presentation.renderer.layers.TileLayerRenderer;
import io.github.example.presentation.renderer.layers.ActorLayerRenderer;
import io.github.example.presentation.renderer.layers.ItemLayerRenderer;
import io.github.example.presentation.renderer.layers.UILayerRenderer;
import io.github.example.presentation.renderer.layers.FogLayerRenderer;
import io.github.example.presentation.camera.CameraController;

/**
 * Главный игровой экран LibGDX версии.
 *
 * Отвечает за:
 * - Инициализацию OrthographicCamera (960x540)
 * - Управление SpriteBatch
 * - Координацию рендеринга всех слоёв (tiles, actors, items, fog, ui)
 * - Обработка ввода игрока
 * - Управление жизненным циклом игры на этом экране
 */
public class LibGDXGameScreen implements Screen {

    // ========== Константы ==========
    private static final int SCREEN_WIDTH = 960;
    private static final int SCREEN_HEIGHT = 540;
    private static final int TILE_SIZE = 32;
    private static final float VIEWPORT_WIDTH = (float) SCREEN_WIDTH / TILE_SIZE;  // 30 тайлов
    private static final float VIEWPORT_HEIGHT = (float) SCREEN_HEIGHT / TILE_SIZE;  // 16.875 тайлов

    // ========== Компоненты LibGDX ==========
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FrameBuffer frameBuffer;
    private CameraController cameraController;

    // ========== Рендеринг слоёв ==========
    private TileLayerRenderer tileLayerRenderer;
    private ActorLayerRenderer actorLayerRenderer;
    private ItemLayerRenderer itemLayerRenderer;
    private UILayerRenderer uiLayerRenderer;
    private FogLayerRenderer fogLayerRenderer;
    private BitmapFont font;

    // ========== Игровые компоненты ==========
    private GameService gameService;
    private boolean isPaused = false;

    // ========== Состояние ввода ==========
    private boolean[] keyPressed = new boolean[256];

    /**
     * Конструктор экрана
     * @param gameService сервис управления игрой
     */
    public LibGDXGameScreen(GameService gameService) {
        this.gameService = gameService;
        Logger.info("LibGDXGameScreen created");
    }

    /**
     * Вызывается когда экран становится активным
     */
    @Override
    public void show() {
        Logger.info("LibGDXGameScreen show() - initializing rendering components");

        // Инициализация SpriteBatch
        batch = new SpriteBatch();
        Logger.info("SpriteBatch initialized");

        // Инициализация TileLayerRenderer
        tileLayerRenderer = new TileLayerRenderer();
        Logger.info("TileLayerRenderer initialized");

        // Инициализация ActorLayerRenderer
        actorLayerRenderer = new ActorLayerRenderer();
        Logger.info("ActorLayerRenderer initialized");

        // Инициализация ItemLayerRenderer
        itemLayerRenderer = new ItemLayerRenderer();
        Logger.info("ItemLayerRenderer initialized");

        // Инициализация UILayerRenderer
        font = new BitmapFont();
        uiLayerRenderer = new UILayerRenderer(batch, font);
        Logger.info("UILayerRenderer initialized");

        // Инициализация FogLayerRenderer
        fogLayerRenderer = new FogLayerRenderer(batch);
        Logger.info("FogLayerRenderer initialized");

        // Инициализация OrthographicCamera
        // 960px / 32px = 30 тайлов по X
        // 540px / 32px = 16.875 тайлов по Y
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(VIEWPORT_WIDTH / 2f, VIEWPORT_HEIGHT / 2f, 0);
        camera.update();
        Logger.info("OrthographicCamera initialized: " + VIEWPORT_WIDTH + "x" + VIEWPORT_HEIGHT + " tiles");

        // Инициализация FrameBuffer для рендеринга
        frameBuffer = new FrameBuffer(com.badlogic.gdx.graphics.Pixmap.Format.RGB888, SCREEN_WIDTH, SCREEN_HEIGHT, false);
        Logger.info("FrameBuffer initialized: " + SCREEN_WIDTH + "x" + SCREEN_HEIGHT);

        // Инициализация CameraController
        cameraController = new CameraController(camera, gameService.getSession().getCurrentLevel(), VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        cameraController.setSmoothness(0.15f);  // Немного плавное движение
        Logger.info("CameraController initialized");
    }

    /**
     * Главный цикл рендеринга
     * @param delta время с последнего фрейма
     */
    @Override
    public void render(float delta) {
        // Обработка ввода
        handleInput();

        // Обновление логики игры (если не на паузе)
        if (!isPaused && gameService.getSession() != null) {
            updateGameLogic();
        }

        // Обновление камеры с использованием CameraController
        if (gameService.getSession() != null && gameService.getSession().getPlayer() != null && cameraController != null) {
            cameraController.update(gameService.getSession().getPlayer(), delta);
        }
        camera.update();

        // Установка projection matrix для батча
        batch.setProjectionMatrix(camera.combined);

        // Очистка экрана
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рендеринг
        batch.begin();
        renderGame();
        batch.end();
    }

    /**
     * Обработка ввода игрока
     */
    private void handleInput() {
        // Обработка WASD и стрелок
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            gameService.processPlayerAction(Direction.UP);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            gameService.processPlayerAction(Direction.DOWN);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            gameService.processPlayerAction(Direction.LEFT);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            gameService.processPlayerAction(Direction.RIGHT);
        }

        // Обработка использования предметов (1-9)
        for (int i = 0; i < 9; i++) {
            int keyCode = Input.Keys.NUM_1 + i;
            if (Gdx.input.isKeyJustPressed(keyCode)) {
                gameService.useItemFromBackpack(i, false);
            }
        }

        // Пауза (P или ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            Logger.info(isPaused ? "Game paused" : "Game resumed");
        }

        // Выход (Q)
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.exit();
        }
    }

    /**
     * Обновление логики игры
     */
    private void updateGameLogic() {
        // В пошаговой системе логика обновляется через processPlayerAction
        // Здесь можно добавить другие обновления (анимации, эффекты, etc)
    }

    /**
     * Обновление камеры для следования за игроком
     */
    private void updateCamera() {
        // Центр камеры на позиции игрока
        float playerX = gameService.getSession().getPlayer().getCoordinates().getX();
        float playerY = gameService.getSession().getPlayer().getCoordinates().getY();

        // Плавное следование (сейчас идёт жёсткое центрирование, позже сделаем smooth)
        camera.position.x = playerX;
        camera.position.y = playerY;

        // Границы камеры по уровню (200x200 тайлов)
        float minX = VIEWPORT_WIDTH / 2f;
        float maxX = 200 - VIEWPORT_WIDTH / 2f;
        float minY = VIEWPORT_HEIGHT / 2f;
        float maxY = 200 - VIEWPORT_HEIGHT / 2f;

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
    }

    /**
     * Рендеринг всех слоёв игры
     */
    private void renderGame() {
        if (gameService.getSession() == null) {
            return;
        }

        // Фаза 1.4 - TileLayerRenderer (готово)
        tileLayerRenderer.render(batch,
        gameService.getSession().getCurrentLevel(),
        camera.position.x,
        camera.position.y,
        VIEWPORT_WIDTH,
        VIEWPORT_HEIGHT);

        // Фаза 2.1 - ActorLayerRenderer (готово)
        actorLayerRenderer.render(batch,
                                 gameService.getSession().getCurrentLevel(),
                                 gameService.getSession().getPlayer(),
                                 camera.position.x,
                                 camera.position.y,
                                 VIEWPORT_WIDTH,
                                 VIEWPORT_HEIGHT);

        // Фаза 2.2 - ItemLayerRenderer (готово)
        itemLayerRenderer.render(batch,
                                gameService.getSession().getCurrentLevel());

        // Фаза 2.4 - FogLayerRenderer (туман войны)
        fogLayerRenderer.render(batch,
                               gameService.getSession().getCurrentLevel(),
                               gameService.getSession().getPlayer(),
                               camera.position.x,
                               camera.position.y,
                               VIEWPORT_WIDTH,
                               VIEWPORT_HEIGHT);

        // Фаза 2.3 - UILayerRenderer (HUD)
        uiLayerRenderer.render(batch,
                              gameService.getSession().getCurrentLevel(),
                              gameService.getSession().getPlayer(),
                              Gdx.graphics.getWidth(),
                              Gdx.graphics.getHeight());
    }

    /**
     * Тестовый рендеринг сетки тайлов (белые квадраты) - УСТАРЕВШЕЕ
     * @deprecated используйте TileLayerRenderer вместо этого
     */
    @Deprecated
    private void renderTilesTest() {
        // Создание белой текстуры 1x1 для рисования прямоугольников
        Texture whitePixel = createWhitePixelTexture();

        // Получить текущий уровень
        var level = gameService.getSession().getCurrentLevel();
        if (level == null) return;

        // Рендер видимых тайлов
        int startX = Math.max(0, (int)camera.position.x - (int)VIEWPORT_WIDTH);
        int startY = Math.max(0, (int)camera.position.y - (int)VIEWPORT_HEIGHT);
        int endX = Math.min(200, (int)camera.position.x + (int)VIEWPORT_WIDTH + 1);
        int endY = Math.min(200, (int)camera.position.y + (int)VIEWPORT_HEIGHT + 1);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                var tile = level.getTiles()[y][x];

                // Цвет в зависимости от типа тайла
                float r, g, b;
                switch (tile.getSpaceType()) {
                    case Wall -> { r = 0.3f; g = 0.3f; b = 0.3f; }  // Тёмно-серый
                    case Room -> { r = 0.7f; g = 0.7f; b = 0.7f; }  // Светло-серый
                    case Passage -> { r = 0.5f; g = 0.5f; b = 0.5f; }  // Средний серый
                    default -> { r = 0.1f; g = 0.1f; b = 0.1f; }  // Чёрный
                }

                // Рисование квадрата
                batch.setColor(r, g, b, 1f);
                batch.draw(whitePixel, x, y, 1, 1);
            }
        }

        batch.setColor(1, 1, 1, 1);  // Сброс цвета
        whitePixel.dispose();
    }

    /**
     * Создание белой текстуры 1x1 для рисования прямоугольников
     * Использует TextureFilter.Nearest для пиксель-арта
     */
    private Texture createWhitePixelTexture() {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        // Используем Nearest фильтр для пиксель-арта
        texture.setFilter(com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest,
                         com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest);

        return texture;
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = VIEWPORT_WIDTH;
        camera.viewportHeight = VIEWPORT_HEIGHT;
        camera.update();
    }

    @Override
    public void pause() {
        isPaused = true;
        Logger.info("Screen paused");
    }

    @Override
    public void resume() {
        Logger.info("Screen resumed");
    }

    @Override
    public void hide() {
        Logger.info("Screen hidden");
    }

    @Override
    public void dispose() {
        Logger.info("LibGDXGameScreen disposed");
        if (batch != null) batch.dispose();
        if (frameBuffer != null) frameBuffer.dispose();
        if (tileLayerRenderer != null) tileLayerRenderer.dispose();
        if (actorLayerRenderer != null) actorLayerRenderer.dispose();
        if (itemLayerRenderer != null) itemLayerRenderer.dispose();
        if (uiLayerRenderer != null) uiLayerRenderer.dispose();
        if (fogLayerRenderer != null) fogLayerRenderer.dispose();
        if (font != null) font.dispose();
    }
}
