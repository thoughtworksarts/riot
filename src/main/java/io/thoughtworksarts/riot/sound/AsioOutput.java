package io.thoughtworksarts.riot.sound;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AsioOutput implements AsioDriverListener
{
    final AsioDriverListener host = this;
    private AsioDriver asioDriver;
    private Set<AsioChannel> activeChannels;
    private int sampleIndex;
    private int bufferSize;
    private double sampleRate;
    private float[] output;
    private IAudioFetchCallback audioCallback;
    
    public AsioOutput(IAudioFetchCallback callback)
    {
    	audioCallback = callback;
    	activeChannels = new HashSet<AsioChannel>();
    }
//
//    public void Initialise(String drivername, int numChannels, int newSampleRate)
//	{
//        asioDriver = AsioDriver.getDriver(drivername);
//        asioDriver.addAsioDriverListener(host);
//
//        for (int i = 0; i < numChannels; i++)
//        {
//            activeChannels.add(asioDriver.getChannelOutput(i));
//        }
//
//        sampleIndex = 0;
//        bufferSize = asioDriver.getBufferPreferredSize();
//        asioDriver.setSampleRate(newSampleRate);
//        sampleRate = asioDriver.getSampleRate();
//        output = new float[bufferSize];
//        asioDriver.createBuffers(activeChannels);
//	}
//
//    public void Start()
//    {
//        if (asioDriver != null)
//        {
//    	    System.out.println("Start() called.");
//
//    	    asioDriver.start();
//        }
//    }
    
//    public void Shutdown()
//    {
//        if (asioDriver != null)
//        {
//    	    System.out.println("Shutdown() called.");
//
//    	    asioDriver.shutdownAndUnloadDriver();
//            activeChannels.clear();
//            asioDriver = null;
//        }
//    }

    public int BufferSize()
    {
    	return bufferSize;
    }
    
    public void setSampleRate(int sampleRate)
    {
        asioDriver.setSampleRate((double)sampleRate);
    }
    
    public double SampleRate()
    {
    	return sampleRate;
    }
    
	@Override
	public void sampleRateDidChange(double sampleRate) 
	{
	    System.out.println("sampleRateDidChange() callback received.");
	}

	@Override
	public void resetRequest() 
	{
	    /*
	     * This thread will attempt to shut down the ASIO driver. However, it will
	     * block on the AsioDriver object at least until the current method has returned.
	     */
	    new Thread() {
	      @Override
	      public void run() {
	        System.out.println("resetRequest() callback received. Returning driver to INITIALIZED state.");
	        asioDriver.returnToState(AsioDriverState.INITIALIZED);
	      }
	    }.start();
	}

	@Override
	public void resyncRequest() {
	    System.out.println("resyncRequest() callback received.");
	}

	@Override
	public void bufferSizeChanged(int bufferSize) 
	{
	    System.out.println("bufferSizeChanged() callback received.");
	}

	@Override
	public void latenciesChanged(int inputLatency, int outputLatency) {
	    System.out.println("latenciesChanged() callback received.");
	}

	@Override
	public void bufferSwitch(long sampleTime, long samplePosition, Set<AsioChannel> activeChannels) {
		audioCallback.bufferSwitch(sampleTime, samplePosition, activeChannels);
	}
	
  public static List<String> getDriverNames() 
  {
	  return AsioDriver.getDriverNames();
  }
}
