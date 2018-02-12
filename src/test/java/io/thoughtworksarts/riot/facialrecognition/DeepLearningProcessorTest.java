package io.thoughtworksarts.riot.facialrecognition;

import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.modelimport.keras.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.modelimport.keras.UnsupportedKerasConfigurationException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeepLearningProcessorTest {

    public static final int RGB_CHANNEL_COUNT = 3;
    public static final int PIXEL_COUNT = 3;
    private String h5File;
    private String jsonFile;
    private MultiLayerNetwork model;
    private File imageFile;

    @BeforeAll
    public void setup() throws UnsupportedKerasConfigurationException, IOException, InvalidKerasConfigurationException {
        h5File = this.getCompleteFileName("conv2d_weights.h5");
        jsonFile = this.getCompleteFileName("conv2d_model.json");
        model = KerasModelImport.importKerasSequentialModelAndWeights(jsonFile, h5File);

        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);
    }

    @Test
    void shouldFindSameLayersWhenLoadsNetwork() {
        List<String> layerNames = Arrays.asList("convolution2d_1", "convolution2d_2", "maxpooling2d_1", "convolution2d_3", "convolution2d_4", "maxpooling2d_2", "dense_1");
        assertEquals(layerNames, model.getLayerNames());
    }

    @Test
    void shouldFindSameRawPixelValuesInTestImage() throws IOException {
        int[] expectedPixels = new int[]{255, 251, 226, 255, 251, 230, 255, 254, 236};

        ImageLoader imageLoader = new ImageLoader();
        int[][] imageData = imageLoader.fromFile(imageFile);
        int[] actualPixels = new int[PIXEL_COUNT * RGB_CHANNEL_COUNT];
        // Only looks at first 3 pixels of 4096 pixels
        for (int pixelIndex = 0; pixelIndex < RGB_CHANNEL_COUNT; pixelIndex++) {
            actualPixels[pixelIndex*RGB_CHANNEL_COUNT] = this.getRPixelValue(imageData[0][pixelIndex]);
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+1] = this.getGPixelValue(imageData[0][pixelIndex]);
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+2] = this.getBPixelValue(imageData[0][pixelIndex]);
        }
        assertArrayEquals(expectedPixels, actualPixels);
    }


    private int getRPixelValue(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    private int getGPixelValue(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    private int getBPixelValue(int pixel) {
        return (pixel) & 0xff;
    }

    private void printPixelARGB(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
    }

    private INDArray prepareImageForNet(BufferedImage image) {
        ImageLoader imageLoader = new ImageLoader();
        INDArray image_array = imageLoader.asMatrix(image);
        INDArray test_data = image_array.ravel();
        int[] shape = new int[]{1, 1, 64, 64};
        test_data = test_data.reshape(shape);
        return test_data;
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}