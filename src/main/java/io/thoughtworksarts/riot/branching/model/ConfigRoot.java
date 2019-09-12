package io.thoughtworksarts.riot.branching.model;

import lombok.Getter;
import lombok.Setter;

public class ConfigRoot {
    @Getter @Setter private Media media;
    @Getter @Setter private String[] actors;
    @Getter @Setter private Level[] levels;
    @Getter @Setter private Intro[] intros;
    @Getter @Setter private Credits[] credits;
    @Getter @Setter private String mode;
    @Getter @Setter private String audioOffset;
}

