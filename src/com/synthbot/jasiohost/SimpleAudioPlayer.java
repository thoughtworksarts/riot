package com.synthbot.jasiohost;

import java.util.Set;

public class SimpleAudioPlayer implements IAudioFetchCallback
{
    private int bufferSize;
    private int sampleRate;
    private double[][] outputPerChannel;
    private double [] silentChannelBuffer;
    WavSource source;
    AsioOutput asioOutput = null;
    
    enum State
    {
        Paused,
        Playing,
    }
    
    State playbackState = State.Paused;

    public SimpleAudioPlayer()
    {
    }

    // Initialise the player with the specified driver name and wav file.
    // 
    // We load the wav file and determine the playback rate, number of channels and samples. 
    // We then initialise the driver with the playback rate and number of channels.
    //
    // After this, the system is primed and ready to start playing back the wav file at the beginning,
    // but will not actually start playing till we call Resume().
    // 
    // Note: You MUST call Shutdown after calling Initialise - if you do not you will probably have to reboot your
    // PC! Because Asio drivers are awesome like that.
    // 
    // Note: Enumerate Asio driver instances like this: AsioDriver.getDriverNames().toArray()
    public void Initialise(String driverName, String wavFile) throws Exception
    {
        source = new WavSource(wavFile);

        int numChannels = (int)source.getNumChannels();
        sampleRate = (int)source.getSampleRate();
        
        asioOutput = new AsioOutput(this);
        asioOutput.Initialise(driverName, numChannels, sampleRate);

        bufferSize = asioOutput.BufferSize();

        outputPerChannel = new double[numChannels][bufferSize];
        silentChannelBuffer = new double[bufferSize];

        asioOutput.Start();
    }

    // Shut down the Asio driver. You MUST do this before closing the application, or you'll probably get no
    // audio output till you reboot your PC
    public void Shutdown()
    {
        if (asioOutput != null)
        {
            asioOutput.Shutdown();
        }
    }

    // Seek to the current position, in seconds
    public void Seek(double seekTimeSeconds)
    {
        source.Seek(seekTimeSeconds);
    }
    
    // Get the current playback position, in seconds
    public double CurrentTime()
    {
        return source.CurrentTime();
    }
    
    // Pause playback
    public void Pause()
    {
        playbackState = State.Paused;
    }
    
    // Resume playback
    public void Resume()
    {
        playbackState = State.Playing;
    }

    // Main function that handles fetching of data from the WavSource and passing it to the Asio driver
    public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels)
    {
        // If we're playing get data from the file
        if (playbackState == State.Playing)
        {
            int numFramesRead = source.ReadFrames(bufferSize, outputPerChannel);
            
            //System.out.printf("bufferSwitch() returned %d bytes.\r\n", numFramesRead);
            
            int index = 0;
            for (AsioChannel channelInfo : channels)
            {
                double [] channel = outputPerChannel[index++];
    
                channelInfo.write(channel);
            }
        }
        // Otherwise just output silence
        else
        {
            for (AsioChannel channelInfo : channels)
            {
                channelInfo.write(silentChannelBuffer);
            }
        }
    }
}
