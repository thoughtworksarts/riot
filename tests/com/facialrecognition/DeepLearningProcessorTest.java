package com.facialrecognition;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DeepLearningProcessorTest {
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getEmotionValue() {
        // SETUP
        BufferedImage image = null;
        String imageFile = "TestResources/testimage.png";
        System.out.println(System.getProperty("java.library.path"));
        try {
            image = ImageIO.read(new File(imageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ACT
        DeepLearningProcessor processor = new DeepLearningProcessor("TestResources/inception_v3_weights_dl4j.h5", "TestResources/inception_v3_model_dl4j.json");
        float emotionValue = processor.GetEmotionValue(image, imageFile);

        // ASSERT
        assertEquals(0.976932, emotionValue);
    }

}