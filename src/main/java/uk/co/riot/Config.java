package uk.co.riot;

import java.util.List;

public class Config {
	
	private List<Level> mLevels;	
	
	private String mVideoFilepath;
	
	private String mAudioFilepath;
	
	public Config(List<Level> levels, String videoFilepath, String audioFilepath) {
		this.mLevels = levels;
		this.mVideoFilepath = videoFilepath;
		this.mAudioFilepath = audioFilepath;
	}
	
	public List<Level> getLevels() {
		return this.mLevels;
	}
	
	public String getVideoFilepath() {
		return mVideoFilepath;
	}
	
	public String getAudioFilepath() {
		return mAudioFilepath;
	}
}
