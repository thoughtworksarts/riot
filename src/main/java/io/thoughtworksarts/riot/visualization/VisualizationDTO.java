package io.thoughtworksarts.riot.visualization;

import java.util.ArrayList;
import java.util.Map;

public class VisualizationDTO {

    private String actorId;
    private ArrayList dominantEmotions;
    private ArrayList scenesPlayed;

    public VisualizationDTO(String actorId, ArrayList dominantEmotions, ArrayList scenesPlayed) {
        this.actorId = actorId;
        this.dominantEmotions = dominantEmotions;
        this.scenesPlayed = scenesPlayed;
    }

    public String getActorId() {
        return actorId;
    }

    public ArrayList getDominantEmotions() {
        return dominantEmotions;
    }

    public ArrayList getScenesPlayed() {
        return scenesPlayed;
    }
}
