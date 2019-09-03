package io.thoughtworksarts.riot.eyetracking;

import io.thoughtworksarts.riot.branching.model.Level;

public class LevelDTO {
    private int levelId;
    private String startTime;
    private String endTime;
    private String dominantEmotion;

    public int getLevelId() {
        return levelId;
    }

    public String getDominantEmotion() { return dominantEmotion; }

    private void setDominantEmotion(String dominantEmotion) { this.dominantEmotion = dominantEmotion; }

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

    public LevelDTO(Level level, String emotion) {
        setLevelId(level.getLevel());
        setDominantEmotion(emotion);
        setStartTime(level.getStart());
        setEndTime(level.getEnd());

    }
}
