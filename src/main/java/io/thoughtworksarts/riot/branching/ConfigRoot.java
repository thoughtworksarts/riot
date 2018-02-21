package io.thoughtworksarts.riot.branching;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class ConfigRoot {
    @Getter @Setter private Media media;
    @Getter @Setter private Level[] levels;

    public String getAudio() {
        return media.getAudio();
    }
    public String getVideo() {
        return media.getVideo();
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
    @Getter @Setter private Map<String, Emotion> branch;
}

class Emotion {
    @Getter @Setter private String start;
    @Getter @Setter private String end;
    @Getter @Setter private int outcome;
}
