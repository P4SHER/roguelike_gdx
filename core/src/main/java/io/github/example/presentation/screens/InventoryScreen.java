package io.github.example.presentation.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.domain.entities.Player;
import io.github.example.domain.entities.Backpack;
import io.github.example.domain.entities.Item;
import io.github.example.domain.entities.ItemType;
import io.github.example.domain.entities.EntityConfig;
import io.github.example.domain.service.GameService;
import io.github.example.domain.service.GameSession;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import io.github.example.presentation.input.InputHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory screen for viewing and managing player items.
 * Layout: Left panel for item list, Right panel for item details.
 * Overlays on GameScreen.
 * Fully integrated with GameService for item operations.
 */
public class InventoryScreen implements Screen {
    private final Player player;
    private final GameService gameService;
    private final InventoryCallback callback;
    private final InputHandler inputHandler;
    private Backpack backpack;
    private Item selectedItem;
    private List<Item> displayItems;
    private int selectedIndex;
    private String lastMessage;
    private float messageDisplayTime;
    private static final float MESSAGE_DISPLAY_DURATION = 2.0f;
    private static Texture filledTexture;
    private static final Object textureLock = new Object();

    public interface InventoryCallback {
        void onClose();
    }

    public InventoryScreen(Player player, GameService gameService, InventoryCallback callback, InputHandler inputHandler) {
        this.player = player;
        this.gameService = gameService;
        this.callback = callback;
        this.inputHandler = inputHandler;
        this.selectedIndex = 0;
        this.lastMessage = "";
        this.messageDisplayTime = 0;

        if (player != null && player.getBackpack() != null) {
            this.backpack = player.getBackpack();
            this.displayItems = new ArrayList<>(backpack.getAllItems());
        } else {
            this.displayItems = new ArrayList<>();
        }
    }

