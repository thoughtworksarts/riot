package io.toughtworksarts.riot.webcam;

import com.github.sarxos.webcam.Webcam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WebCamFeedTest {

    @Test
    public void shouldBeAbleToLoadImageFromWebCam() throws Exception {
        // given
        Webcam webcam = Webcam.getDefault();
        File file = File.createTempFile("capture", ".png");
        Dimension highestResAvailable = Arrays.asList(webcam.getViewSizes())
                .stream()
                .map(Tuple::fromDimension)
                .max(Comparator.naturalOrder())
                .map(Tuple::getDimension)
                .orElseThrow(Exception::new);


        // when
        webcam.setViewSize(highestResAvailable);
        webcam.open();
        BufferedImage image = webcam.getImage();
        ImageIO.write(image, "PNG", file);
        webcam.close();

        // then
        log.info("File created: '{}'", file.getCanonicalPath());
        assertThat(file).exists();

        double expected = highestResAvailable.getHeight() * highestResAvailable.getWidth();
        double actual = image.getHeight() * image.getWidth();

        assertThat(expected).isEqualTo(actual);
    }


    @RequiredArgsConstructor
    static class Tuple implements Comparable<Tuple> {

        @Getter
        private final Double area;

        @Getter
        private final Dimension dimension;

        public static Tuple fromDimension(Dimension dimension) {
            return new Tuple(dimension.getHeight() * dimension.getWidth(), dimension);
        }

        @Override
        public int compareTo(Tuple o) {
            return this.area.compareTo(o.area);
        }
    }

}
