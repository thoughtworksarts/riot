package io.thoughtworksarts.riot.audio;

import io.thoughtworksarts.riot.OSChecker;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RiotAudioPlayerTest {

    private RiotAudioPlayer audioPlayer;

    @BeforeEach
    void setUp() throws Exception {
        audioPlayer = OSChecker.isWindows() ? new AudioPlayer() : new JavaSoundAudioPlayer();
        String wavFile = this.getClass().getClassLoader().getResource("audio/audio.wav").getFile();
        audioPlayer.initialise("ASIO4ALL v2", wavFile);

    }

    @Ignore //Test that the other test works on the alien ware, if so - delete this test.
    public void asioDriverConnectorShouldPlayAudioFile() {
        AudioPlayer audioPlayer = new AudioPlayer();
        try {
            AsioDriverConnector driverConnector = new AsioDriverConnector(audioPlayer);
            driverConnector.start();
            audioPlayer.resume();
            TimeUnit.SECONDS.sleep(3);
            driverConnector.shutdown();
        }
        catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        audioPlayer.shutdown();
    }

    @Test
    void shouldPlayGivenFile() throws Exception {
        audioPlayer.resume();
        Thread.sleep(3000);
        audioPlayer.pause();
        Thread.sleep(3000);
        audioPlayer.seek(120);
        audioPlayer.resume();
        Thread.sleep(3000);
        audioPlayer.shutdown();
    }
}
