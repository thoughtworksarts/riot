package io.thoughtworksarts.riot.sound;

import lombok.Getter;

import java.io.File;
import java.io.IOException;

// Simple class to handle audio playback from a wav file
public class WavSource {

	private int numFrames;
	private float[] buffer;
	private int readCurrentFrame;

	@Getter private int numChannels;
	@Getter private long sampleRate;

	WavSource(String path) throws IOException, WavFileException {
		// Open the wav file specified as the first argument
		WavFile wavFile;

		wavFile = WavFile.openWavFile(new File(path));
		// Display information about the wav file
		wavFile.display();

		// Get the number of audio channels in the wav file
		numChannels = wavFile.getNumChannels();
		
		numFrames = (int)wavFile.getNumFrames();
		
		sampleRate = wavFile.getSampleRate();
		    
		buffer = new float[numFrames * numChannels];

		readCurrentFrame = 0;
		
		// Read frames into buffer
		wavFile.readFrames(buffer, numFrames);

		// Close the wavFile
		wavFile.close();

		// Output the minimum and maximum value
		System.out.printf("Channels: %d Frames: %d\n", numChannels, numFrames);
	}

	public int readFrames(int numFramesRequested, float[][] outBuffer) {
		int numFramesLeft = numFrames - readCurrentFrame;
		int numFramesToRead = Math.min(numFramesLeft, numFramesRequested);

		for (int i = 0; i < numFramesToRead; i++) {
		    for (int c = 0; c < numChannels; c++) {
		        int readPosition = (readCurrentFrame + i) * numChannels + c;
	            outBuffer[c][i] = buffer [readPosition];
		    }
		}
		
		int numFramesSilence = numFramesRequested - numFramesLeft;

		for (int i = 0; i < numFramesSilence; i++) {
            for (int c = 0; c < numChannels; c++) {
                outBuffer[c][numFramesToRead+i] = 0;
            }
        }
		
		readCurrentFrame += numFramesToRead;
		return numFramesToRead;
	}
	
    public void seek(double seekTimeSeconds) {
        int seekTimeFrames = (int)(seekTimeSeconds * sampleRate);
        if (seekTimeFrames < 0) {
            seekTimeFrames = 0;
        }
        if (seekTimeFrames > numFrames) {
            seekTimeFrames = numFrames;
        }
        readCurrentFrame = seekTimeFrames;
    }
    
    public double currentTime()
    {
        return (double)readCurrentFrame/(double)sampleRate;
    }

    
}
