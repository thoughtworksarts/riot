package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import io.thoughtworksarts.riot.utilities.JSONReader;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FacialEmotionRecognitionAPI {

    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;
    private FERNeuralNetConfigRoot configRoot;
    public final Map<String, Integer> emotionMap;

    public FacialEmotionRecognitionAPI(String configPath) {
        String jsonConfig = JSONReader.readFile(configPath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            configRoot = objectMapper.readValue(jsonConfig, FERNeuralNetConfigRoot.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        emotionMap = configRoot.getEmotionMap();
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
        emotionProbabilities = deepLearningProcessor.getEmotionPrediction(imageData);
    }

    public float getCalm() throws UnsupportedEmotionException {
        validateEmotion("calm");
        return emotionProbabilities[emotionMap.get("calm")];
    }

    public float getFear() throws UnsupportedEmotionException {
        validateEmotion("fear");
        return emotionProbabilities[emotionMap.get("fear")];
    }

    public float getAnger() throws UnsupportedEmotionException {
        validateEmotion("anger");
        return emotionProbabilities[emotionMap.get("anger")];
    }

    public float getDisgust() throws UnsupportedEmotionException {
        validateEmotion("disgust");
        return emotionProbabilities[emotionMap.get("disgust")];
    }

    public float getSurprise() throws UnsupportedEmotionException {
        validateEmotion("surprise");
        return emotionProbabilities[emotionMap.get("surprise")];
    }


    public float getContempt() throws UnsupportedEmotionException {
        validateEmotion("contempt");
        return emotionProbabilities[emotionMap.get("contempt")];
    }

    private void validateEmotion(String emotionString) throws UnsupportedEmotionException {
        if (!emotionMap.containsKey(emotionString)) {
            throw new UnsupportedEmotionException(emotionString);
        }
    }
}
