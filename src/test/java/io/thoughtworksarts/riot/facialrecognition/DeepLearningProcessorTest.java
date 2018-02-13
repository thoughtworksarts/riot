package io.thoughtworksarts.riot.facialrecognition;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeepLearningProcessorTest {

    private DeepLearningProcessor deepLearningProcessor;
    private File imageFile;

    @BeforeAll
    public void setup() {

        String h5File = this.getCompleteFileName("conv2d_weights.h5");
        String jsonFile = this.getCompleteFileName("conv2d_model.json");
        deepLearningProcessor = new DeepLearningProcessor(jsonFile, h5File);

        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);
    }

    @Test
    void shouldFindSameLayersWhenLoadsNetwork() {
        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");
        assertEquals(layerNames, deepLearningProcessor.getModelLayerNames());
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}