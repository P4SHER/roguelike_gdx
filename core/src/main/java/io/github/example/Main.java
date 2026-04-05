package io.github.example;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import io.github.example.presentation.PresentationLayer;
import io.github.example.presentation.screens.MenuScreen;
import io.github.example.presentation.screens.LeaderboardScreen;
import io.github.example.presentation.util.Constants;
import io.github.example.presentation.util.Logger;
import io.github.example.domain.service.GameService;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private PresentationLayer presentationLayer;
    private GameService gameService;

    @Override
    public void create() {
        Logger.info("Application created");
        
        try {
            // Initialize domain layer
            gameService = new GameService();
            Logger.info("GameService initialized");
            
            // Initialize presentation layer
            presentationLayer = new PresentationLayer(
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT
            );
            Logger.info("PresentationLayer initialized");
            
            // Set initial screen (Menu)
            MenuScreen menuScreen = new MenuScreen(new MenuScreen.MenuCallback() {
                @Override
                public void onNewGame() {
                    handleNewGame();
                }

                @Override
                public void onLoadGame() {
                    handleLoadGame();
                }

                @Override
                public void onLeaderboard() {
                    handleLeaderboard();
                }

                @Override
                public void onExit() {
                    handleQuit();
                }
            });
            
            presentationLayer.setInitialScreen(menuScreen);
            Logger.info("Initial screen set to MenuScreen");
            
        } catch (Exception e) {
            Logger.error("Error during application creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleNewGame() {
        Logger.info("Starting new game...");
        try {
            gameService.startNewGame();
            // GameScreen setup would go here (handled by MenuScreen callback)
        } catch (Exception e) {
            Logger.error("Error starting new game: " + e.getMessage());
        }
    }

    private void handleLoadGame() {
        Logger.info("Loading saved game...");
        try {
            boolean loaded = gameService.loadSaveGame();
            if (!loaded) {
                Logger.info("No save found, starting new game");
                handleNewGame();
            }
        } catch (Exception e) {
            Logger.error("Error loading game: " + e.getMessage());
        }
    }

    private void handleLeaderboard() {
        Logger.info("Showing leaderboard...");
        try {
            LeaderboardScreen leaderboardScreen = new LeaderboardScreen(new LeaderboardScreen.LeaderboardCallback() {
                @Override
                public void onBack() {
                    // Return to menu
                    MenuScreen menuScreen = new MenuScreen(new MenuCallback());
                    presentationLayer.transitionToScreen(menuScreen, 0.5f);
                }

                @Override
                public java.util.List<LeaderboardScreen.LeaderboardEntry> loadLeaderboard() {
                    // Load leaderboard from GameService
                    try {
                        return new java.util.ArrayList<>(); // TODO: Load from actual leaderboard
                    } catch (Exception e) {
                        Logger.error("Error loading leaderboard: " + e.getMessage());
                        return new java.util.ArrayList<>();
                    }
                }
            });
            presentationLayer.transitionToScreen(leaderboardScreen, 0.5f);
        } catch (Exception e) {
            Logger.error("Error showing leaderboard: " + e.getMessage());
        }
    }

    private void handleQuit() {
        Logger.info("Quitting application");
        Gdx.app.exit();
    }

    // Default menu callback
    private class MenuCallback implements MenuScreen.MenuCallback {
        @Override
        public void onNewGame() {
            handleNewGame();
        }

        @Override
        public void onLoadGame() {
            handleLoadGame();
        }

        @Override
        public void onLeaderboard() {
            handleLeaderboard();
        }

        @Override
        public void onExit() {
            handleQuit();
        }
    }

    @Override
    public void render() {
        try {
            super.render();
            if (presentationLayer != null) {
                presentationLayer.render(0.016f); // Approximate 60 FPS delta
            }
        } catch (Exception e) {
            Logger.error("Render error: " + e.getMessage());
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        
        try {
            if (presentationLayer != null) {
                presentationLayer.resize(width, height);
            }
        } catch (Exception e) {
            Logger.error("Resize error: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        Logger.info("Application disposed");
        try {
            if (presentationLayer != null) {
                presentationLayer.dispose();
            }
        } catch (Exception e) {
            Logger.error("Dispose error: " + e.getMessage());
        }
    }
}



