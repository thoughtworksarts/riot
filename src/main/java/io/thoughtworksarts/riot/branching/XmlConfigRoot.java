package io.thoughtworksarts.riot.branching;

import lombok.Getter;
import lombok.Setter;

public class XmlConfigRoot {
    @Getter @Setter Media media;
    @Getter @Setter Level[] levels;
}

class Media {
    @Getter @Setter String video;
    @Getter @Setter String audio;
}

class Level {
    @Getter @Setter int level;
    @Getter @Setter String start;
    @Getter @Setter String end;
    @Getter @Setter Branch branch;
}

class Branch {
    @Getter @Setter Emotion anger;
    @Getter @Setter Emotion fear;
    @Getter @Setter Emotion calm;
}
class Emotion {
    @Getter @Setter String start;
    @Getter @Setter String end;
    @Getter @Setter int outcome;
}
