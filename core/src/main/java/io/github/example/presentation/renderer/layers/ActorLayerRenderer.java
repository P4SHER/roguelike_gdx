package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.domain.entities.Enemy;
import io.github.example.domain.entities.Player;
import io.github.example.domain.level.Coordinates;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Constants;
import java.util.*;

/**
 * Рендерер слоя персонажей (Player и Enemies).
 * Использует Z-order по Y-координате для правильной перспективы.
 */
public class ActorLayerRenderer extends AbstractLayerRenderer {
    private final Player player;
    private final List<Enemy> enemies;
    private final AssetManager assetManager;
    private int actorsRendered;

    public ActorLayerRenderer(Player player, List<Enemy> enemies, AssetManager assetManager) {
        super("ActorLayer");
        this.player = player;
        this.enemies = enemies != null ? enemies : new ArrayList<>();
        this.assetManager = assetManager;
    }

    @Override
    public void init() {
        super.init();
        debugLog("Инициализирован с игроком и " + enemies.size() + " врагами");
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible) {
            return;
        }

        actorsRendered = 0;
        Rectangle visibleBounds = new Rectangle(
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight
        );

        // Собираем всех персонажей для сортировки
        List<ActorRenderData> actors = new ArrayList<>();

        // Добавляем игрока
        if (player != null && player.isAlive()) {
            Coordinates playerCoord = player.getCoordinates();
            if (playerCoord != null && cullingSystem.isVisible(playerCoord, visibleBounds)) {
                actors.add(new ActorRenderData(
                    player.getCoordinates().getY(),
                    () -> renderPlayer(batch, player)
                ));
            }
        }

        // Добавляем врагов
        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.isAlive()) {
                Coordinates enemyCoord = enemy.getCoordinates();
                if (enemyCoord != null && cullingSystem.isVisible(enemyCoord, visibleBounds)) {
                    actors.add(new ActorRenderData(
                        enemy.getCoordinates().getY(),
                        () -> renderEnemy(batch, enemy)
                    ));
                }
            }
        }

        // Сортируем по Y для правильного Z-order (перспектива)
        sortActorsByYCoord(actors);

        // Отрисовываем в порядке
        for (ActorRenderData actor : actors) {
            actor.render();
            actorsRendered++;
        }

        debugLog("Отрисовано персонажей: " + actorsRendered);
    }

    /**
     * Отрисовывает игрока.
     * Использует спрайты из Kenney Micro Roguelike.
     */
    private void renderPlayer(SpriteBatch batch, Player player) {
        Coordinates coord = player.getCoordinates();
        if (coord == null) {
            return;
        }

        float x = coord.getX() * Constants.TILE_SIZE;
        float y = coord.getY() * Constants.TILE_SIZE;

        String spritePath = "characters/player"; // Kenney player sprite
        Sprite sprite = assetManager.getSprite(spritePath);

        if (sprite != null) {
            sprite.setPosition(x, y);
            sprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
            sprite.draw(batch);
        }
    }

    /**
     * Отрисовывает врага.
     * Использует спрайты из Kenney Micro Roguelike.
     */
    private void renderEnemy(SpriteBatch batch, Enemy enemy) {
        Coordinates coord = enemy.getCoordinates();
        if (coord == null || !enemy.isVisible()) {
            return;
        }

        float x = coord.getX() * Constants.TILE_SIZE;
        float y = coord.getY() * Constants.TILE_SIZE;

        String spritePath = getEnemySpriteForType(enemy.getType());
        Sprite sprite = assetManager.getSprite(spritePath);

        if (sprite != null) {
            sprite.setPosition(x, y);
            sprite.setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
            sprite.draw(batch);
        }
    }

    /**
     * Получает путь спрайта для типа врага.
     * Использует спрайты из Kenney Micro Roguelike.
     */
    private String getEnemySpriteForType(Object enemyType) {
        if (enemyType == null) {
            return "characters/enemy_zombie";
        }

        String typeName = enemyType.toString();
        switch (typeName) {
            case "ZOMBIE":
                return "characters/enemy_zombie";
            case "VAMPIRE":
                return "characters/enemy_vampire";
            case "GHOST":
                return "characters/enemy_ghost";
            case "OGRE":
                return "characters/enemy_ogre";
            case "SNAKE_MAGE":
                return "characters/enemy_snake";
            default:
                return "characters/enemy_zombie";
        }
    }

    /**
     * Сортирует персонажей по Y-координате (от меньшей к большей).
     * Это обеспечивает правильный Z-order для изометрической перспективы.
     */
    private void sortActorsByYCoord(List<ActorRenderData> actors) {
        actors.sort((a, b) -> Integer.compare(a.yCoord, b.yCoord));
    }

    /**
     * Получает количество отрисованных персонажей.
     */
    public int getActorsRendered() {
        return actorsRendered;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Вспомогательный класс для хранения данных о персонаже для рендеринга.
     */
    private static class ActorRenderData {
        final int yCoord;
        final Runnable renderCallback;

        ActorRenderData(int yCoord, Runnable renderCallback) {
            this.yCoord = yCoord;
            this.renderCallback = renderCallback;
        }

        void render() {
            renderCallback.run();
        }
    }
}
