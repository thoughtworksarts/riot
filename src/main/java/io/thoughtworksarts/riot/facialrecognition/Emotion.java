package io.thoughtworksarts.riot.facialrecognition;

import lombok.Getter;

public enum Emotion {
    ANGER(0), CALM(1), FEAR(2), SADNESS(3), HAPPINESS(4), SURPRISE(5), CONTEMPT(6);

    @Getter private int number;

    Emotion(int number) {
        this.number = number;
    }

}
