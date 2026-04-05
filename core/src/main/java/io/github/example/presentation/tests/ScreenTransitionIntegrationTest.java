package io.github.example.presentation.tests;

import io.github.example.presentation.screens.*;
import io.github.example.presentation.util.Logger;
import io.github.example.domain.service.GameService;
import io.github.example.domain.service.GameSession;

/**
 * Integration test for screen transitions and gameplay flow.
 * Tests the complete flow: Menu → Game → Pause → Resume → Game Over.
 */
public class ScreenTransitionIntegrationTest {
    private final GameService gameService;
    private final ScreenManager screenManager;
    private int testsPassed;
    private int testsFailed;
    private String lastError;

    public ScreenTransitionIntegrationTest(GameService gameService, ScreenManager screenManager) {
        this.gameService = gameService;
        this.screenManager = screenManager;
        this.testsPassed = 0;
        this.testsFailed = 0;
    }

    /**
     * Run all integration tests.
     */
    public boolean runAllTests() {
        Logger.info("Starting Screen Transition Integration Tests...");
        
        reset();
        
        // Test 1: Game initialization
        testGameInitialization();
        
        // Test 2: Screen transitions
        testScreenTransitions();
        
        // Test 3: Game state preservation
        testGameStatePreservation();
        
        // Test 4: Pause/Resume cycle
        testPauseResumeFlow();
        
        // Test 5: Game over flow
        testGameOverFlow();

        printResults();
        return testsFailed == 0;
    }

    /**
     * Test 1: Game service initializes correctly.
     */
    private void testGameInitialization() {
        try {
            gameService.startNewGame();
            GameSession session = gameService.getSession();
            
            assertTrue(session != null, "GameSession should be created");
            assertTrue(session.getPlayer() != null, "Player should be created");
            assertTrue(session.getCurrentLevel() != null, "Level should be created");
            
            testsPassed++;
            Logger.info("✓ Game Initialization test passed");
        } catch (Exception e) {
            testsFailed++;
            lastError = "Game Initialization failed: " + e.getMessage();
            Logger.error(lastError);
        }
    }

    /**
     * Test 2: Screen transitions work correctly.
     */
    private void testScreenTransitions() {
        try {
            // Test transition to game screen
            GameScreen gameScreen = new GameScreen(gameService, null, null, null);
            screenManager.setScreen(gameScreen);
            
            assertTrue(screenManager.getCurrentScreen() == gameScreen, "Current screen should be GameScreen");
            
            // Test transition with crossfade (create callback stubs)
            PauseScreen pauseScreen = new PauseScreen(new PauseScreen.PauseCallback() {
                @Override
                public void onResume() { }
                @Override
                public void onInventory() { }
                @Override
                public void onMainMenu() { }
            });
            screenManager.transitionToScreen(pauseScreen, 0.5f);
            
            // Test transition back
            screenManager.transitionToScreen(gameScreen, 0.5f);
            
            testsPassed++;
            Logger.info("✓ Screen Transitions test passed");
        } catch (Exception e) {
            testsFailed++;
            lastError = "Screen Transitions failed: " + e.getMessage();
            Logger.error(lastError);
        }
    }

    /**
     * Test 3: Game state is preserved during transitions.
     */
    private void testGameStatePreservation() {
        try {
            gameService.startNewGame();
            GameSession session = gameService.getSession();
            int playerHealthBefore = session.getPlayer().getStats().getCurrentHealth();
            
            // Create and save game state
            GameStateSnapshot snapshot = new GameStateSnapshot(session, "TestSnapshot");
            
            assertTrue(snapshot.getGameSession() != null, "Snapshot should contain game session");
            assertTrue(snapshot.getScreenName().equals("TestSnapshot"), "Snapshot should record screen name");
            
            // Verify player state is preserved
            GameSession restoredSession = snapshot.getGameSession();
            assertTrue(restoredSession.getPlayer().getStats().getCurrentHealth() == playerHealthBefore,
                "Player health should be preserved");
            
            testsPassed++;
            Logger.info("✓ Game State Preservation test passed");
        } catch (Exception e) {
            testsFailed++;
            lastError = "Game State Preservation failed: " + e.getMessage();
            Logger.error(lastError);
        }
    }

