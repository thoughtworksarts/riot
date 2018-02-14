package io.thoughtworksarts.riot.facialrecognition;

import org.datavec.image.loader.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageProcessor {

    public BufferedImage convertImageToGrayscale(BufferedImage colorImage) {
        BufferedImage grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int rowIndex = 0; rowIndex < colorImage.getWidth(); ++rowIndex) {
            for (int colIndex = 0; colIndex < colorImage.getHeight(); ++colIndex) {
                int rgb = colorImage.getRGB(rowIndex, colIndex);
                int r = getRPixelValue(rgb);
                int g = getGPixelValue(rgb);
                int b = getBPixelValue(rgb);

                // uses rgb to grayscale formula from Python skimage color library method rgb2gray()
                int grayPixel = (int)Math.floor((r * 0.2125f) + (g * 0.7154f) + (b * 0.0721f));
                grayImage.getRaster().setSample(rowIndex, colIndex, 0, grayPixel);
            }
        }
        return grayImage;
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int newW, int newH) {
        BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, newW, newH, null);
        g2d.dispose();

        return resizedImage;
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

    public BufferedImage loadImage(File imageFile) throws IOException {
        ImageLoader imageLoader = new ImageLoader();
        int[][] colorImageData = imageLoader.fromFile(imageFile);
        BufferedImage colorImage = new BufferedImage(colorImageData.length, colorImageData[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int pixelRow = 0; pixelRow < colorImage.getWidth(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < colorImage.getHeight(); pixelCol++) {
                colorImage.setRGB(pixelRow, pixelCol, colorImageData[pixelRow][pixelCol]);
            }
        }
        return colorImage;
    }
}
