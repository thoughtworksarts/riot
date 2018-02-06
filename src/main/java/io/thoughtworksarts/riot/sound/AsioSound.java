package io.thoughtworksarts.riot.sound;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class AsioSound implements AsioDriverListener {

    private AsioDriver asioDriver;
    private int bufferSize;
    private int sampleIndex;
    private float[] output;
    private double sampleRate;
    private Set<AsioChannel> activeChannels = new HashSet<>();

    private final AsioDriverListener host = this;

    public void start() {
        List<String> driverNames = AsioDriver.getDriverNames();
        AsioDriver asioDriver = initDriver("ASIO4ALL v2");

        asioDriver.start();
    }

    private AsioDriver initDriver(String driverName) {
        asioDriver = AsioDriver.getDriver(driverName);
        asioDriver.addAsioDriverListener(host);
        activeChannels.add(asioDriver.getChannelOutput(0));
        activeChannels.add(asioDriver.getChannelOutput(1));
        sampleIndex = 0;
        bufferSize = asioDriver.getBufferPreferredSize();
        sampleRate = asioDriver.getSampleRate();
        output = new float[bufferSize];
        asioDriver.createBuffers(activeChannels);
        return asioDriver;
    }


    public void stop(){
        if( asioDriver != null) {
            asioDriver.stop();
            asioDriver.shutdownAndUnloadDriver();
            activeChannels.clear();
        }
    }


    @Override
    public void resyncRequest() {
        System.out.println("resyncRequest() callback received.");
    }

    @Override
    public void sampleRateDidChange(double sampleRate) {
        System.out.println("sampleRateDidChange() callback received.");
    }

    @Override
    public void resetRequest() {
        /*
         * This thread will attempt to shut down the ASIO driver. However, it will
         * block on the AsioDriver object at least until the current method has returned.
         */
        new Thread(() -> {
            System.out.println("resetRequest() callback received. Returning driver to INITIALIZED state.");
            asioDriver.returnToState(AsioDriverState.INITIALIZED);
        }).start();
    }

    @Override
    public void bufferSizeChanged(int bufferSize) {
        System.out.println("bufferSizeChanged() callback received.");
    }

    @Override
    public void latenciesChanged(int inputLatency, int outputLatency) {
        System.out.println("latenciesChanged() callback received.");
    }

    @Override
    public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels) {
        for (int i = 0; i < bufferSize; i++, sampleIndex++) {
            output[i] = (float) Math.sin(2 * Math.PI * sampleIndex * 440.0 / sampleRate);
        }
        for (AsioChannel channelInfo : channels) {
            channelInfo.write(output);
        }
    }

}
