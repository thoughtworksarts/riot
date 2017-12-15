package uk.co.riot;

public class EmotionsRecord {
	private float anger;
	private float fear;
	private float calm;
	
	public EmotionsRecord(float anger, float fear, float calm) {
		this.anger = anger;
		this.fear = fear;
		this.calm = calm;
	}

	public float getAnger() {
		return anger;
	}

	public float getFear() {
		return fear;
	}

	public float getCalm() {
		return calm;
	}
	
	
}
