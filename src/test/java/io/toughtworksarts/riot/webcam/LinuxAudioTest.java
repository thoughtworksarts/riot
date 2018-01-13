package io.toughtworksarts.riot.webcam;

import io.thoughtworksarts.riot.LinuxAudio;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class LinuxAudioTest {

    private boolean linuxOrMac;

    @Test
    public void shouldPlayFile() throws Exception {
        // given
        assumeTrue(this.isLinuxOrMac());
        File testAudioFile = getTestAudioFile("audio.wav");
        String source = testAudioFile.toURI().toURL().toString();
        LinuxAudio linuxAudio = new LinuxAudio(source);

        // when
        linuxAudio.playAudio();


        // then
        assertThat(linuxAudio.isPlaying()).isTrue();

        int count = 0;
        while (linuxAudio.isPlaying() && count < 5) {
            Thread.sleep(1000);
            count++;
        }

    }


    private File getTestAudioFile(String s) {
        String fileName = Objects.requireNonNull(Thread.currentThread()
                .getContextClassLoader()
                .getResource(s))
                .getFile();

        return new File(fileName);
    }

    public boolean isLinuxOrMac() {
        return SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC;
    }
}
