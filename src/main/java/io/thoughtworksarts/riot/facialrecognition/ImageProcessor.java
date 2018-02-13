package io.thoughtworksarts.riot.facialrecognition;

import org.datavec.image.loader.ImageLoader;
import org.nd4j.linalg.util.ArrayUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {

    public float[] convertImageToGrayscale(File imageFile) throws IOException {
        ImageLoader imageLoader = new ImageLoader();
        BufferedImage colorImage = imageLoader.toImage(imageLoader.asMatrix(imageFile));

        float[][] grayImageData = new float[colorImage.getWidth()][colorImage.getHeight()];
        for (int rowIndex = 0; rowIndex < colorImage.getWidth(); ++rowIndex) {
            for (int colIndex = 0; colIndex < colorImage.getHeight(); ++colIndex) {
                int rgb = colorImage.getRGB(rowIndex, colIndex);
                int r = getRPixelValue(rgb);
                int g = getGPixelValue(rgb);
                int b = getBPixelValue(rgb);

                // uses rgb to grayscale formula from Python skimage color library method rgb2gray()
                float gray = (r * 0.2125f) + (g * 0.7154f) + (b * 0.0721f);
                grayImageData[rowIndex][colIndex] = gray/255;
            }
        }
        return ArrayUtil.flatten(grayImageData);
    }

    public int getRPixelValue(int pixel) {
        return (pixel >> 16) & 0xff;
    }

    public int getGPixelValue(int pixel) {
        return (pixel >> 8) & 0xff;
    }

    public int getBPixelValue(int pixel) {
        return (pixel) & 0xff;
    }
}
