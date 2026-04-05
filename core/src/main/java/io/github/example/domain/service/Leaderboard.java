package io.github.example.domain.service;

import io.github.example.domain.unittest.Logger;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private final List<GameRecord> leaderboard = new ArrayList<>();
    private int countWriteRecord = 1;

    public Leaderboard() {
        leaderboard.add(new GameRecord());
    }
    public void addRecord(GameRecord record) {
        leaderboard.add(record);
        countWriteRecord++;
        Logger.info("New game was writing in leaderboards");
    }

    public List<GameRecord> getAllRecords(){
        return leaderboard;
    }


    public void updateLastRecord(GameRecord newRecord) {
        if (countWriteRecord > 0) leaderboard.removeLast();
        leaderboard.add(newRecord);
        Logger.info("Leader table was update");
    }

    // Getter
    public int getCountWriteRecord() {
        return countWriteRecord;
    }
    public GameRecord getGameRecordById(int id) {

        if (id < countWriteRecord && !leaderboard.isEmpty()) {
            return leaderboard.get(id);
        }

        return null;
    }
}
