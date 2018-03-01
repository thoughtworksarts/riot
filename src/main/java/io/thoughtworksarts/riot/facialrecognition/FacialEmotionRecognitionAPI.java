package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import io.thoughtworksarts.riot.utilities.JSONReader;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FacialEmotionRecognitionAPI {

    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;
    private FERNeuralNetConfigRoot configRoot;

    public FacialEmotionRecognitionAPI(String configPath) {
        String jsonConfig = JSONReader.readFile(configPath);
        ObjectMapper objectMapper = new ObjectMapper();
        emotionProbabilities = new float[Emotion.values().length];
        Arrays.fill(emotionProbabilities, 0);
        try {
            configRoot = objectMapper.readValue(jsonConfig, FERNeuralNetConfigRoot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialise() {
        deepLearningProcessor = new DeepLearningProcessor(configRoot.getModelFile(), configRoot.getWeightsFile());
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
        webcam.close();
        return imageFile;
    }

    public void recordEmotionProbabilities() {
        File imageFile = captureImage();
        int[] dataShape = new int[]{1, 1, 64, 64};
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
