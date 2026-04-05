package io.github.example.datalayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.example.domain.service.GameRecord;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaderboardSave {
    private final List<GameRecord> leaderboard;

    @JsonCreator
    public LeaderboardSave(
            @JsonProperty("leaderboard") List<GameRecord> leaderboard){
        this.leaderboard = leaderboard;
    }

    public List<GameRecord> getLeaderboard() {
        return leaderboard;
    }
}
