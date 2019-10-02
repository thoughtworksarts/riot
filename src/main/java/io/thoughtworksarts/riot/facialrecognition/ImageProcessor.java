package io.thoughtworksarts.riot.facialrecognition;

import com.github.sarxos.webcam.Webcam;
import io.thoughtworksarts.riot.logger.PerceptionLogger;
import org.datavec.image.loader.ImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ImageProcessor {

    private PerceptionLogger logger;

    public ImageProcessor(){
        this.logger = new PerceptionLogger("ImageProcessor");
    }
    public INDArray prepareImageForNet(File imageFile, int[] targetDataShape) {
        BufferedImage image = loadImage(imageFile);
        image = convertImageToGrayscale(image);
        image = resizeImage(image, targetDataShape[2], targetDataShape[3]);
        INDArray imageData = normalizeImageData(image);
        return imageData.reshape(targetDataShape);
    }

    public File captureImage() {
        // get default webcam and open it
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        // get image
        BufferedImage image = webcam.getImage();
        File imageFile = new File("image.jpg");
        try {
            ImageIO.write(image, "jpg", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webcam.close();
        return imageFile;
    }

    public BufferedImage loadImage(File imageFile) {
        ImageLoader imageLoader = new ImageLoader();
        int[][] colorImageData = new int[0][];
        try {
            colorImageData = imageLoader.fromFile(imageFile);
        } catch (IOException e) {
            logger.log(Level.INFO, "loadImage", e.getMessage(), null);
            System.out.println("Unable to read data from test image file.");
            e.printStackTrace();
        }
        BufferedImage colorImage = new BufferedImage(colorImageData.length, colorImageData[0].length, BufferedImage.TYPE_INT_ARGB);
        for (int pixelRow = 0; pixelRow < colorImage.getWidth(); pixelRow++) {
            for (int pixelCol = 0; pixelCol < colorImage.getHeight(); pixelCol++) {
                colorImage.setRGB(pixelRow, pixelCol, colorImageData[pixelRow][pixelCol]);
            }
        }
        return colorImage;
    }

    public BufferedImage convertImageToGrayscale(BufferedImage colorImage) {
        BufferedImage grayImage = new BufferedImage(colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int rowIndex = 0; rowIndex < colorImage.getWidth(); ++rowIndex) {
            for (int colIndex = 0; colIndex < colorImage.getHeight(); ++colIndex) {
                int rgb = colorImage.getRGB(rowIndex, colIndex);
                int grayPixel = getGrayPixelValue(rgb);
                grayImage.getRaster().setSample(rowIndex, colIndex, 0, grayPixel);
            }
        }
        return grayImage;
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        return resizedImage;
    }

    private INDArray normalizeImageData(BufferedImage image) {
        INDArray normalizedData = Nd4j.zeros(image.getWidth(), image.getHeight());
        for (int rowIdx = 0; rowIdx < image.getWidth(); rowIdx++) {
            for (int colIdx = 0; colIdx < image.getHeight(); colIdx++) {
                float normalizedValue = getNormalizedPixelValue(image, rowIdx, colIdx);
                normalizedData.put(rowIdx, colIdx, normalizedValue);
            }
        }
        return normalizedData;
    }

    private int getGrayPixelValue(int rgb) {
        int r = getRPixelValue(rgb);
        int g = getGPixelValue(rgb);
        int b = getBPixelValue(rgb);
        // uses rgb to grayscale formula from Python skimage color library method rgb2gray()
        return (int)Math.floor((r * 0.2125f) + (g * 0.7154f) + (b * 0.0721f));
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

    private float getNormalizedPixelValue(BufferedImage image, int rowIdx, int colIdx) {
        return (image.getRaster().getSample(rowIdx, colIdx, 0)) / 255.0f;
    }
}
