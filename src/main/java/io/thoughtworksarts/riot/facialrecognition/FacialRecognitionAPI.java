package io.thoughtworksarts.riot.facialrecognition;

import com.github.sarxos.webcam.Webcam;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static io.thoughtworksarts.riot.facialrecognition.Emotion.*;

public class FacialRecognitionAPI {

    private DeepLearningProcessor deepLearningProcessor;
    private ImageProcessor imageProcessor;
    private float[] emotionProbabilities;

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
