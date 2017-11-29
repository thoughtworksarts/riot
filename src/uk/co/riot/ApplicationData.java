package uk.co.riot;

import java.util.ArrayList;
import com.facialrecognition.FacialRecognitionWindowsAPI;
import com.facialrecognition.IFacialRecognitionAPI;
import com.synthbot.jasiohost.SimpleAudioPlayer;

import javafx.scene.media.MediaPlayer;


public class ApplicationData {
	
	private static ApplicationData singleton;
	
	private Config mConfig;
	private IFacialRecognitionAPI mFacialRecognitionAPI;
	private SimpleAudioPlayer mAudioPlayer;
	private MediaPlayer mVideoPlayer;
	private ArrayList<EmotionsRecord> mEmotionsRecords;
	
	private ApplicationData() {    	
    	mEmotionsRecords = new ArrayList<EmotionsRecord>();
	}
	
	public static ApplicationData getSingleton() {
		if(singleton == null) singleton = new ApplicationData();
		return singleton;
	}
	
	public Config getConfig() {
		return mConfig;
	}
	
	public void setConfig(Config config) {
		this.mConfig = config;
	}
	
	public SimpleAudioPlayer getAudioPlayer() {
		return mAudioPlayer;
	}
	
	public void setAudioPlayer(SimpleAudioPlayer player) {
		this.mAudioPlayer = player;
	}
	
	public MediaPlayer getVideoPlayer() {
		return mVideoPlayer;
	}
	
	public void setVideoPlayer(MediaPlayer player) {
		this.mVideoPlayer = player;
	}
	
	public IFacialRecognitionAPI getFacialRecognitionAPI() {
		return mFacialRecognitionAPI;
	}
	
	public void setFacialRecognitionAPI(IFacialRecognitionAPI api) {
		this.mFacialRecognitionAPI = api;
	}
	
	public ArrayList<EmotionsRecord> getEmotionsRecords() {
		return mEmotionsRecords;
	}
	
	public void addEmotionsRecord(EmotionsRecord record) {
		this.mEmotionsRecords.add(record);
	}
	
	public void clearEmotionsRecord() {
		this.mEmotionsRecords.clear();
	}
	
	public static void clearSingleton() {
		singleton = null;
	}
}
