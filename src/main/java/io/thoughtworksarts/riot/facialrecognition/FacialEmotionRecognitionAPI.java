package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import com.google.common.util.concurrent.Uninterruptibles;
import io.thoughtworksarts.riot.utilities.JSONReader;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FacialEmotionRecognitionAPI {

    private final int[] dataShape = new int[]{1, 1, 48, 48};
    private final String mode;
    private float[] emotionProbabilities;
    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private final Map<Emotion, Integer> emotionMap;
    private File imageFile;
    private volatile boolean webcamThreadRunning;

    public FacialEmotionRecognitionAPI(ImageProcessor imageProcessor, DeepLearningProcessor deepLearningProcessor, String emotionMapFile, String mode) {
        this.imageProcessor = imageProcessor;
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
                imageFile = new File("image.jpg");
                try {
                    ImageIO.write(image, "jpg", imageFile);
                    recordEmotionProbabilities();
                    Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
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

        List<Integer> emotionIdList = emotionMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .sorted()
                .collect(Collectors.toList());

        for(int index=0; index < emotionPrediction.length; index++){
            emotionProbabilities[emotionIdList.get(index)]=emotionPrediction[index];
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

    public Emotion getDominantEmotion(Set<String> enabledEmotions){
        List<Emotion> enabledEmotionList = enabledEmotions.stream()
                .map(emotion -> Emotion.valueOf(emotion.toUpperCase()))
                .collect(Collectors.toList());

        if (mode.contains("test"))  return Emotion.CALM;

        Emotion maxEmotion = null;
        for (Emotion emotion : enabledEmotionList){
            if( maxEmotion == null){
                maxEmotion = emotion;
            } else if (emotionProbabilities[maxEmotion.getNumber()] < emotionProbabilities[emotion.getNumber()]){
                maxEmotion = emotion;
            }
        }
        return maxEmotion;
    }

    public Map<Emotion, Integer> loadEmotionMap(String emotionMapFile) {
        Map<String,Integer> emotionStringMap;
        Map<Emotion, Integer> enumEmotionMap = new HashMap<>();
        try {
            emotionStringMap = new ObjectMapper().readValue(JSONReader.readFile(emotionMapFile), HashMap.class);
            emotionStringMap.forEach( (key,value) -> enumEmotionMap.put(Emotion.valueOf(key.toUpperCase()),value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enumEmotionMap;
    }

}