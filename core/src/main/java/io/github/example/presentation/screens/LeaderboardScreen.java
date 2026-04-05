package io.github.example.presentation.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Leaderboard screen displaying high scores and player achievements.
 * Shows rank, name, score, floor reached, and date.
 * Replaces current screen (not overlay).
 */
public class LeaderboardScreen implements Screen {
    private final LeaderboardCallback callback;
    private List<LeaderboardEntry> entries;
    private SortBy sortBy = SortBy.SCORE;
    private static final int ENTRIES_PER_PAGE = 20;
    private int currentPage = 0;
    private static Texture filledTexture;
    private static final Object textureLock = new Object();

    public enum SortBy {
        SCORE,
        FLOOR
    }

    public static class LeaderboardEntry {
        public final int rank;
        public final String playerName;
        public final int score;
        public final int floor;
        public final String date;

        public LeaderboardEntry(int rank, String playerName, int score, int floor, String date) {
            this.rank = rank;
            this.playerName = playerName;
            this.score = score;
            this.floor = floor;
            this.date = date;
        }
    }

    public interface LeaderboardCallback {
        void onBack();
        List<LeaderboardEntry> loadLeaderboard();
    }

    public LeaderboardScreen(LeaderboardCallback callback) {
        this.callback = callback;
        this.entries = new ArrayList<>();
    }

    @Override
    public void show() {
        Logger.info("LeaderboardScreen показан");
        if (callback != null) {
            entries = callback.loadLeaderboard();
            if (entries == null) {
                entries = new ArrayList<>();
            }
        }
        currentPage = 0;
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        // Draw background
        batch.setColor(0.1f, 0.1f, 0.15f, 1f);
        batch.draw(getFilledTexture(), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        batch.setColor(1, 1, 1, 1);

        // Title
        renderTitle(batch);

        // Table header
        renderTableHeader(batch);

        // Leaderboard entries
        renderEntries(batch);

        // Pagination and buttons
        renderControls(batch);
    }

    /**
     * Renders the "LEADERBOARD" title.
     */
    private void renderTitle(SpriteBatch batch) {
        float titleY = Constants.SCREEN_HEIGHT - Constants.UI_PADDING * 2;
        batch.setColor(1f, 0.84f, 0f, 1f);
        Logger.info("LEADERBOARD");
        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders the table header (Rank | Name | Score | Floor | Date).
     */
    private void renderTableHeader(SpriteBatch batch) {
        float headerY = Constants.SCREEN_HEIGHT - Constants.UI_PADDING * 4;
        float rowHeight = 30;
        float padding = Constants.UI_PADDING;

        // Header background
        batch.setColor(0.2f, 0.2f, 0.25f, 1f);
        batch.draw(getFilledTexture(), padding, headerY - rowHeight, 
            Constants.SCREEN_WIDTH - padding * 2, rowHeight);

        batch.setColor(0.9f, 0.9f, 0.9f, 1f);
        // Header text would go here (using BitmapFont in real implementation)
        Logger.debug("Table Header: Rank | Name | Score | Floor | Date");
    }

    /**
     * Renders leaderboard entries.
     */
    private void renderEntries(SpriteBatch batch) {
        float startY = Constants.SCREEN_HEIGHT - Constants.UI_PADDING * 6;
        float rowHeight = 25;
        float padding = Constants.UI_PADDING;
        int startIndex = currentPage * ENTRIES_PER_PAGE;
        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, entries.size());

        for (int i = startIndex; i < endIndex; i++) {
            LeaderboardEntry entry = entries.get(i);
            float rowY = startY - (i - startIndex) * rowHeight;

            // Alternate row colors for readability
            if ((i - startIndex) % 2 == 0) {
                batch.setColor(0.15f, 0.15f, 0.18f, 1f);
            } else {
                batch.setColor(0.12f, 0.12f, 0.15f, 1f);
            }
            batch.draw(getFilledTexture(), padding, rowY - rowHeight, 
                Constants.SCREEN_WIDTH - padding * 2, rowHeight);

            // Entry text
            batch.setColor(0.9f, 0.9f, 0.9f, 1f);
            Logger.debug(String.format("%-5d | %-20s | %7d | %3d | %s",
                entry.rank, entry.playerName, entry.score, entry.floor, entry.date));
        }
        batch.setColor(1, 1, 1, 1);
    }

    /**
     * Renders pagination info and control buttons.
     */
    private void renderControls(SpriteBatch batch) {
        float controlY = Constants.UI_PADDING * 3;
        float buttonWidth = 120;
        float buttonHeight = 40;
        float buttonSpacing = 20;

        // Pagination info
        int totalPages = (int) Math.ceil((float) entries.size() / ENTRIES_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }
        Logger.info("Page " + (currentPage + 1) + " of " + totalPages);

        // Sort buttons
        float sortX = Constants.UI_PADDING;
        batch.setColor(0.2f, 0.3f, 0.5f, 0.8f);
        batch.draw(getFilledTexture(), sortX, controlY, buttonWidth, buttonHeight);
        batch.setColor(0.2f, 0.5f, 0.2f, 0.8f);
        batch.draw(getFilledTexture(), sortX + buttonWidth + buttonSpacing, controlY, 
            buttonWidth, buttonHeight);

        // Back button
        float backX = Constants.SCREEN_WIDTH - Constants.UI_PADDING - buttonWidth;
        batch.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        batch.draw(getFilledTexture(), backX, controlY, buttonWidth, buttonHeight);

        batch.setColor(1, 1, 1, 1);
        Logger.debug("Controls: Sort by Score | Sort by Floor | Back");
    }

    /**
     * Sorts entries by score (descending).
     */
    public void sortByScore() {
        sortBy = SortBy.SCORE;
        entries.sort((a, b) -> Integer.compare(b.score, a.score));
        currentPage = 0;
        Logger.info("Sorted by Score");
    }

    /**
     * Sorts entries by floor reached (descending).
     */
    public void sortByFloor() {
        sortBy = SortBy.FLOOR;
        entries.sort((a, b) -> Integer.compare(b.floor, a.floor));
        currentPage = 0;
        Logger.info("Sorted by Floor");
    }

    /**
     * Goes to the next page of results.
     */
    public void nextPage() {
        int maxPage = (int) Math.ceil((float) entries.size() / ENTRIES_PER_PAGE) - 1;
        if (currentPage < maxPage) {
            currentPage++;
            Logger.debug("Next page: " + (currentPage + 1));
        }
    }

    /**
     * Goes to the previous page of results.
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            Logger.debug("Previous page: " + (currentPage + 1));
        }
    }

    /**
     * Closes the leaderboard screen.
     */
    public void close() {
        if (callback != null) {
            callback.onBack();
        }
        Logger.info("Closing leaderboard");
    }

    @Override
    public void resize(int width, int height) {
        // Leaderboard adapts to window size
    }

    @Override
    public void hide() {
        Logger.debug("LeaderboardScreen hidden");
    }

    @Override
    public void dispose() {
        entries.clear();
        disposeFilledTexture();
        Logger.debug("LeaderboardScreen disposed");
    }

    @Override
    public String getName() {
        return "LeaderboardScreen";
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
                    Logger.debug("LeaderboardScreen filledTexture created");
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
                Logger.debug("LeaderboardScreen filledTexture disposed");
            }
        }
    }
}
