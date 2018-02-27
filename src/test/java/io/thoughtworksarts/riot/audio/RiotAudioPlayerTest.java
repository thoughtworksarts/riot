package io.thoughtworksarts.riot.audio;

import io.thoughtworksarts.riot.OSChecker;
import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RiotAudioPlayerTest {

    @Ignore
    void shouldPlayGivenFile() throws Exception {

        boolean isWindows = OSChecker.isWindows();
        RiotAudioPlayer  audioPlayer = isWindows ? new AudioPlayer() : new JavaSoundAudioPlayer();
        String wavFile = this.getClass().getClassLoader().getResource("audio/audio.wav").getFile();
        audioPlayer.initialise("ASIO4ALL v2", wavFile);

        if( isWindows ){
            try {
                AsioDriverConnector driverConnector = new AsioDriverConnector((AudioPlayer) audioPlayer);
                driverConnector.start();
                audioPlayer.resume();
                TimeUnit.SECONDS.sleep(3);
                driverConnector.shutdown();
            }
            catch (Exception e) {
                e.printStackTrace();
                assertTrue(false);
            }
        } else {
            audioPlayer.resume();
            Thread.sleep(3000);
            audioPlayer.pause();
            Thread.sleep(3000);
            audioPlayer.seek(120);
            audioPlayer.resume();
            Thread.sleep(3000);
        }
        audioPlayer.shutdown();
    }
}
