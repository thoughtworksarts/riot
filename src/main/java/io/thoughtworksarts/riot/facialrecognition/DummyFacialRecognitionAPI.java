package io.thoughtworksarts.riot.facialrecognition;

public class DummyFacialRecognitionAPI {

    public DummyFacialRecognitionAPI(String config) {
    }

    public void initialise() {
    }

    public Emotion getDominateEmotion() {
        return Emotion.CALM;
    }

    public float getCalm() {
        return 1;
    }

    public float getFear() {
        return 0;
    }

    public float getAnger() {
        return 0;
    }
}
