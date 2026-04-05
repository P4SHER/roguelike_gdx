package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Backpack;
import io.github.example.domain.entities.Item;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import java.util.List;

/**
 * Inventory screen for viewing and managing player items.
 * Layout: Left panel for item list, Right panel for item details.
 * Overlays on GameScreen.
 */
public class InventoryScreen implements Screen {
    private final Player player;
    private final InventoryCallback callback;
    private Item selectedItem;
    private List<Item> itemList;
    private static Texture filledTexture;
    private static final Object textureLock = new Object();

    public interface InventoryCallback {
        void onClose();
        void onItemUse(Item item);
        void onItemDrop(Item item);
    }

    public InventoryScreen(Player player, InventoryCallback callback) {
        this.player = player;
        this.callback = callback;
        this.itemList = player != null && player.getBackpack() != null ? 
            player.getBackpack().getAllItems() : 
            new java.util.ArrayList<>();
    }

    @Override
    public void show() {
        Logger.info("InventoryScreen показан");
        if (player != null && player.getBackpack() != null) {
            itemList = player.getBackpack().getAllItems();
            if (!itemList.isEmpty()) {
                selectedItem = itemList.get(0);
            }
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Draw semi-transparent overlay
        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(getFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Left panel: Item list (vertical scroll)
        renderItemList(batch);

        // Right panel: Item details
        renderItemDetails(batch);

        // Buttons: Use/Equip/Drop, Close
        renderButtons(batch);

        Logger.debug("Inventory rendered with " + itemList.size() + " items");
    }

    /**
     * Renders the left panel with item list.
     */
    private void renderItemList(SpriteBatch batch) {
        float panelX = Constants.UI_PADDING;
        float panelY = Constants.UI_PADDING * 2;
        float panelWidth = Constants.SCREEN_WIDTH / 3f;
        float panelHeight = Constants.SCREEN_HEIGHT - Constants.UI_PADDING * 4;

        // Draw panel background
        batch.setColor(0.15f, 0.15f, 0.15f, 0.8f);
        batch.draw(getFilledTexture(), panelX, panelY, panelWidth, panelHeight);
        batch.setColor(1, 1, 1, 1);

        // Draw item list
        float itemY = panelY + panelHeight - Constants.UI_PADDING;
        float itemHeight = 30;
        float itemSpacing = 5;

        for (Item item : itemList) {
            if (itemY < panelY) {
                break; // Stop if we've scrolled past the panel
            }

            // Highlight selected item
            if (item == selectedItem) {
                batch.setColor(0.3f, 0.3f, 0.5f, 0.8f);
            } else {
                batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
            }
            batch.draw(getFilledTexture(), panelX + Constants.UI_MARGIN, itemY - itemHeight, 
                panelWidth - Constants.UI_MARGIN * 2, itemHeight);

            // Reset color for text
            batch.setColor(0.9f, 0.9f, 0.9f, 1f);
            Logger.debug("Item: " + item.getName() + " x" + item.getCountFoodInBackpack());

            itemY -= (itemHeight + itemSpacing);
        }
        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders the right panel with details of the selected item.
     */
    private void renderItemDetails(SpriteBatch batch) {
        float panelX = Constants.SCREEN_WIDTH / 3f + Constants.UI_PADDING * 2;
        float panelY = Constants.UI_PADDING * 2;
        float panelWidth = Constants.SCREEN_WIDTH * 2 / 3f - Constants.UI_PADDING * 3;
        float panelHeight = Constants.SCREEN_HEIGHT - Constants.UI_PADDING * 4;

        // Draw panel background
        batch.setColor(0.15f, 0.15f, 0.15f, 0.8f);
        batch.draw(getFilledTexture(), panelX, panelY, panelWidth, panelHeight);
        batch.setColor(1, 1, 1, 1);

        if (selectedItem == null) {
            batch.setColor(0.5f, 0.5f, 0.5f, 1f);
            Logger.info("No item selected");
            return;
        }

        // Display item details
        float textY = panelY + panelHeight - Constants.UI_PADDING;
        float lineHeight = 25;

        batch.setColor(0.9f, 0.9f, 0.9f, 1f);
        Logger.debug("Item Name: " + selectedItem.getName());
        textY -= lineHeight;

        Logger.debug("Type: " + selectedItem.getType());
        textY -= lineHeight;

        Logger.debug("Count: " + selectedItem.getCountFoodInBackpack());
        textY -= lineHeight;

        Logger.debug("Strength Bonus: " + selectedItem.getStrengthBonus());

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders action buttons.
     */
    private void renderButtons(SpriteBatch batch) {
        float buttonWidth = 120;
        float buttonHeight = 40;
        float startY = Constants.UI_PADDING;
        float spacing = 10;
        float buttonsX = Constants.SCREEN_WIDTH / 2f - (buttonWidth + spacing / 2f);

        // Use/Equip button
        batch.setColor(0.2f, 0.5f, 0.2f, 0.8f);
        batch.draw(getFilledTexture(), buttonsX, startY, buttonWidth, buttonHeight);

        // Drop button
        batch.setColor(0.5f, 0.2f, 0.2f, 0.8f);
        batch.draw(getFilledTexture(), buttonsX + buttonWidth + spacing, startY, buttonWidth, buttonHeight);

        // Close button
        batch.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        batch.draw(getFilledTexture(), buttonsX + (buttonWidth + spacing) * 2, startY, buttonWidth, buttonHeight);

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Handles item selection (simulated).
     */
    public void selectItem(Item item) {
        if (itemList.contains(item)) {
            selectedItem = item;
            Logger.info("Selected: " + item.getName());
        }
    }

    /**
     * Uses or equips the selected item.
     */
    public void useSelectedItem() {
        if (selectedItem != null && callback != null) {
            callback.onItemUse(selectedItem);
            Logger.info("Used: " + selectedItem.getName());
        }
    }

    /**
     * Drops the selected item.
     */
    public void dropSelectedItem() {
        if (selectedItem != null && callback != null) {
            callback.onItemDrop(selectedItem);
            itemList.remove(selectedItem);
            if (!itemList.isEmpty()) {
                selectedItem = itemList.get(0);
            } else {
                selectedItem = null;
            }
            Logger.info("Dropped item");
        }
    }

    @Override
    public void resize(int width, int height) {
        // Inventory adapts to window size
    }

    @Override
    public void hide() {
        Logger.debug("InventoryScreen hidden");
    }

    @Override
    public void dispose() {
        Logger.debug("InventoryScreen disposed");
    }

    @Override
    public String getName() {
        return "InventoryScreen";
    }

    /**
     * Gets or creates a static white texture for drawing rectangles.
     */
    private static Texture getFilledTexture() {
        if (filledTexture == null) {
            synchronized (textureLock) {
                if (filledTexture == null) {
                    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                    pixmap.setColor(1, 1, 1, 1);
                    pixmap.fill();
                    filledTexture = new Texture(pixmap);
                    pixmap.dispose();
                    Logger.debug("InventoryScreen filledTexture created");
                }
            }
        }
        return filledTexture;
    }

    /**
     * Disposes the static texture. Called from PresentationLayer.dispose().
     */
    public static void disposeFilledTexture() {
        synchronized (textureLock) {
            if (filledTexture != null) {
                filledTexture.dispose();
                filledTexture = null;
                Logger.debug("InventoryScreen filledTexture disposed");
            }
        }
    }
}
