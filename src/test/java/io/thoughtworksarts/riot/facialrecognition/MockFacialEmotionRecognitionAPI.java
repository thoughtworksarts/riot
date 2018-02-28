package io.thoughtworksarts.riot.facialrecognition;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

public class MockFacialEmotionRecognitionAPI extends FacialEmotionRecognitionAPI {

    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;

    public MockFacialEmotionRecognitionAPI(String configPath) {
        super(configPath);
    }

    public MockFacialEmotionRecognitionAPI(String configPath, DeepLearningProcessor deepLearningProcessor, ImageProcessor imageProcessor) {
        super(configPath);
        this.deepLearningProcessor = deepLearningProcessor;
        this.imageProcessor = imageProcessor;
        emotionProbabilities = new float[Emotion.values().length];
    }

    @Override
    public void initialise() {
        recordEmotionProbabilities();
    }

    @Override
    public File captureImage() {
        return new File("src/test/resources/testimage.png");
    }

    @Override
    public void recordEmotionProbabilities() {
        File imageFile = captureImage();
        int[] dataShape = new int[]{1, 1, 1, 1};
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        float[] emotionPrediction = deepLearningProcessor.getEmotionPrediction(imageData);
        System.arraycopy(emotionPrediction, 0, emotionProbabilities, 0, emotionPrediction.length);
    }

    @Override
    public float getCalm() {
        return emotionProbabilities[Emotion.CALM.getNumber()];
    }

    @Override
    public float getAnger() {
        return emotionProbabilities[Emotion.ANGER.getNumber()];
    }

    @Override
    public float getFear() {
        return emotionProbabilities[Emotion.FEAR.getNumber()];
    }

    public float[] getEmotionProbabilities() {
        return emotionProbabilities;
    }
}