    @Override
    public void show() {
        Logger.info("InventoryScreen opened");
        updateFromBackpack(player != null ? player.getBackpack() : null);
        if (!displayItems.isEmpty()) {
            selectedItem = displayItems.get(0);
            selectedIndex = 0;
        } else {
            selectedItem = null;
            selectedIndex = -1;
        }
        clearMessage();
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Update message display time
        if (messageDisplayTime > 0) {
            messageDisplayTime -= delta;
        }

        // Draw semi-transparent overlay
        batch.setColor(0, 0, 0, 0.5f);
        batch.draw(getFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Left panel: Item list with slots info
        renderItemList(batch);

        // Right panel: Item details
        renderItemDetails(batch);

        // Buttons: Use/Equip/Drop, Close
        renderButtons(batch);

        // Display messages
        renderMessages(batch);

        Logger.debug("Inventory rendered: " + displayItems.size() + " items, selected: " +
            (selectedItem != null ? selectedItem.getName() : "none"));
    }

    /**
     * Updates the display items from the backpack.
     * Called whenever inventory contents change.
     */
    public void updateFromBackpack(Backpack backpack) {
        if (backpack != null) {
            this.backpack = backpack;
            this.displayItems = new ArrayList<>(backpack.getAllItems());
            // Validate selected index
            if (selectedIndex >= displayItems.size()) {
                selectedIndex = Math.max(0, displayItems.size() - 1);
            }
            if (selectedIndex >= 0 && selectedIndex < displayItems.size()) {
                selectedItem = displayItems.get(selectedIndex);
            } else {
                selectedItem = null;
            }
        } else {
            this.displayItems = new ArrayList<>();
            this.backpack = null;
            this.selectedItem = null;
            this.selectedIndex = -1;
        }
    }

    /**
     * Renders the left panel with item list and inventory slots info.
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

        // Draw inventory slots info at the top
        float headerY = panelY + panelHeight - Constants.UI_PADDING;
        if (backpack != null) {
            int current = backpack.getCurrentItems();
            int maxSlots = EntityConfig.MAX_ITEMS_IN_BACKPACK;
            Logger.debug("Inventory: " + current + "/" + maxSlots);
        }

        // Draw item list
        float itemY = headerY - 40;
        float itemHeight = 30;
        float itemSpacing = 5;

        for (int i = 0; i < displayItems.size(); i++) {
            Item item = displayItems.get(i);
            if (itemY < panelY) {
                break; // Stop if we've scrolled past the panel
            }

            // Highlight selected item
            if (i == selectedIndex) {
                batch.setColor(0.3f, 0.3f, 0.5f, 0.9f);
            } else {
                batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
            }
            batch.draw(getFilledTexture(), panelX + Constants.UI_MARGIN, itemY - itemHeight,
                panelWidth - Constants.UI_MARGIN * 2, itemHeight);

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

        if (selectedItem.getCountFoodInBackpack() > 0) {
            Logger.debug("Quantity: " + selectedItem.getCountFoodInBackpack());
            textY -= lineHeight;
        }

        if (selectedItem.getStrengthBonus() > 0) {
            Logger.debug("Strength Bonus: " + selectedItem.getStrengthBonus());
            textY -= lineHeight;
        }

        if (selectedItem.getHealthRestoreFood() > 0) {
            Logger.debug("Health Restore: " + selectedItem.getHealthRestoreFood());
            textY -= lineHeight;
        }

        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders action buttons and keyboard hints.
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
     * Renders status messages with fade-out.
     */
    private void renderMessages(SpriteBatch batch) {
        if (messageDisplayTime > 0 && !lastMessage.isEmpty()) {
            float alpha = messageDisplayTime / MESSAGE_DISPLAY_DURATION;
            batch.setColor(1, 1, 1, alpha);
            Logger.debug(lastMessage);
            batch.setColor(1, 1, 1, 1);
        }
    }

    /**
     * Displays a message to the user.
     */
    private void showMessage(String message) {
        lastMessage = message;
        messageDisplayTime = MESSAGE_DISPLAY_DURATION;
        Logger.info(message);
    }

    /**
     * Clears the current message.
     */
    private void clearMessage() {
        lastMessage = "";
        messageDisplayTime = 0;
    }

    /**
     * Navigates up in the inventory list.
     */
    public void navigateUp() {
        if (selectedIndex > 0) {
            selectedIndex--;
            selectedItem = displayItems.get(selectedIndex);
            Logger.info("Selected: " + selectedItem.getName());
        }
    }

    /**
     * Navigates down in the inventory list.
     */
    public void navigateDown() {
        if (selectedIndex < displayItems.size() - 1) {
            selectedIndex++;
            selectedItem = displayItems.get(selectedIndex);
            Logger.info("Selected: " + selectedItem.getName());
        }
    }

    /**
     * Uses the selected item through GameService.
     */
    public void useSelectedItem() {
        if (selectedItem == null || gameService == null || selectedIndex < 0) {
            showMessage("Cannot use item");
            return;
        }

        try {
            boolean success = gameService.useItemFromBackpack(selectedIndex, false);
            if (success) {
                String itemName = selectedItem.getName();
                showMessage("Used: " + itemName);
                updateFromBackpack(player.getBackpack());
            } else {
                showMessage("Failed to use item");
            }
        } catch (Exception e) {
            Logger.error("Error using item: " + e.getMessage());
            showMessage("Error using item");
        }
    }

    /**
     * Drops the selected item.
     */
    public void dropSelectedItem() {
        if (selectedItem == null || backpack == null || selectedIndex < 0) {
            showMessage("Cannot drop item");
            return;
        }

        try {
            Item itemToDrop = selectedItem;
            backpack.removeItem(itemToDrop);
            updateFromBackpack(backpack);
            showMessage("Dropped: " + itemToDrop.getName());
        } catch (Exception e) {
            Logger.error("Error dropping item: " + e.getMessage());
            showMessage("Error dropping item");
        }
    }

    /**
     * Equips/unequips the selected item (for weapons).
     */
    public void toggleEquip() {
        if (selectedItem == null) {
            showMessage("Cannot equip: no item selected");
            return;
        }

        if (selectedItem.getType() != ItemType.WEAPON) {
            showMessage("Can only equip weapons");
            return;
        }

        try {
            boolean success = gameService.useItemFromBackpack(selectedIndex, true);
            if (success) {
                showMessage("Equipped: " + selectedItem.getName());
                updateFromBackpack(player.getBackpack());
            } else {
                showMessage("Failed to equip item");
            }
        } catch (Exception e) {
            Logger.error("Error equipping item: " + e.getMessage());
            showMessage("Error equipping item");
        }
    }

    /**
     * Handles input navigation for arrow keys and action keys.
     */
    public void handleInput() {
        if (inputHandler == null) {
            return;
        }

        // Arrow key navigation
        if (inputHandler.isKeyPressed(Input.Keys.UP)) {
            navigateUp();
            inputHandler.clearInput();
        }
        if (inputHandler.isKeyPressed(Input.Keys.DOWN)) {
            navigateDown();
            inputHandler.clearInput();
        }

        // Action keys
        if (inputHandler.isKeyPressed(Input.Keys.ENTER) || inputHandler.isKeyPressed(Input.Keys.SPACE)) {
            useSelectedItem();
            inputHandler.clearInput();
        }
        if (inputHandler.isKeyPressed(Input.Keys.D) || inputHandler.isKeyPressed(Input.Keys.DEL)) {
            dropSelectedItem();
            inputHandler.clearInput();
        }
        if (inputHandler.isKeyPressed(Input.Keys.E)) {
            toggleEquip();
            inputHandler.clearInput();
        }
    }

    /**
     * Handles menu input for inventory navigation and actions.
     * Called from LibGdxGameApplicationListener when InventoryScreen is active.
     *
     * @param input the menu input from InputHandler
     */
    public void handleMenuInput(InputHandler.MenuInput input) {
        if (input == null) return;

        try {
            switch (input) {
                case UP:
                    navigateUp();
                    break;
                case DOWN:
                    navigateDown();
                    break;
                case SELECT:
                    // Use selected item on SELECT
                    useSelectedItem();
                    break;
                case CANCEL:
                    // Close inventory on CANCEL (ESC)
                    if (callback != null) {
                        callback.onClose();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Logger.error("Error handling inventory input: " + e.getMessage());
        }
    }

    /**
     * Adds an item from the ground to the backpack.
     */
    public void addItemFromGround(Item item) {
        if (backpack != null && item != null) {
            if (backpack.addItem(item)) {
                updateFromBackpack(backpack);
                showMessage("Added: " + item.getName());
            } else {
                Logger.warn("Inventory full!");
            }
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
