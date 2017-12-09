package io.toughtworksarts.riot.webcam;

import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WebCamFeedTest {

    @Test
    public void shouldBeAbleToLoadImageFromWebCam() throws Exception {
        // given
        Webcam webcam = Webcam.getDefault();
        File file = File.createTempFile("capture", "png");

        // when
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", file);

        // then
        log.info("File created: '{}'", file.getCanonicalPath());
        assertThat(file).exists();
    }

}
