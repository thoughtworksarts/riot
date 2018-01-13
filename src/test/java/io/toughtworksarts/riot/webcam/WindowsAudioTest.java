package io.toughtworksarts.riot.webcam;


import com.synthbot.jasiohost.ExampleHost;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
public class WindowsAudioTest {

    @Test
    public void shouldRunOnWindows() {
        // given
        log.info("Operating System: '{}'", SystemUtils.OS_NAME);
        assumeTrue(isWindows());

        // then
        String libraryPath = System.getProperty("java.library.path");
        log.info("Library Path: {}", libraryPath);
        assertThat(libraryPath).contains("asio");

        // then
        log.info("Windows");
    }


    public boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }
}
