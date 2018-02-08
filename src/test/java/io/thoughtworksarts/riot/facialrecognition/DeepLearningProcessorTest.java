package io.thoughtworksarts.riot.facialrecognition;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeepLearningProcessorTest {

    @Test
    void shouldBeAbleToImportKerasModel() throws Exception {
        // given
        String fileName = this.getCompleteFileName("testimage.png");
        String h5File = this.getCompleteFileName("conv2d_weights.h5");
        String jsonFile = this.getCompleteFileName("conv2d_model.json");
        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");

        File imageFile = new File(fileName);
        BufferedImage image = ImageIO.read(imageFile);

        // when
        MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights(jsonFile, h5File);

        // then
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