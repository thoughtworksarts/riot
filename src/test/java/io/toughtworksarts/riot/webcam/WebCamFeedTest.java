package io.toughtworksarts.riot.webcam;

import com.github.sarxos.webcam.Webcam;
import io.thoughtworksarts.riot.WebcamFeed;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WebCamFeedTest {

    @Test
    public void shouldGetHighestResolutionAvailable() {

        // given
        WebcamFeed webcamFeed = new WebcamFeed();

        // when
        Dimension highestResolutionAvailable = webcamFeed.getHighestResolutionAvailable();
        double actual = highestResolutionAvailable.getHeight() * highestResolutionAvailable.getWidth();

        // then
        Arrays.asList(Webcam.getDefault().getViewSizes())
                .stream()
                .map(this::toArea)
                .allMatch(area -> area <= actual);
    }

    @Test
    public void shouldBeAbleToLoadImageFromWebCam() throws Exception {
        // given
        WebcamFeed webcamFeed = new WebcamFeed();


        // when
        File file = webcamFeed.takePicture();

        // then
        log.info("File created: '{}'", file.getCanonicalPath());
        assertThat(file)
                .exists();

    }

    public double toArea(Dimension dimension) {
        return dimension.getWidth() * dimension.getHeight();
    }


}
