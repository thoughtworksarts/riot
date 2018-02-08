package io.thoughtworksarts.riot.sound;

import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsioDriverConnectorTest {

    @Ignore
    public void asioDriverConnectorShouldPlayAudioFile() {
        SimpleAudioPlayer audioPlayer = new SimpleAudioPlayer();
        String wavFile = "src/main/resources/audio/audio.wav";
        try {
            audioPlayer.initialise("ASIO4ALL v2", wavFile);
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
}
