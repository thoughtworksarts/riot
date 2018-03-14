package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import io.thoughtworksarts.riot.utilities.JSONReader;
import javassist.NotFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacialEmotionRecognitionAPI {

    private final int[] dataShape = new int[]{1, 1, 64, 64};
    private float[] emotionProbabilities;
    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private final Map<Emotion, Integer> emotionMap;
    private File imageFile;
    private Thread webcamThread;
    private volatile boolean webcamThreadRunning;

    public FacialEmotionRecognitionAPI(ImageProcessor imageProcessor, DeepLearningProcessor deepLearningProcessor, String emotionMapFile) {
        this.imageProcessor = imageProcessor;
        this.deepLearningProcessor = deepLearningProcessor;
        this.emotionMap = loadEmotionMap(emotionMapFile);
        emotionProbabilities = new float[Emotion.values().length];
        Arrays.fill(emotionProbabilities, 0);
        webcamThreadRunning = true;
        startImageCapture();
    }

    private void startImageCapture() {
        webcamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println();
                Webcam webcam = Webcam.getDefault();
                webcam.open();
                System.out.println("Webcam initialized");
                while (webcamThreadRunning) {
                    BufferedImage image = webcam.getImage();
                    imageFile = new File("image.jpg");
                    try {
                        ImageIO.write(image, "jpg", imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recordEmotionProbabilities();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                webcam.close();
            }
        });
        webcamThread.start();
    }

    public void endImageCapture() {
        webcamThreadRunning = false;
    }

    public void recordEmotionProbabilities() {
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        float[] emotionPrediction = deepLearningProcessor.getEmotionPrediction(imageData);
        for (Map.Entry<Emotion, Integer> entry : emotionMap.entrySet()) {
            emotionProbabilities[entry.getKey().getNumber()] = emotionPrediction[entry.getValue()];
        }
        printProbabilitiesToConsole();
    }

    private void printProbabilitiesToConsole() {
        System.out.println();
        for (Map.Entry<Emotion, Integer> entry : emotionMap.entrySet()) {
            System.out.println(String.format("%s - %f", entry.getKey().name(), emotionProbabilities[entry.getKey().getNumber()]));
        }
        System.out.println();
    }

    public Emotion getDominantEmotion() {
        Emotion maxEmotion = null;
        for (Emotion emotion : Emotion.values()){
            if( maxEmotion == null){
                maxEmotion = emotion;
            } else if (emotionProbabilities[maxEmotion.getNumber()] < emotionProbabilities[emotion.getNumber()]){
                maxEmotion = emotion;
            }
        }
        return Emotion.CALM;
    }

    public Map<Emotion, Integer> loadEmotionMap(String emotionMapFile) {
        JSONReader jsonReader = new JSONReader();
        Map<String,Integer> emotionStringMap = new HashMap<>();
        try {
             emotionStringMap = new ObjectMapper().readValue(jsonReader.readFile(emotionMapFile), HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<Emotion, Integer> emotionMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : emotionStringMap.entrySet()) {
            Emotion emotion = null;
            try {
                emotion = getEmotionFromString(entry.getKey());
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            emotionMap.put(emotion, entry.getValue());
        }
        return emotionMap;
    }

    private Emotion getEmotionFromString(String emotionString) throws NotFoundException {
        for (Emotion emotion: Emotion.values()) {
            if (emotion.name().toLowerCase().equals(emotionString)) {
                return emotion;
            }
        }
        throw new NotFoundException(String.format("Emotion not valid: %s", emotionString));
    }
}