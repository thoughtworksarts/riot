package io.thoughtworksarts.riot.facialrecognition;

import org.deeplearning4j.datasets.iterator.DoublesDataSetIterator;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.File;

class DeepLearningProcessorTest {

    @Test
    @Disabled
    void shouldBeAbleToImportKerasModel() throws Exception {
        // given
        String fileName = this.getCompleteFileName("testimage.png");
        String h5File = this.getCompleteFileName("inception_v3_weights_dl4j.h5");
        String jsonFile = this.getCompleteFileName("inception_v3_model_dl4j.json");

        File imageFile = new File(fileName);

        BufferedImage image = ImageIO.read(imageFile);

        // when
        ComputationGraph model = KerasModelImport.importKerasModelAndWeights(jsonFile, h5File);

        // then ????
        DataSetIterator iterator = new DoublesDataSetIterator(null, 0);
        assertEquals("123", model.evaluate(iterator).stats());
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }

}