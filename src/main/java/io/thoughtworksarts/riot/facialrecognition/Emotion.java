package io.thoughtworksarts.riot.facialrecognition;

import lombok.Getter;

public enum Emotion {
    ANGER(0), DISGUST(1), FEAR(2), HAPPINESS(3), SADNESS(4), SURPRISE(5), CALM(6), CONTEMPT(7);

    @Getter private int number;

    Emotion(int number) {
        this.number = number;
    }

}
