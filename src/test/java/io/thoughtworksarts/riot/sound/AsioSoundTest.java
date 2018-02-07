package io.thoughtworksarts.riot.sound;

import org.junit.Ignore;

import java.util.concurrent.TimeUnit;

public class AsioSoundTest {

    @Ignore
    public void testThatAsioCanMakeABeep() throws InterruptedException {
        AsioSound sound = new AsioSound();
        sound.start();
        TimeUnit.SECONDS.sleep(3);
        sound.stop();
    }
}
