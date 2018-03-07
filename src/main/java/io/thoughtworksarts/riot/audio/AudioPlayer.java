package io.thoughtworksarts.riot.audio;

import com.synthbot.jasiohost.AsioChannel;

import java.util.Set;

public class AudioPlayer implements RiotAudioPlayer {

    private int bufferSize;
    private int sampleRate;
    private float[][] outputPerChannel;
    private float [] silentChannelBuffer;
    private WavSource source;
    private AsioDriverConnector asioDriverConnector;

    enum State {
        Paused,
        Playing,
    }

    State playbackState = State.Paused;

    // initialise the player with the specified driver name and wav file.
    // 
    // We load the wav file and determine the playback rate, number of channels and samples. 
    // We then initialise the driver with the playback rate and number of channels.
    //
    // After this, the system is primed and ready to start playing back the wav file at the beginning,
    // but will not actually start playing till we call resume().
    // 
    // Note: You MUST call shutdown after calling initialise - if you do not you will probably have to reboot your
    // PC! Because Asio drivers are awesome like that.
    // 
    // Note: Enumerate Asio driver instances like this: AsioDriver.getDriverNames().toArray()
    public void initialise(String driverName, String wavFile) throws Exception {
        source = new WavSource(wavFile);

        int numChannels = source.getNumChannels();
        sampleRate = (int)source.getSampleRate();

        asioDriverConnector = new AsioDriverConnector(this);
        asioDriverConnector.initialize(driverName, numChannels, sampleRate);

        bufferSize = asioDriverConnector.getBufferSize();

        outputPerChannel = new float[numChannels][bufferSize];
        silentChannelBuffer = new float[bufferSize];

        asioDriverConnector.start();
    }

    // Shut down the Asio driver. You MUST do this before closing the application, or you'll probably get no
    // audio output till you reboot your PC
    public void shutdown() {
        if (asioDriverConnector != null) {
            asioDriverConnector.shutdown();
        }
    }


    public void seek(double seekTimeSeconds) {
        source.seek(seekTimeSeconds);
    }
    
    public double currentTime() {
        return source.currentTime();
    }

    public void pause() {
        playbackState = State.Paused;
    }

    public void resume() {
        playbackState = State.Playing;
    }

    // Main function that handles fetching of data from the WavSource and passing it to the Asio driver
    public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels) {
            // If we're playing get data from the file
            if (playbackState == State.Playing) {
                source.readFrames(bufferSize, outputPerChannel);
                int index = 0;
                for (AsioChannel channelInfo : channels) {
                    float [] channel = outputPerChannel[index++];
                    channelInfo.write(channel);
                }
            } else { // Otherwise just output silence
                for (AsioChannel channelInfo : channels) {
                    channelInfo.write(silentChannelBuffer);
                }
            }
    }
}
