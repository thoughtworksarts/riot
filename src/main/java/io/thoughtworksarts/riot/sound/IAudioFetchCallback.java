package io.thoughtworksarts.riot.sound;

import com.synthbot.jasiohost.AsioChannel;

import java.util.Set;

public interface IAudioFetchCallback
{
	  public void bufferSwitch(long sampleTime, long samplePosition, Set<AsioChannel> activeChannels);
}
