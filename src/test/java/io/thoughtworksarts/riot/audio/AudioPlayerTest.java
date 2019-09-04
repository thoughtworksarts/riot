package io.thoughtworksarts.riot.audio;

import io.thoughtworksarts.riot.utilities.OSChecker;
import org.junit.jupiter.api.Test;

class AudioPlayerTest {

    private RiotAudioPlayer audioPlayer;

    @Test
    void shouldPlayGivenFile() throws Exception {
        boolean isWindows = OSChecker.isWindows();
        audioPlayer = isWindows ? new AudioPlayer() : new JavaSoundAudioPlayer();
        String wavFile = this.getClass().getClassLoader().getResource("audio/audio.wav").getFile();
        audioPlayer.initialise("ASIO4ALL v2", wavFile);
        playAudio();
        audioPlayer.shutdown();
    }

    @Test
    private void playAudio() throws InterruptedException {
        audioPlayer.resume();
        Thread.sleep(3000);
        audioPlayer.pause();
        Thread.sleep(3000);
        audioPlayer.seek(120);
        audioPlayer.resume();
        Thread.sleep(3000);
    }
}
