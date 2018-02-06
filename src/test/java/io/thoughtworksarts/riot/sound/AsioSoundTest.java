package io.thoughtworksarts.riot.sound;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class AsioSoundTest {

    @Test
    public void testThatAsioCanMakeABeep() throws InterruptedException {
        AsioSound sound = new AsioSound();
        sound.start();
        TimeUnit.SECONDS.sleep(3);
        sound.stop();
    }
}
