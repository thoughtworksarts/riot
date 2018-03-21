package io.thoughtworksarts.riot.facialrecognition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImageProcessorTest {

    public static final int RGB_CHANNEL_COUNT = 3;
    public static final int PIXEL_COUNT = 3;
    private ImageProcessor imageProcessor;
    private BufferedImage colorImage;
    private File imageFile;

    @BeforeEach
    void setup() {
        imageProcessor = new ImageProcessor();
        String fileName = this.getCompleteFileName("testimage.png");
        imageFile = new File(fileName);
        colorImage = imageProcessor.loadImage(imageFile);
    }

    @Test
    void shouldFindSameRawPixelValuesInTestImage() {
        int[] expectedPixels = new int[]{255, 251, 226, 255, 251, 230, 255, 254, 236};
        int[] actualPixels = getRGBImagePixelSubset();

        assertArrayEquals(expectedPixels, actualPixels);
    }

    @Test
    void shouldFindSamePixelValuesAfterTransformingImageToGrayscale() {
        int[] expectedPixels = new int[]{250, 250, 252, 243, 216, 242, 255, 255, 255, 255};
        BufferedImage grayImage = imageProcessor.convertImageToGrayscale(colorImage);
        int[] actualPixels = this.getGrayImagePixelSubset(10, grayImage);

        assertArrayEquals(expectedPixels, actualPixels);
    }

    @Test
    void shouldFindSamePixelValuesAfterResizeImage() {
        int[] expectedPixels = new int[]{251, 237, 242, 255, 254, 255, 255, 255, 255, 255};
        BufferedImage grayImage = imageProcessor.convertImageToGrayscale(colorImage);
        BufferedImage resizedImage = imageProcessor.resizeImage(grayImage, 64, 64);

        for (int i = 0; i < 10; i++) {
            int actualPixel = resizedImage.getRaster().getSample(0, i, 0);
            int expectedPixel = expectedPixels[i];
            assertTrue(Math.abs(actualPixel-expectedPixel) <= 1);
        }
    }

    @Test
    void shouldFindSameValuesInPreppedDataAsInPython() {
        int[] dataShape = new int[]{1, 1, 64, 64};
        INDArray data = imageProcessor.prepareImageForNet(imageFile, dataShape);

        float[] expectedValues = new float[]{0.988151985294f, 0.932896623775f, 0.951524050245f, 1.0f, 0.999845373775f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        float[] actualValues = Arrays.copyOfRange(data.ravel().data().asFloat(), 0, 10);

        float delta = 0.01f;
        assertArrayEquals(expectedValues, actualValues, delta);
    }

    private int[] getRGBImagePixelSubset() {
        int[] actualPixels = new int[PIXEL_COUNT * RGB_CHANNEL_COUNT];
        // Only returns first 3 pixels of 4096 pixels
        for (int pixelIndex = 0; pixelIndex < RGB_CHANNEL_COUNT; pixelIndex++) {
            actualPixels[pixelIndex*RGB_CHANNEL_COUNT] = imageProcessor.getRPixelValue(colorImage.getRGB(0, pixelIndex));
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+1] = imageProcessor.getGPixelValue(colorImage.getRGB(0, pixelIndex));
            actualPixels[(pixelIndex*RGB_CHANNEL_COUNT)+2] = imageProcessor.getBPixelValue(colorImage.getRGB(0, pixelIndex));
        }
        return actualPixels;
    }

    private int[] getGrayImagePixelSubset(int pixelCount, BufferedImage image) {
        int[] pixelSubset = new int[pixelCount];
        for (int pixelCol = 0; pixelCol < pixelCount; pixelCol++) {
            pixelSubset[pixelCol] = image.getRaster().getSample(0, pixelCol, 0);
        }
        return pixelSubset;
    }

    private String getCompleteFileName(String relativePath) {
        return Thread
                .currentThread()
                .getContextClassLoader()
                .getResource(relativePath)
                .getFile();
    }
}
