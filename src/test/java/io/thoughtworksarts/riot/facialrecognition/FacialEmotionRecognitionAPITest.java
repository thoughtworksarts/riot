package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
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
        String PATH_TO_EMOTION_MAP_FILE = "src/test/resources/conv2d_emotion_map.json";
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE);
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        facialRecognition.recordEmotionProbabilities();

        verify(imageProcessor).prepareImageForNet(any(), any());
        verify(deepLearningProcessor).getEmotionPrediction(any());
    }

    @Test
    public void getDominantEmotionShouldReturnCalmWhenCalmHasTheHighestValue() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 5, 1});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(dominantEmotion, Emotion.CALM);
    }

    @Test
    public void getDominantEmotionShouldReturnCalm() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3, 4, 5, 6, 7});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(dominantEmotion, Emotion.CALM);
    }
}
