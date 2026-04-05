package io.github.example.presentation.renderer.layers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import io.github.example.domain.entities.Item;
import io.github.example.domain.entities.ItemType;
import io.github.example.domain.level.Coordinates;
import io.github.example.domain.level.Level;
import io.github.example.presentation.assets.AssetManager;
import io.github.example.presentation.util.Constants;
import java.util.List;

/**
 * Рендерер слоя предметов.
 * Отрисовывает предметы на полу с простой анимацией парения.
 */
public class ItemLayerRenderer extends AbstractLayerRenderer {
    private final Level level;
    private final AssetManager assetManager;
    private float animationTime;
    private int itemsRendered;

    public ItemLayerRenderer(Level level, AssetManager assetManager) {
        super("ItemLayer");
        this.level = level;
        this.assetManager = assetManager;
        this.animationTime = 0;
    }

    @Override
    public void init() {
        super.init();
        debugLog("Инициализирован для уровня");
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera, float delta) {
        if (!isVisible || level == null) {
            return;
        }

        itemsRendered = 0;
        animationTime += delta;

        Rectangle visibleBounds = new Rectangle(
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight
        );

        List<Item> items = level.getAllItems();
        if (items == null || items.isEmpty()) {
            return;
        }

        for (Item item : items) {
            if (item == null) {
                continue;
            }

            Coordinates itemCoord = item.getPos();
            if (itemCoord == null || !cullingSystem.isVisible(itemCoord, visibleBounds)) {
                continue;
            }

            renderItem(batch, item, delta);
            itemsRendered++;
        }

        debugLog("Отрисовано предметов: " + itemsRendered);
    }

    /**
     * Отрисовывает один предмет с анимацией парения.
     */
    private void renderItem(SpriteBatch batch, Item item, float delta) {
        Coordinates coord = item.getPos();
        if (coord == null) {
            return;
        }

        float baseX = coord.getX() * Constants.TILE_SIZE;
        float baseY = coord.getY() * Constants.TILE_SIZE;

        // Простая анимация парения
        float hoverOffset = (float) Math.sin(animationTime * 3) * 4; // Колебание в 4 пикселя
        float y = baseY + Constants.TILE_SIZE / 2 + hoverOffset;

        String spritePath = getItemSpriteForType(item.getType());
        Sprite sprite = assetManager.getSprite(spritePath);

        if (sprite != null) {
            sprite.setPosition(baseX, y);
            sprite.setSize(Constants.TILE_SIZE * 0.75f, Constants.TILE_SIZE * 0.75f);
            sprite.setAlpha(0.9f);
            sprite.draw(batch);
            sprite.setAlpha(1.0f);
        }
    }

    /**
     * Получает путь спрайта для типа предмета.
     */
    private String getItemSpriteForType(ItemType type) {
        if (type == null) {
            return "items/generic";
        }

        switch (type) {
            case WEAPON:
                return "items/weapon";
            case TREASURE:
                return "items/treasure";
            case FOOD:
                return "items/food";
            case ELIXIR:
                return "items/elixir";
            case SCROLL:
                return "items/scroll";
            case EMPTY:
            default:
                return "items/generic";
        }
    }

    /**
     * Получает количество отрисованных предметов.
     */
    public int getItemsRendered() {
        return itemsRendered;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
