package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FacialEmotionRecognitionAPITest {

    @Test
    @Ignore
    void shouldGetEmotionValuesBetweenZeroAndOneForWebcamImage() {
        String configPath = "src/main/resources/neuralNetConfig.json";
        FacialEmotionRecognitionAPI facialEmotionRecognitionAPI = new FacialEmotionRecognitionAPI(configPath);
        facialEmotionRecognitionAPI.initialise();

        float calmValue = facialEmotionRecognitionAPI.getCalm();
        float angerValue = facialEmotionRecognitionAPI.getAnger();
        float fearValue = facialEmotionRecognitionAPI.getFear();

        assertTrue(calmValue >= 0 && calmValue <= 1);
        assertTrue(angerValue >= 0 && angerValue <= 1);
        assertTrue(fearValue >= 0 && angerValue <= 1);
    }
}
