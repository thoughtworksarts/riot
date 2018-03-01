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

    public void recordEmotionProbabilities() {
        File imageFile = imageProcessor.captureImage();
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        float[] emotionPrediction = deepLearningProcessor.getEmotionPrediction(imageData);
        System.arraycopy(emotionPrediction, 0, emotionProbabilities, 0, emotionPrediction.length);
    }

    public Emotion getDominateEmotion() {
        Emotion maxEmotion = null;
        for (Emotion emotion : Emotion.values()){
            if( maxEmotion == null){
                maxEmotion = emotion;
            } else if (emotionProbabilities[maxEmotion.getNumber()] < emotionProbabilities[emotion.getNumber()]){
                maxEmotion = emotion;
            }
        }
        return maxEmotion;
    }
}