package io.thoughtworksarts.riot.branching.model;

import lombok.Getter;
import lombok.Setter;

public class EmotionBranch {
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private int outcome;
}
