package io.thoughtworksarts.riot.eyetracking;

import io.thoughtworksarts.riot.branching.model.Level;

public class LevelDTO {
    private int levelId;
    private String startTime;
    private String endTime;

    public int getLevelId() {
        return levelId;
    }

    private void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getStartTime() {
        return startTime;
    }

    private void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    private void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public LevelDTO(Level level) {
        setLevelId(level.getLevel());
        setStartTime(level.getStart());
        setEndTime(level.getEnd());
    }
}
