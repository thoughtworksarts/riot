package io.toughtworksarts.riot.webcam;


import com.synthbot.jasiohost.ExampleHost;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
public class WindowsAudioTest {

    @Test
    public void shouldRunOnWindows() {
        // given
        assumeTrue(isWindows());

        // then
        assertThat(System.getProperty("java.library.path")).isEqualTo("audio");

        // then
        log.info("Windows");
    }

    public boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }
}
