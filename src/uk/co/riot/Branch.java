package uk.co.riot;

public class Branch {
	private String mId;
	private long mStart;
	private long mEnd;
	private String mEmotion;
	private String mOutcome;
	
	public Branch(String id, long start, long end, String emotion, String outcome) {
		this.mId = id;
		this.mStart = start;
		this.mEnd = end;
		this.mEmotion = emotion;
		this.mOutcome = outcome;
	}
	
	public String getId() {
		return mId;
	}

	public long getStart() {
		return mStart;
	}

	public long getEnd() {
		return mEnd;
	}

	public String getEmotion() {
		return mEmotion;
	}

	public String getOutcome() {
		return mOutcome;
	}	
}
