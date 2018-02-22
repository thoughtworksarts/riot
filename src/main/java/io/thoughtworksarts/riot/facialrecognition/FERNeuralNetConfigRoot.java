package io.thoughtworksarts.riot.facialrecognition;

import lombok.Getter;
import lombok.Setter;

public class FERNeuralNetConfigRoot {
    @Getter @Setter private String weightsFile;
    @Getter @Setter private String modelFile;
}
