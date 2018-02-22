package io.thoughtworksarts.riot.facialrecognition;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class FERNeuralNetConfigRoot {
    @Getter @Setter private String weightsFile;
    @Getter @Setter private String modelFile;

    @Getter @Setter private Map<String, Integer> emotionMap;
}
