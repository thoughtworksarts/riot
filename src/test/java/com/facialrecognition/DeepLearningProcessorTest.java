package com.facialrecognition;

import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

class DeepLearningProcessorTest {

    @Test
    void shouldBeAbleToImportKerasModel() throws Exception {
        // given
        String fileName = this.getCompleteFileName("testimage.png");
        String h5File = this.getCompleteFileName("inception_v3_weights_dl4j.h5");
        String jsonFile = this.getCompleteFileName("inception_v3_model_dl4j.json");

        File imageFile = new File(fileName);

        BufferedImage image = ImageIO.read(imageFile);

        // when
        KerasModelImport.importKerasModelAndWeights(jsonFile, h5File);

        // then ????
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }

}