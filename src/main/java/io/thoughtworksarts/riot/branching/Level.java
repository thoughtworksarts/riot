package io.thoughtworksarts.riot.branching;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class Level {
    @Getter @Setter private int level;
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private Map<String, EmotionBranch> branch;
}
