package io.thoughtworksarts.riot.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;

public class JavaSoundAudioPlayer implements LineListener, RiotAudioPlayer {

  /**
   * this flag indicates whether the playback completes or not.
   */
  boolean playCompleted;

  private Clip audioClip;

  /**
   * Listens to the START and STOP events of the audio line.
   */
  @Override
  public void update(LineEvent event) {
    LineEvent.Type type = event.getType();

    if (type == LineEvent.Type.START) {
      System.out.println("Playback started.");

    } else if (type == LineEvent.Type.STOP) {
      playCompleted = true;
      System.out.println("Playback completed.");
    }

  }

  @Override
  public void pause() {
    audioClip.stop();
  }

  @Override
  public void resume() {
    audioClip.start();
  }

  @Override
  public void seek(double seconds) {
    audioClip.setMicrosecondPosition((long) seconds * 1000000);
  }

  @Override
  public void shutdown() {
    audioClip.close();
  }

  @Override
  public void initialise(String driverName, String audioPath) throws Exception {
    AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audioPath));

    AudioFormat format = audioStream.getFormat();

    DataLine.Info info = new DataLine.Info(Clip.class, format);

    audioClip = (Clip) AudioSystem.getLine(info);

    audioClip.addLineListener(this);

    audioClip.open(audioStream);
  }

}