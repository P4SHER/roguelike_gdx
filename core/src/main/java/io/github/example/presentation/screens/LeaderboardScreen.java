package io.github.example.presentation.screens;

import java.util.List;

public class LeaderboardScreen {
    public interface LeaderboardCallback {
        void onBack();
        List<LeaderboardEntry> loadLeaderboard();
    }
    public static class LeaderboardEntry {}
    public LeaderboardScreen(LeaderboardCallback callback) {}
}