    /**
     * Test 4: Pause and resume cycle works.
     */
    private void testPauseResumeFlow() {
        try {
            gameService.startNewGame();
            
            // Create game screen
            GameScreen gameScreen = new GameScreen(gameService, null, null, null);
            screenManager.setScreen(gameScreen);
            
            assertTrue(screenManager.getCurrentScreen() == gameScreen, "Should be on GameScreen");
            
            // Simulate pause
            PauseScreen pauseScreen = new PauseScreen(new PauseScreen.PauseCallback() {
                @Override
                public void onResume() { }
                @Override
                public void onInventory() { }
                @Override
                public void onMainMenu() { }
            });
            screenManager.transitionToScreen(pauseScreen, 0.3f);
            
            // Simulate resume
            screenManager.transitionToScreen(gameScreen, 0.3f);
            
            assertTrue(screenManager.getCurrentScreen() == gameScreen, "Should be back on GameScreen");
            
            testsPassed++;
            Logger.info("✓ Pause/Resume Flow test passed");
        } catch (Exception e) {
            testsFailed++;
            lastError = "Pause/Resume Flow failed: " + e.getMessage();
            Logger.error(lastError);
        }
    }

    /**
     * Test 5: Game over flow works correctly.
     */
    private void testGameOverFlow() {
        try {
            gameService.startNewGame();
            
            // Create game screen
            GameScreen gameScreen = new GameScreen(gameService, null, null, null);
            screenManager.setScreen(gameScreen);
            
            // Simulate game over
            DeathScreen deathScreen = new DeathScreen(new DeathScreen.DeathCallback() {
                @Override
                public void onRestart() { }
                @Override
                public void onReturnToMenu() { }
            });
            screenManager.transitionToScreen(deathScreen, 0.5f);
            
            assertTrue(screenManager.getCurrentScreen() == deathScreen, "Should be on DeathScreen");
            
            // Simulate return to menu
            MenuScreen menuScreen = new MenuScreen(null);
            screenManager.transitionToScreen(menuScreen, 0.5f);
            
            assertTrue(screenManager.getCurrentScreen() == menuScreen, "Should be back on MenuScreen");
            
            testsPassed++;
            Logger.info("✓ Game Over Flow test passed");
        } catch (Exception e) {
            testsFailed++;
            lastError = "Game Over Flow failed: " + e.getMessage();
            Logger.error(lastError);
        }
    }

    /**
     * Assert that a condition is true.
     */
    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Reset test counters.
     */
    private void reset() {
        testsPassed = 0;
        testsFailed = 0;
        lastError = "";
    }

    /**
     * Print test results summary.
     */
    private void printResults() {
        int total = testsPassed + testsFailed;
        Logger.info("=".repeat(50));
        Logger.info("Screen Transition Integration Test Results");
        Logger.info("=".repeat(50));
        Logger.info(String.format("Passed: %d/%d", testsPassed, total));
        Logger.info(String.format("Failed: %d/%d", testsFailed, total));
        Logger.info(String.format("Success Rate: %.1f%%", (testsPassed * 100.0f / total)));
        if (testsFailed > 0) {
            Logger.error("Last Error: " + lastError);
        }
        Logger.info("=".repeat(50));
    }

    /**
     * Get number of tests passed.
     */
    public int getTestsPassed() {
        return testsPassed;
    }

    /**
     * Get number of tests failed.
     */
    public int getTestsFailed() {
        return testsFailed;
    }

    /**
     * Get last error message.
     */
    public String getLastError() {
        return lastError;
    }
}
