package io.thoughtworksarts.riot.webcam;


import com.github.sarxos.webcam.Webcam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class WebcamFeed {

    private final Webcam webcam;

    public WebcamFeed() {
        this.webcam = Webcam.getDefault();
        Dimension highestResAvailable = getHighestResolutionAvailable();
        this.webcam.setViewSize(highestResAvailable);
    }


    public File takePicture() {

        try (WebcamAutoCloseableAdapter webcamAdapter = new WebcamAutoCloseableAdapter(this.webcam)) {

            File file = File.createTempFile("capture", ".png");

            BufferedImage image = webcamAdapter.getImage();

            ImageIO.write(image, "PNG", file);

            return file;
        } catch (Exception e) {
            throw new RuntimeException("Unable to take picture: " + e.getMessage(), e);
        }
    }

    public Dimension getHighestResolutionAvailable() {
        return Arrays.stream(webcam.getViewSizes())
                .map(WebcamAndResolutionPair::fromDimension)
                .max(Comparator.naturalOrder())
                .map(WebcamAndResolutionPair::getDimension)
                .orElseThrow(RuntimeException::new);
    }


    private class WebcamAutoCloseableAdapter implements AutoCloseable {

        private final Webcam webcam;

        WebcamAutoCloseableAdapter(Webcam webcam) {
            this.webcam = webcam;
            this.webcam.open();
        }

        public BufferedImage getImage() {
            return this.webcam.getImage();
        }

        @Override
        public void close() throws Exception {
            webcam.close();
        }
    }

    @RequiredArgsConstructor
    private static class WebcamAndResolutionPair implements Comparable<WebcamAndResolutionPair> {

        @Getter
        private final Double area;

        @Getter
        private final Dimension dimension;

        static WebcamAndResolutionPair fromDimension(Dimension dimension) {
            return new WebcamAndResolutionPair(dimension.getHeight() * dimension.getWidth(), dimension);
        }

        @Override
        public int compareTo(WebcamAndResolutionPair o) {
            Objects.requireNonNull(o);

            return this.area.compareTo(o.area);
        }
    }

}
