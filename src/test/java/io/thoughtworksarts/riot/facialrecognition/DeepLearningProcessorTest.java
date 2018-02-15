package io.thoughtworksarts.riot.facialrecognition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeepLearningProcessorTest {

    private DeepLearningProcessor deepLearningProcessor;
    private File imageFile;
    private ImageProcessor imageProcessor;

    @BeforeAll
    public void setup() {

        String h5File = this.getCompleteFileName("conv2d_weights.h5");
        String jsonFile = this.getCompleteFileName("conv2d_model.json");
        deepLearningProcessor = new DeepLearningProcessor(jsonFile, h5File);

        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);

        imageProcessor = new ImageProcessor();
    }

    @Test
    void shouldFindSameLayersWhenLoadsNetwork() {
        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");
        assertEquals(layerNames, deepLearningProcessor.getModelLayerNames());
    }

    @Test
    void shouldOutputSamePredictionValuesAsInPython() {
        float[] expectedPrediction = new float[]{0.0f, 0.24070886f, 0.0920638f};

        int[] dataShape = new int[]{1, 1, 64, 64};
        INDArray imageData = null;
        try {
            imageData = imageProcessor.prepareImageForNet(imageFile, 64, 64, dataShape);
        } catch (IOException e) {
            System.out.println("Unable to load and process test image.");
            e.printStackTrace();
        }
        float[] actualPrediction = deepLearningProcessor.getEmotionPrediction(imageData);

        float delta = 0.001f;
        assertArrayEquals(expectedPrediction, actualPrediction, delta);
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}