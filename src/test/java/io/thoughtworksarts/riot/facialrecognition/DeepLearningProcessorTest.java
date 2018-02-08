package io.thoughtworksarts.riot.facialrecognition;

import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeepLearningProcessorTest {

    private String h5File;
    private String jsonFile;
    private MultiLayerNetwork model;

    @BeforeAll
    public void setup() throws UnsupportedKerasConfigurationException, IOException, InvalidKerasConfigurationException {
        h5File = this.getCompleteFileName("conv2d_weights.h5");
        jsonFile = this.getCompleteFileName("conv2d_model.json");
        model = KerasModelImport.importKerasSequentialModelAndWeights(jsonFile, h5File);
        System.out.println("Here");
    }

    @Test
    void shouldFindSameLayersWhenLoadsNetwork() {
        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");
        assertEquals(layerNames, model.getLayerNames());
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}