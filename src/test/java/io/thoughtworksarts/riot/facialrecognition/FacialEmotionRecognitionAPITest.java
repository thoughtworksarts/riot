package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FacialEmotionRecognitionAPITest {

    @Test
    public void shouldBuildEmotionMapFromConfigFileWithThreeEmotions() {
        String configPath = "src/main/resources/neuralNetConfig.json";
        FacialEmotionRecognitionAPI facialEmotionRecognitionAPI = new FacialEmotionRecognitionAPI(configPath);
        Map<String, Integer> actualEmotionMap = facialEmotionRecognitionAPI.emotionMap;

        Map<String, Integer> expectedEmotionMap = new HashMap<>();
        expectedEmotionMap.put("anger", 0);
        expectedEmotionMap.put("calm", 1);
        expectedEmotionMap.put("fear", 2);

        assertTrue(expectedEmotionMap.equals(actualEmotionMap));
    }
}
