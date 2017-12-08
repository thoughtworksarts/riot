package uk.co.riot;

import java.util.List;

public class Level {
	private String mId;
	private long mStart;
	private long mMeasure;
	private long mEnd;
	private List<Branch> mBranches;
	
	public Level(String id, long start, long measure, long end, List<Branch> branches) {
		this.mId = id;
		this.mStart = start;
		this.mEnd = end;
		this.mMeasure = measure;
		this.mBranches = branches;
	}

	public String getId() {
		return mId;
	}

	public long getStart() {
		return mStart;
	}

	public long getMeasure() {
		return mMeasure;
	}

	public long getEnd() {
		return mEnd;
	}

	public List<Branch> getBranches() {
		return mBranches;
	}
	
	
}
