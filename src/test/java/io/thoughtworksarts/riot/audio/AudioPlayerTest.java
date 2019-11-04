package io.thoughtworksarts.riot.audio;
import io.thoughtworksarts.riot.utilities.OSChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AudioPlayerTest {

    private RiotAudioPlayer audioPlayer;

    @Disabled
    @Test
    void shouldPlayGivenFile() throws Exception {
        boolean isWindows = OSChecker.isWindows();
        audioPlayer = isWindows ? new AudioPlayer() : new JavaSoundAudioPlayer();
        String wavFile = this.getClass().getClassLoader().getResource("audio/audio_test.wav").getFile();
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
        audioPlayer.seek(50);
        audioPlayer.resume();
        Thread.sleep(3000);
    }
}
