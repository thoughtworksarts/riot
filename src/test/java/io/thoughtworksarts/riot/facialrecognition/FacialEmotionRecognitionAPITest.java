package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacialEmotionRecognitionAPITest {

    MockFacialEmotionRecognitionAPI mockEmotionAPI;
    @Mock DeepLearningProcessor deepLearningProcessor;
    @Mock ImageProcessor imageProcessor;

    @Before
    public void setup() {
        initMocks(this);
        String configPath = "src/main/resources/neuralNetConfig.json";
        mockEmotionAPI = new MockFacialEmotionRecognitionAPI(configPath, deepLearningProcessor, imageProcessor);
    }

    @Test
    public void shouldBuildEmotionMapFromConfigFileWithThreeEmotions() {
        Map<String, Integer> actualEmotionMap = mockEmotionAPI.emotionMap;

        Map<String, Integer> expectedEmotionMap = new HashMap<>();
        expectedEmotionMap.put("anger", 0);
        expectedEmotionMap.put("calm", 1);
        expectedEmotionMap.put("fear", 2);

        assertTrue(expectedEmotionMap.equals(actualEmotionMap));
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        mockEmotionAPI.initialise();

        verify(imageProcessor).prepareImageForNet(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
        verify(deepLearningProcessor).getEmotionPrediction(Mockito.any());
    }
}
