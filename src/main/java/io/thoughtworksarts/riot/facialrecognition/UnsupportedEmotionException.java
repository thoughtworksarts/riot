package io.thoughtworksarts.riot.facialrecognition;

public class UnsupportedEmotionException extends Exception{

    public UnsupportedEmotionException(String emotion) {
        super(String.format("The emotion %s is not supported by this instance of FacialEmotionRecognitionAPI", emotion));
    }

}
