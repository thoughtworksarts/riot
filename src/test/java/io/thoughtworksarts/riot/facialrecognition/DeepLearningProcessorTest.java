package io.thoughtworksarts.riot.facialrecognition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

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
        deepLearningProcessor = new DeepLearningProcessor(h5File);

        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);

        imageProcessor = new ImageProcessor();
    }

//    @Test
//    void shouldFindSameLayersWhenLoadsNetwork() {
//        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");
//        assertEquals(layerNames, deepLearningProcessor.getModelLayerNames());
//    }

    @Test @Disabled
    void shouldOutputSamePredictionValuesAsInPythonWhenGivenSameImage() {
        float[] expectedPrediction = new float[]{0.0f, 0.24070886f, 0.0920638f};

        int[] dataShape = new int[]{1, 1, 64, 64};
        INDArray imageData = imageProcessor.prepareImageForNet(imageFile, dataShape);
        float[] actualPrediction = deepLearningProcessor.getEmotionPrediction(imageData);

        float delta = 0.001f;
        assertArrayEquals(expectedPrediction, actualPrediction, delta);
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