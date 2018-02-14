package io.thoughtworksarts.riot.branching;

import lombok.Getter;
import lombok.Setter;

public class ConfigRoot {
    @Getter @Setter private Media media;
    @Getter @Setter private Level[] levels;

    public String getAudio() {
        return media.getAudio();
    }
}

class Media {
    @Getter @Setter private String video;
    @Getter @Setter private String audio;
}

class Level {
    @Getter @Setter private int level;
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private Branch branch;
}

class Branch {
    @Getter @Setter private Emotion anger;
    @Getter @Setter private Emotion fear;
    @Getter @Setter private Emotion calm;
}
class Emotion {
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private int outcome;
}
