package io.thoughtworksarts.riot.visualization;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class VisualizationDTO {


    private Map<String, ArrayList<String>> emotionDataByActorId;

    public Map<String, ArrayList<String>> getEmotionDataByActorId() {
        return emotionDataByActorId;
    }

    public void setEmotionDataByActorId(Map<String, ArrayList<String>> emotionDataByActorId) {
        this.emotionDataByActorId = emotionDataByActorId;
    }

    public ArrayList<String> getOrderedActorID() {
        return orderedActorID;
    }

    public void setOrderedActorID(ArrayList<String> orderedActorID) {
        this.orderedActorID = orderedActorID;
    }

    public VisualizationDTO(Map<String, ArrayList<String>> emotionDataByActorId, ArrayList<String> orderedActorID) {
        this.emotionDataByActorId = emotionDataByActorId;
        this.orderedActorID = orderedActorID;
    }

    private ArrayList<String> orderedActorID;



}
