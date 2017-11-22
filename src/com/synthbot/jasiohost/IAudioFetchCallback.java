package com.synthbot.jasiohost;

import java.util.Set;

public interface IAudioFetchCallback
{
	  public void bufferSwitch(long sampleTime, long samplePosition, Set<AsioChannel> activeChannels);
}
