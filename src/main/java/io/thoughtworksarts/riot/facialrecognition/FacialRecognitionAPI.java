package io.thoughtworksarts.riot.facialrecognition;

import com.github.sarxos.webcam.Webcam;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FacialRecognitionAPI {

    private Map<Emotion, Integer> emotionMap;
    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;

    public FacialRecognitionAPI() {
        Map<Emotion, Integer> emotionMap = new HashMap<>();
        emotionMap.put(Emotion.ANGER, 0);
        emotionMap.put(Emotion.CALM, 1);
        emotionMap.put(Emotion.FEAR, 2);
        this.emotionMap = emotionMap;
    }

    public void initialise() {
        String h5File = this.getCompleteFileName("conv2d_weights.h5");
        String jsonFile = this.getCompleteFileName("conv2d_model.json");
        deepLearningProcessor = new DeepLearningProcessor(jsonFile, h5File);
        imageProcessor = new ImageProcessor();
        recordEmotionProbabilities();
    }

    public File captureImage() {
        // get default webcam and open it
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        // get image
        BufferedImage image = webcam.getImage();
        File imageFile = new File("image.jpg");
        try {
            ImageIO.write(image, "jpg", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    public void recordEmotionProbabilities() {
        File imageFile = captureImage();
        int[] dataShape = new int[]{1, 1, 64, 64};
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        emotionProbabilities = deepLearningProcessor.getEmotionPrediction(imageData);
    }

    public float getCalm() {
        return emotionProbabilities[emotionMap.get(Emotion.CALM)];
    }

    public float getFear() {
        return -1;
    }

    public float getAnger() {
        return -1;
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}
