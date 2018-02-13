package io.thoughtworksarts.riot.facialrecognition;

import org.datavec.image.loader.ImageLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageProcessorTest {

    public static final int RGB_CHANNEL_COUNT = 3;
    public static final int PIXEL_COUNT = 3;
    private ImageProcessor imageProcessor;
    private int[][] imageData;
    private float[] grayImageData;
    private File imageFile;

    @BeforeAll
    void setup() throws IOException {
        imageProcessor = new ImageProcessor();

        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);
        ImageLoader imageLoader = new ImageLoader();
        imageData = imageLoader.fromFile(imageFile);
    }

    @Test
    void shouldFindSameRawPixelValuesInTestImage() throws IOException {
        int[] expectedPixels = new int[]{255, 251, 226, 255, 251, 230, 255, 254, 236};
        int[] actualPixels = new int[PIXEL_COUNT * RGB_CHANNEL_COUNT];
        // Only looks at first 3 pixels of 4096 pixels
        for (int pixelIndex = 0; pixelIndex < RGB_CHANNEL_COUNT; pixelIndex++) {
            actualPixels[pixelIndex*RGB_CHANNEL_COUNT] = imageProcessor.getRPixelValue(imageData[0][pixelIndex]);
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+1] = imageProcessor.getGPixelValue(imageData[0][pixelIndex]);
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+2] = imageProcessor.getBPixelValue(imageData[0][pixelIndex]);
        }
        assertArrayEquals(expectedPixels, actualPixels);
    }

    @Test
    void shouldFindSamePixelValuesAfterTransformingImageToGrayscale() throws IOException {
        float[] expectedPixels = new float[]{0.980578431373f, 0.981709411765f, 0.991822352941f, 0.954831764706f, 0.849507058824f, 0.950403529412f, 1.0f, 1.0f, 1.0f, 1.0f};

        grayImageData = imageProcessor.convertImageToGrayscale(imageFile);
        //only looks at first 10 pixels
        float[] actualPixels = Arrays.copyOfRange(grayImageData, 0, 10);

        float delta = 0.0001f;
        assertArrayEquals(expectedPixels, actualPixels, delta);
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
