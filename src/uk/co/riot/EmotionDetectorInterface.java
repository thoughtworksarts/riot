package uk.co.riot;

public interface EmotionDetectorInterface {
	void startMeasure();
	void stopMeasure();
	String getEmotion();
}
