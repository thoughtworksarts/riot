package io.thoughtworksarts.riot.facialrecognition;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

public class MockFacialEmotionRecognitionAPI extends FacialEmotionRecognitionAPI {

    private DeepLearningProcessor mockDeepLearningProcessor;
    private ImageProcessor mockImageProcessor;
    private float[] emotionProbabilities;

    public MockFacialEmotionRecognitionAPI(String configPath) {
        super(configPath);
    }

    public MockFacialEmotionRecognitionAPI(String configPath, DeepLearningProcessor mockDeepLearningProcessor, ImageProcessor mockImageProcessor) {
        super(configPath);
        this.mockDeepLearningProcessor = mockDeepLearningProcessor;
        this.mockImageProcessor = mockImageProcessor;
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
        INDArray imageData = mockImageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        emotionProbabilities = mockDeepLearningProcessor.getEmotionPrediction(imageData);
    }
}
