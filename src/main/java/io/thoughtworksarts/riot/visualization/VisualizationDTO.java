package io.thoughtworksarts.riot.visualization;

import java.util.ArrayList;
import java.util.Map;

public class VisualizationDTO {


    private Map<String, Map<String, ArrayList<String>>> emotionDataByActorId;

    public Map<String, Map<String, ArrayList<String>>> getEmotionDataByActorId() {
        return emotionDataByActorId;
    }

    public void setEmotionDataByActorId(Map<String, Map<String, ArrayList<String>>> emotionDataByActorId) {
        this.emotionDataByActorId = emotionDataByActorId;
    }

    public ArrayList<String> getOrderedActorId() {
        return orderedActorId;
    }

    public void setOrderedActorId(ArrayList<String> orderedActorId) {
        this.orderedActorId = orderedActorId;
    }

    public VisualizationDTO(Map<String, Map<String, ArrayList<String>>> emotionDataByActorId, ArrayList<String> orderedActorId) {
        this.emotionDataByActorId = emotionDataByActorId;
        this.orderedActorId = orderedActorId;
    }

    private ArrayList<String> orderedActorId;



}
