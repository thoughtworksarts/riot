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

    FacialEmotionRecognitionAPI facialRecognition;
    @Mock DeepLearningProcessor deepLearningProcessor;
    @Mock ImageProcessor imageProcessor;

    @Before
    public void setup() {
        initMocks(this);
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3});
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor);
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        facialRecognition.initialise();

        verify(imageProcessor).prepareImageForNet(any(), Mockito.anyInt(), Mockito.anyInt(), any());
        verify(deepLearningProcessor).getEmotionPrediction(any());
    }

    @Test
    public void shouldGetEmotionValuesSubSetAtInitialisationWhenDeepLearningProcessorReturns3Predictions() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3});
        facialRecognition.initialise();

        assertEquals(1, facialRecognition.getAnger());
        assertEquals(2, facialRecognition.getCalm());
        assertEquals(3, facialRecognition.getFear());
        assertEquals(0, facialRecognition.getSadness());
        assertEquals(0, facialRecognition.getSurprise());
        assertEquals(0, facialRecognition.getContempt());
        assertEquals(0, facialRecognition.getDisgust());
    }

    @Test
    public void shouldGetAllEmotionValuesSetAtInitialisationWhenDeepLearningProcessorReturnsAllPredictions() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3, 4, 5, 6, 7});
        facialRecognition.initialise();

        assertEquals(1, facialRecognition.getAnger());
        assertEquals(2, facialRecognition.getCalm());
        assertEquals(3, facialRecognition.getFear());
        assertEquals(4, facialRecognition.getSadness());
        assertEquals(5, facialRecognition.getSurprise());
        assertEquals(6, facialRecognition.getContempt());
        assertEquals(7, facialRecognition.getDisgust());
    }
}
