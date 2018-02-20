package io.thoughtworksarts.riot.facialrecognition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MockFacialRecognitionAPI extends FacialRecognitionAPI {

    DeepLearningProcessor deepLearningProcessor;
    ImageProcessor imageProcessor;
    Map<Emotion, Integer> emotionMap;

    public MockFacialRecognitionAPI() {
        Map<Emotion, Integer> emotionMap = new HashMap<>();
        emotionMap.put(Emotion.ANGER, 0);
        emotionMap.put(Emotion.CALM, 1);
        emotionMap.put(Emotion.FEAR, 2);
        this.emotionMap = emotionMap;
    }

    public MockFacialRecognitionAPI(DeepLearningProcessor deepLearningProcessor, ImageProcessor imageProcessor) {
        this();
        this.deepLearningProcessor = deepLearningProcessor;
        this.imageProcessor = imageProcessor;
    }

    @Override
    public File captureImage() {
        String fileName = this.getCompleteFileName("testimage.png");
        return new File(fileName);
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}
