package io.thoughtworksarts.riot.facialrecognition;

import io.thoughtworksarts.riot.IFacialRecognitionAPI;

public class MockFacialRecognitionAPI implements IFacialRecognitionAPI {
	float calm;
	float fear;
	float anger;
	
	public MockFacialRecognitionAPI(float calm, float fear, float anger) {
		setEmotions(calm,fear,anger);
	}
	
	public void setEmotions(float calm, float fear, float anger) {
		this.calm = calm;
		this.fear = fear;
		this.anger = anger;
	}

	@Override
	public void Initialise() {
	}

	@Override
	public void CaptureImage() {
	}

	@Override
	public float GetCalm() {
		return calm;
	}

	@Override
	public float GetFear() {
		return fear;
	}

	@Override
	public float GetAnger() {
		return anger;
	}

}