package io.thoughtworksarts.riot.facialrecognition;

import lombok.Getter;

public enum Emotion {
    ANGER(0), FEAR(1), CALM(2), SADNESS(3), SURPRISE(4), CONTEMPT(5), DISGUST(6);

    @Getter private int number;

    Emotion(int number) {
        this.number = number;
    }

}
