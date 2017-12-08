package uk.co.riot;

public class EmotionsRecord {
	private float mAnger;
	private float mFear;
	private float mCalm;
	
	public EmotionsRecord(float anger, float fear, float calm) {
		this.mAnger = anger;
		this.mFear = fear;
		this.mCalm = calm;
	}

	public float getAnger() {
		return mAnger;
	}

	public float getFear() {
		return mFear;
	}

	public float getCalm() {
		return mCalm;
	}
	
	
}
