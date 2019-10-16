package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import com.google.common.util.concurrent.Uninterruptibles;
import io.thoughtworksarts.riot.logger.PerceptionLogger;
import io.thoughtworksarts.riot.utilities.JSONReader;
import org.nd4j.linalg.api.ndarray.INDArray;
//import org.nd4j.shade.guava.util.concurrent.Uninterruptibles;
//import org.nd4j.shade.guava.util.concurrent.Uninterruptibles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class FacialEmotionRecognitionAPI {

    private final int[] dataShape = new int[]{1, 1, 48, 48};
    private final String mode;
    private PerceptionLogger logger;
    private float[] emotionProbabilities;
    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private final Map<Emotion, Integer> emotionMap;
    private File imageFile;
    private volatile boolean webcamThreadRunning;
    private final ArrayList<String> VALID_APP_MODES = getValidAppModes();
    private String imagePath;

    public FacialEmotionRecognitionAPI(ImageProcessor imageProcessor, DeepLearningProcessor deepLearningProcessor, String emotionMapFile, String mode) {
        this(imageProcessor, deepLearningProcessor, emotionMapFile, mode,
                Paths.get(System.getProperty("user.home"), "Desktop/perception.io/image.jpg").toString());
    }

    public FacialEmotionRecognitionAPI(ImageProcessor imageProcessor, DeepLearningProcessor deepLearningProcessor, String emotionMapFile, String mode, String imagePath) {
        this.imagePath = imagePath;
        validateMode(mode);
        this.imageProcessor = imageProcessor;
        this.logger = new PerceptionLogger("FacialEmotionRecognitionAPI");
        this.deepLearningProcessor = deepLearningProcessor;
        this.emotionMap = loadEmotionMap(emotionMapFile);
        this.mode = mode;
        emotionProbabilities = new float[Emotion.values().length];
        Arrays.fill(emotionProbabilities, 0);
        webcamThreadRunning = true;
        startImageCapture();
    }

    private void startImageCapture() {
        new Thread(() -> {
            System.out.println();
            Webcam webcam = Webcam.getDefault();
            webcam.open();
            System.out.println("Webcam initialized");
            while (webcamThreadRunning) {
                BufferedImage image = webcam.getImage();
                imageFile = new File(imagePath);
                try {
                    ImageIO.write(image, "jpg", imageFile);
                    recordEmotionProbabilities();
                    Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            webcam.close();
        }).start();
    }

    public void endImageCapture() {
        webcamThreadRunning = false;
    }

    public void recordEmotionProbabilities() {
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, dataShape);
        float[] emotionPrediction = deepLearningProcessor.getEmotionPrediction(imageData);

        for (int index = 0; index < emotionPrediction.length; index++) {
            emotionProbabilities[index] += emotionPrediction[index];
        }
        printProbabilitiesToConsole();
    }

    private void printProbabilitiesToConsole() {
        for (Map.Entry<Emotion, Integer> entry : emotionMap.entrySet()) {
            System.out.println(String.format("%s - %f", entry.getKey(), emotionProbabilities[entry.getValue()]));
        }
        System.out.println();
    }

    public Emotion getDominantEmotion() {

        if (mode.contains("test")) {
            String testingEmotion = mode.split("-")[0];
            return Emotion.valueOf(testingEmotion.toUpperCase());
        }

        Map.Entry<Emotion, Integer> maxEmotionEntry = null;
        for (Map.Entry<Emotion, Integer> entry : emotionMap.entrySet()) {
            if (maxEmotionEntry == null) {
                maxEmotionEntry = entry;
            } else if (emotionProbabilities[maxEmotionEntry.getValue()] < emotionProbabilities[entry.getValue()]) {
                maxEmotionEntry = entry;
            }
        }
        emotionProbabilities = new float[Emotion.values().length];
        Arrays.fill(emotionProbabilities, 0);
        return maxEmotionEntry.getKey();
    }

    public Map<Emotion, Integer> loadEmotionMap(String emotionMapFile) {
        Map<String, Integer> emotionStringMap;
        Map<Emotion, Integer> enumEmotionMap = new HashMap<>();
        try {
            emotionStringMap = new ObjectMapper().readValue(JSONReader.readFile(emotionMapFile), HashMap.class);
            emotionStringMap.forEach((key, value) -> enumEmotionMap.put(Emotion.valueOf(key.toUpperCase()), value));
        } catch (IOException e) {
            logger.log(Level.INFO, "loadEmotionMap", e.getMessage(), null);
            e.printStackTrace();
        }
        return enumEmotionMap;
    }


    public void validateMode(String mode) {
        if (!isValidAppMode(mode)) {
            String validAppModeStr = String.join(", ", VALID_APP_MODES);
            throw new IllegalArgumentException(String.format("Please use a valid app mode in config file: %s", validAppModeStr));
        }
    }

    public boolean isValidAppMode(String mode) {
        return VALID_APP_MODES.contains(mode);
    }

    public ArrayList<String> getValidAppModes() {
        ArrayList<String> validAppModes = new ArrayList<>();
        validAppModes.add("installation");
        for (Emotion emotion : Emotion.values()) {
            validAppModes.add(emotion.name().toLowerCase() + "-test");
        }
        return validAppModes;
    }
}