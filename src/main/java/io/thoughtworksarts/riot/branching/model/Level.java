package io.thoughtworksarts.riot.branching.model;

import io.thoughtworksarts.riot.branching.model.EmotionBranch;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Level {
    @Getter @Setter private int level;
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private Map<String, EmotionBranch> branch;
}
