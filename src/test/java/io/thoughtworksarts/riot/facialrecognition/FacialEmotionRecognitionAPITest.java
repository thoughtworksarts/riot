package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacialEmotionRecognitionAPITest {

    MockFacialEmotionRecognitionAPI threeEmotionMockEmotionAPI;
    MockFacialEmotionRecognitionAPI fiveEmotionMockEmotionAPI;
    @Mock DeepLearningProcessor deepLearningProcessor;
    @Mock ImageProcessor imageProcessor;

    @Before
    public void setup() {
        initMocks(this);
        String configPath = "src/test/resources/neuralNetConfig.json";
        threeEmotionMockEmotionAPI = new MockFacialEmotionRecognitionAPI(configPath, deepLearningProcessor, imageProcessor);
        String fiveEmotionConfigPath = "src/test/resources/neuralNetConfig5Emotions.json";
        fiveEmotionMockEmotionAPI = new MockFacialEmotionRecognitionAPI(fiveEmotionConfigPath, deepLearningProcessor, imageProcessor);
    }

    @Test
    public void shouldBuildEmotionMapFromConfigFileWithThreeEmotions() {
        Map<String, Integer> actualEmotionMap = threeEmotionMockEmotionAPI.emotionMap;

        Map<String, Integer> expectedEmotionMap = new HashMap<>();
        expectedEmotionMap.put("anger", 0);
        expectedEmotionMap.put("calm", 1);
        expectedEmotionMap.put("fear", 2);

        assertTrue(expectedEmotionMap.equals(actualEmotionMap));
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        threeEmotionMockEmotionAPI.initialise();

        verify(imageProcessor).prepareImageForNet(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
        verify(deepLearningProcessor).getEmotionPrediction(Mockito.any());
    }

    @Test
    public void shouldGetEmotionValuesSetAtInitialisation() {
        float[] emotionProbabilities = new float[]{0f, 1f, 0.5f};
        doReturn(emotionProbabilities).when(deepLearningProcessor).getEmotionPrediction(Mockito.any());

        threeEmotionMockEmotionAPI.initialise();

        assertEquals(1, threeEmotionMockEmotionAPI.getCalm());
        assertEquals(0, threeEmotionMockEmotionAPI.getAnger());
        assertEquals(0.5, threeEmotionMockEmotionAPI.getFear());
    }

    @Test(expected = UnsupportedEmotionException.class)
    public void shouldRaiseErrorIfValueRequestedForEmotionNotInConfig() throws UnsupportedEmotionException {
        threeEmotionMockEmotionAPI.initialise();
        threeEmotionMockEmotionAPI.getDisgust();
    }

    @Test
    public void shouldBuildEmotionMapFromConfigFileWithFiveEmotions() {
        Map<String, Integer> actualEmotionMap = fiveEmotionMockEmotionAPI.emotionMap;

        Map<String, Integer> expectedEmotionMap = new HashMap<>();
        expectedEmotionMap.put("anger", 0);
        expectedEmotionMap.put("calm", 1);
        expectedEmotionMap.put("fear", 2);
        expectedEmotionMap.put("disgust", 3);
        expectedEmotionMap.put("contempt", 4);

        assertTrue(expectedEmotionMap.equals(actualEmotionMap));
    }
}
