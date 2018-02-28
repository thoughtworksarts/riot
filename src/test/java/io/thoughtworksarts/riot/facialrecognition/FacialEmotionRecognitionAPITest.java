package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacialEmotionRecognitionAPITest {

    MockFacialEmotionRecognitionAPI threeEmotionMockEmotionAPI;
    MockFacialEmotionRecognitionAPI fiveEmotionMockEmotionAPI;
    @Mock DeepLearningProcessor deepLearningProcessor;
    @Mock ImageProcessor imageProcessor;

    @Before
    public void setup() {
        initMocks(this);
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3});
        String configPath = "src/test/resources/neuralNetConfig.json";
        threeEmotionMockEmotionAPI = new MockFacialEmotionRecognitionAPI(configPath, deepLearningProcessor, imageProcessor);
        String fiveEmotionConfigPath = "src/test/resources/neuralNetConfig5Emotions.json";
        fiveEmotionMockEmotionAPI = new MockFacialEmotionRecognitionAPI(fiveEmotionConfigPath, deepLearningProcessor, imageProcessor);
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        threeEmotionMockEmotionAPI.initialise();

        verify(imageProcessor).prepareImageForNet(any(), Mockito.anyInt(), Mockito.anyInt(), any());
        verify(deepLearningProcessor).getEmotionPrediction(any());
    }

    @Test
    public void shouldGetEmotionValuesSetAtInitialisation() {
        threeEmotionMockEmotionAPI.initialise();

        assertEquals(1, threeEmotionMockEmotionAPI.getAnger());
        assertEquals(2, threeEmotionMockEmotionAPI.getCalm());
        assertEquals(3, threeEmotionMockEmotionAPI.getFear());
        assertEquals(0, threeEmotionMockEmotionAPI.getSadness());
        assertEquals(0, threeEmotionMockEmotionAPI.getSurprise());
        assertEquals(0, threeEmotionMockEmotionAPI.getContempt());
        assertEquals(0, threeEmotionMockEmotionAPI.getDisgust());
    }
}
