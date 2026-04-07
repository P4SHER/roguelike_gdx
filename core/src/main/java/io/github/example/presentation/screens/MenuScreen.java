package io.github.example.presentation.screens;

public class MenuScreen {
    public interface MenuCallback {
        void onNewGame();
        void onLoadGame();
        void onLeaderboard();
        void onExit();
    }
    public MenuScreen(MenuCallback callback) {}
}
