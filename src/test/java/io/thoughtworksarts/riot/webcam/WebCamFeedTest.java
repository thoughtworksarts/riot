package io.thoughtworksarts.riot.webcam;

import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
public class WebCamFeedTest {

    @BeforeAll
    public static void assumeHardwareIsAvailable() {
        log.info("Checking if we have the necessary hardware.");

        try {
            Optional<Webcam> webcam = Optional.ofNullable(Webcam.getDefault());
            assumeTrue(webcam.isPresent());
        } catch (Exception e) {
            log.info("Failed to retrieve default webcam: {}", e.getMessage(), e);
            throw new TestAbortedException("Webcam is not available", e);
        }

    }
    
    @Test
    public void shouldGetHighestResolutionAvailable() {

        // given
        WebcamFeed webcamFeed = new WebcamFeed();

        // when
        Dimension highestResolutionAvailable = webcamFeed.getHighestResolutionAvailable();
        double actual = highestResolutionAvailable.getHeight() * highestResolutionAvailable.getWidth();

        // then
        boolean isHighestResolution = Arrays.stream(Webcam.getDefault().getViewSizes())
                .map(this::getResolution)
                .allMatch(area -> area <= actual);

        assertThat(isHighestResolution).isTrue();
    }

    @Test
    public void shouldCreatePictureFileWithProperResolution() throws Exception {
        // given
        WebcamFeed webcamFeed = new WebcamFeed();


        // when
        File file = webcamFeed.takePicture();

        // then
        log.info("File created: '{}'", file.getCanonicalPath());

        assertThat(file).exists();

        BufferedImage bufferedImage = ImageIO.read(file);
        Dimension highestResolutionAvailable = webcamFeed.getHighestResolutionAvailable();

        int actualResolution = this.getResolution(bufferedImage);
        int expectedResolution = this.getResolution(highestResolutionAvailable);

        assertThat(actualResolution).isEqualTo(expectedResolution);
    }

    private int getResolution(Dimension dimension) {
        Double area = dimension.getWidth() * dimension.getHeight();
        return area.intValue();
    }

    private int getResolution(BufferedImage bufferedImage) {
        return bufferedImage.getWidth() * bufferedImage.getHeight();
    }


}