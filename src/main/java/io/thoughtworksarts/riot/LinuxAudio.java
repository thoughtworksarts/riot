package io.thoughtworksarts.riot;

import javafx.scene.media.AudioClip;

import static com.google.common.base.Preconditions.checkNotNull;

public class LinuxAudio {

    private final AudioClip audioClip;

    public LinuxAudio(String sourcefile) {
        checkNotNull(sourcefile);
        this.audioClip = new AudioClip(sourcefile);
    }


    public void playAudio() {
        this.audioClip.play();
    }

    public void stop() {
        this.audioClip.stop();
    }

    public boolean isPlaying() {
        return this.audioClip.isPlaying();
    }

}
