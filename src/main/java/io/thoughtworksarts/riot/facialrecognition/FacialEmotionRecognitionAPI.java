package io.thoughtworksarts.riot.facialrecognition;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.util.Arrays;

public class FacialEmotionRecognitionAPI {

    private final int[] dataShape = new int[]{1, 1, 64, 64};
    private float[] emotionProbabilities;
    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;

    public FacialEmotionRecognitionAPI(ImageProcessor imageProcessor, DeepLearningProcessor deepLearningProcessor) {
        this.imageProcessor = imageProcessor;
        this.deepLearningProcessor = deepLearningProcessor;
        emotionProbabilities = new float[Emotion.values().length];
        Arrays.fill(emotionProbabilities, 0);
    }

    public void initialise() {
        recordEmotionProbabilities();
    }

    public void recordEmotionProbabilities() {
        File imageFile = imageProcessor.captureImage();
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        float[] emotionPrediction = deepLearningProcessor.getEmotionPrediction(imageData);
        System.arraycopy(emotionPrediction, 0, emotionProbabilities, 0, emotionPrediction.length);
    }

    public float getAnger() {
        return emotionProbabilities[Emotion.ANGER.getNumber()];
    }

    public float getCalm() {
        return emotionProbabilities[Emotion.CALM.getNumber()];
    }

    public float getFear() {
        return emotionProbabilities[Emotion.FEAR.getNumber()];
    }

    public float getSadness() {
        return emotionProbabilities[Emotion.SADNESS.getNumber()];
    }

    public float getSurprise() {
        return emotionProbabilities[Emotion.SURPRISE.getNumber()];
    }

    public float getContempt() {
        return emotionProbabilities[Emotion.CONTEMPT.getNumber()];
    }

    public float getDisgust() {
        return emotionProbabilities[Emotion.DISGUST.getNumber()];
    }

    public Emotion getDominateEmotion() {
        return Emotion.CALM;
    }
}