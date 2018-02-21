package io.thoughtworksarts.riot.facialrecognition;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.thoughtworksarts.riot.facialrecognition.Emotion.*;

public class FacialEmotionRecognitionAPI {

    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;
    private String jsonModelFile;
    private String h5WeightsFile;
    Map<Emotion, Integer> emotionMap;

    public FacialEmotionRecognitionAPI() {
        Map<Emotion, Integer> emotionMap = new HashMap<>();
        emotionMap.put(Emotion.ANGER, 0);
        emotionMap.put(Emotion.CALM, 1);
        emotionMap.put(Emotion.FEAR, 2);
        this.emotionMap = emotionMap;
    }

    public FacialEmotionRecognitionAPI(String configPath) {
        this();
        File configFile = new File(getCompleteFileName(configPath));
        HashMap<String,Object> result = null;
        try {
            result = new ObjectMapper().readValue(configFile, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonModelFile = getCompleteFileName((String)result.get("model_file"));
        h5WeightsFile = getCompleteFileName((String)result.get("weights_file"));
    }

    public void initialise() {
        deepLearningProcessor = new DeepLearningProcessor(jsonModelFile, h5WeightsFile);
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

    public float getCalm() {
        return emotionProbabilities[CALM.getNumber()];
    }

    public float getFear() {
        return emotionProbabilities[FEAR.getNumber()];
    }

    public float getAnger() {
        return emotionProbabilities[ANGER.getNumber()];
    }

    private String getCompleteFileName(String relativePath) {
        String filepath = Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();

        boolean isWindows = System.getProperty("os.name").contains("indow");
        return isWindows ? filepath.substring(1) : filepath;
    }
}
