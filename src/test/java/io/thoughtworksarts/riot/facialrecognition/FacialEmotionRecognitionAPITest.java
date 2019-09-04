package io.thoughtworksarts.riot.facialrecognition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;
import java.util.TreeSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacialEmotionRecognitionAPITest {

    FacialEmotionRecognitionAPI facialRecognition;
    Set<String> enabledEmotions;
    private String PATH_TO_EMOTION_MAP_FILE = "src/test/resources/conv2d_emotion_map.json";
    private String INSTALLATION_MODE = "installation";
    private String CALM_TEST_MODE = "calm-test";
    private String FEAR_TEST_MODE = "fear-test";
    private String ANGER_TEST_MODE = "anger-test";
    @Mock DeepLearningProcessor deepLearningProcessor;
    @Mock ImageProcessor imageProcessor;

    @Before
    public void setup() {
        initMocks(this);
        enabledEmotions = new TreeSet<>();
        enabledEmotions.add("anger");
        enabledEmotions.add("fear");
        enabledEmotions.add("calm");
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE, INSTALLATION_MODE);
    }

    @Test
    public void shouldRecordEmotionProbabilitiesOnInitialise() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 3});

        facialRecognition.recordEmotionProbabilities();

        verify(imageProcessor).prepareImageForNet(any(), any());
        verify(deepLearningProcessor).getEmotionPrediction(any());
    }

    @Test
    public void getDominantEmotionShouldReturnCalmWhenCalmHasTheHighestValue() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 1, 5});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.CALM,dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnCalm() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 2, 8});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.CALM,dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnFear() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 8, 3});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.FEAR,dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnAnger() {
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{4, 2, 0});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.ANGER,dominantEmotion);
    }
    @Test
    public void getDominantEmotionShouldReturnFearEvenWhenCalmHasAHigherValueAndIsDisabled() {
        enabledEmotions = new TreeSet<>();
        enabledEmotions.add("fear");
        enabledEmotions.add("anger");

        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 3, 4});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.FEAR,dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnAngerEvenWhenCalmHasAHigherValueAndIsDisabled() {
        enabledEmotions = new TreeSet<>();
        enabledEmotions.add("anger");

        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{1, 3, 4});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.ANGER,dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnCalmWhenInCalmTestMode() {
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE, CALM_TEST_MODE);
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{3, 3 ,3});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.CALM, dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnFearWhenInFearTestMode() {
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE, FEAR_TEST_MODE);
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{3, 3 ,3});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.FEAR, dominantEmotion);
    }

    @Test
    public void getDominantEmotionShouldReturnAngerWhenInAngerTestMode() {
        facialRecognition = new FacialEmotionRecognitionAPI(imageProcessor, deepLearningProcessor, PATH_TO_EMOTION_MAP_FILE, ANGER_TEST_MODE);
        when(deepLearningProcessor.getEmotionPrediction(any())).thenReturn(new float[]{3, 3 ,3});
        facialRecognition.recordEmotionProbabilities();

        Emotion dominantEmotion = facialRecognition.getDominantEmotion();
        assertEquals(Emotion.ANGER, dominantEmotion);
    }

    @Test
    public void shouldReturnFalseWhenGivenInvalidModeValueInConfig() {
        assertFalse(facialRecognition.isValidAppMode("ggg-test"));
    }
}
