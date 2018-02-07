package io.thoughtworksarts.riot.sound;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class AsioSound implements AsioDriverListener {

    @Getter private int bufferSize;
    private int sampleIndex;
    private float[] output;
    private double sampleRate;

    private AsioDriver asioDriver;
    private Set<AsioChannel> activeChannels;

    private final SimpleAudioPlayer audioPlayer;
    private final AsioDriverListener host = this;

    public AsioSound(SimpleAudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        activeChannels = new HashSet<>();
    }

    public void start() {
        if( asioDriver != null ){
            log.info("Start Asio Driver");
            asioDriver.start();
        }
    }

    public void shutdown(){
        if( asioDriver != null) {
            asioDriver.shutdownAndUnloadDriver();
            activeChannels.clear();
            asioDriver = null;
        }
    }

    public void initialize(String driverName, int numChannels, int newSampleRate) {
        asioDriver = AsioDriver.getDriver(driverName);
        asioDriver.addAsioDriverListener(host);
        for (int i = 0; i < numChannels; i++) {
            activeChannels.add(asioDriver.getChannelOutput(i));
        }
        sampleIndex = 0;
        bufferSize = asioDriver.getBufferPreferredSize();
        asioDriver.setSampleRate(newSampleRate);
        sampleRate = asioDriver.getSampleRate();
        output = new float[bufferSize];
        asioDriver.createBuffers(activeChannels);
    }

    @Override
    public void resyncRequest() { log.info("resyncRequest() callback received."); }

    @Override
    public void sampleRateDidChange(double sampleRate) { log.info("sampleRateDidChange() callback received."); }

    @Override
    public void bufferSizeChanged(int bufferSize) { log.info("bufferSizeChanged() callback received."); }

    @Override
    public void latenciesChanged(int inputLatency, int outputLatency) { log.info("latenciesChanged() callback received."); }

    @Override
    public void bufferSwitch(long sampleTime, long samplePosition, Set<AsioChannel> channels) {
        audioPlayer.bufferSwitch(sampleTime, samplePosition, activeChannels);
    }

    @Override
    public void resetRequest() {
        /*
         * This thread will attempt to shut down the ASIO driver. However, it will
         * block on the AsioDriver object at least until the current method has returned.
         */
        new Thread(() -> {
            log.info("resetRequest() callback received. Returning driver to INITIALIZED state.");
            asioDriver.returnToState(AsioDriverState.INITIALIZED);
        }).start();
    }

}
