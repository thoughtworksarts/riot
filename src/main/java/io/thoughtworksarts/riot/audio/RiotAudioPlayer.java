package io.thoughtworksarts.riot.audio;

public interface RiotAudioPlayer {

  void pause();

  void resume();

  void seek(double seconds);

  void shutdown();

  void initialise(String driverName, String audioPath) throws Exception;
}
